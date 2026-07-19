package technology.positivehome.ihome.security.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import technology.positivehome.ihome.model.runtime.module.ModuleConfigEntry;
import technology.positivehome.ihome.security.model.user.Role;
import technology.positivehome.ihome.security.util.IHomeApiTargetAccessType;
import technology.positivehome.ihome.security.util.IHomeApiTargetType;
import technology.positivehome.ihome.server.persistence.ModuleConfigRepository;

import java.io.Serializable;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Central authority for resolving permissions based on roles and target types.
 * <p>
 * This is the single source of truth for "who can do what" in the iHome system.
 * All authorization checks across REST API controllers, MCP tools, and
 * method security expressions should delegate here.
 * <p>
 * Permission model:
 * <ul>
 *   <li><b>ADMIN</b> — full access to all targets (READ, WRITE, CREATE, DELETE)</li>
 *   <li><b>SUPERVISOR</b> — can READ everything; can WRITE to MODULE (enable/disable power to lights, garage doors, sliding gates, etc.)</li>
 *   <li><b>CHILDREN_ROOM1_MANAGER</b> — can READ everything; WRITE reserved for future room-scoped control</li>
 *   <li><b>CHILDREN_ROOM2_MANAGER</b> — same as above</li>
 *   <li><b>AUTHORIZED_GUEST</b> — READ only on non-sensitive targets</li>
 *   <li><b>UNDEFINED</b> — no permissions</li>
 * </ul>
 */
@Service
public class PermissionService {

    private static final Logger log = LoggerFactory.getLogger(PermissionService.class);

    private final ModuleConfigRepository moduleConfigRepository;

    public PermissionService(ModuleConfigRepository moduleConfigRepository) {
        this.moduleConfigRepository = moduleConfigRepository;
    }

    /**
     * Checks whether the authenticated user has the specified permission on the target type.
     * <p>
     * When {@code targetType == MODULE} and {@code ids} contains a valid module ID,
     * this delegates to {@link #hasModulePermission(Authentication, long, IHomeApiTargetAccessType)}
     * which evaluates per-module writer role names from the database.
     *
     * @param authentication the authenticated user
     * @param targetType     the type of resource being accessed (e.g., MODULE, USER)
     * @param accessType     the type of access requested (e.g., READ, WRITE)
     * @param ids            optional resource IDs — when targetType is MODULE, the first
     *                       non-null ID is used as the module ID for per-module permission checks
     * @return true if the user has the required permission
     */
    public boolean hasPermission(Authentication authentication,
                                 IHomeApiTargetType targetType,
                                 IHomeApiTargetAccessType accessType,
                                 Serializable[] ids) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Set<Role> userRoles = extractRoles(authentication.getAuthorities());

        // ADMIN has full access to everything
        if (userRoles.contains(Role.ADMIN)) {
            return true;
        }

        // When target is a MODULE and we have a resource ID, use per-module permission check
        if (targetType == IHomeApiTargetType.MODULE
                && ids != null && ids.length > 0 && ids[0] instanceof Number) {
            long moduleId = ((Number) ids[0]).longValue();
            return hasModulePermission(authentication, moduleId, accessType);
        }

        // Resolve permission based on role, target, and access type
        for (Role role : userRoles) {
            if (hasPermissionForRole(role, targetType, accessType, ids)) {
                return true;
            }
        }

        log.debug("Permission DENIED: user '{}' (roles={}) cannot {} on {}",
                authentication.getName(), userRoles, accessType, targetType);
        return false;
    }

    /**
     * Checks whether the authenticated user has the specified permission on a domain object.
     * Currently delegates to the type-based check for simplicity.
     *
     * @param authentication    the authenticated user
     * @param targetDomainObject the domain object being accessed
     * @param accessType        the type of access requested
     * @return true if the user has the required permission
     */
    public boolean hasPermission(Authentication authentication,
                                 Object targetDomainObject,
                                 IHomeApiTargetAccessType accessType) {
        // For now, domain-object-level checks are resolved via type-based logic.
        // Future: can inspect the object type (e.g., ModuleSummary) to infer target type.
        return hasPermission(authentication,
                IHomeApiTargetType.UNDEFINED,
                accessType,
                new Serializable[]{});
    }

    /**
     * Returns the set of IHomeApiTargetAccessType permissions a user has for a given target type.
     * Useful for the AI chat system prompt to tell the LLM what the user can do.
     */
    public Set<IHomeApiTargetAccessType> getPermissionsForTarget(Authentication authentication,
                                                                  IHomeApiTargetType targetType) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return Set.of();
        }

        Set<Role> userRoles = extractRoles(authentication.getAuthorities());
        EnumSet<IHomeApiTargetAccessType> result = EnumSet.noneOf(IHomeApiTargetAccessType.class);

        for (Role role : userRoles) {
            result.addAll(getPermissionsForRole(role, targetType));
        }

        return result;
    }

    /**
     * Checks if the user has WRITE permission on any target.
     * Used by the AI chat to determine whether to show "control" tools.
     */
    public boolean hasAnyWritePermission(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        Set<Role> userRoles = extractRoles(authentication.getAuthorities());
        for (Role role : userRoles) {
            if (role == Role.ADMIN || role == Role.SUPERVISOR) {
                return true;
            }
        }
        return false;
    }

    /** Module assignment types that SUPERVISOR is allowed to write to (power on/off). */
    private static final Set<String> SUPERVISOR_CONTROLLABLE_ASSIGNMENTS = Set.of(
            "LIGHT_CONTROL",
            "EXT_LIGHT_CONTROL",
            "GATE_CONTROL"
    );

    /**
     * Checks whether the user can control a module of the given assignment type.
     * <p>
     * ADMIN can control any module type. SUPERVISOR can only control types
     * listed in {@link #SUPERVISOR_CONTROLLABLE_ASSIGNMENTS}. Other roles
     * cannot control any modules.
     *
     * @param auth           the authenticated user
     * @param assignmentName the ModuleAssignment enum name (e.g., "GATE_CONTROL", "HEATING_CONTROL")
     * @return true if the user has write permission on this module type
     */
    public boolean canControlModuleAssignment(Authentication auth, String assignmentName) {
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        Set<Role> roles = extractRoles(auth.getAuthorities());
        if (roles.contains(Role.ADMIN)) {
            return true; // ADMIN can control any module type
        }
        if (roles.contains(Role.SUPERVISOR)) {
            return SUPERVISOR_CONTROLLABLE_ASSIGNMENTS.contains(assignmentName);
        }
        return false;
    }

    /**
     * Returns a human-readable description of which module types the user can control.
     * Used to dynamically build the AI system prompt so the LLM knows the user's scope.
     *
     * @param auth the authenticated user
     * @return a string like "lights, external lights, garage doors, sliding gates"
     */
    public String getControllableModulesDescription(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return "none";
        }
        Set<Role> roles = extractRoles(auth.getAuthorities());
        if (roles.contains(Role.ADMIN)) {
            return "all module types (lights, gates, heating, power supply, ventilation, etc.)";
        }
        if (roles.contains(Role.SUPERVISOR)) {
            return "lights, external lights, garage doors, sliding gates";
        }
        return "none (read-only access)";
    }

    /**
     * Checks whether the user has the specified permission on a specific module
     * by consulting the per-module writer role name list stored in the {@link ModuleConfigEntry}.
     * <p>
     * <b>WRITE access:</b> If the module has a non-empty list of writer role names,
     * only roles appearing in that list are granted WRITE access. If the list
     * is empty, WRITE access falls back to system-wide role defaults.
     * <p>
     * <b>READ access:</b> Always determined by system-wide role defaults,
     * regardless of the per-module writer-role list. The per-module list only governs
     * WRITE access.
     * <p>
     * <b>ADMIN</b> always has full access and bypasses all per-module checks.
     *
     * @param auth       the authenticated user
     * @param moduleId   the module ID to check access for
     * @param accessType the type of access requested (READ or WRITE)
     * @return true if the user has the required permission on this module
     */
    public boolean hasModulePermission(Authentication auth,
                                       long moduleId,
                                       IHomeApiTargetAccessType accessType) {
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        Set<Role> userRoles = extractRoles(auth.getAuthorities());
        if (userRoles.contains(Role.ADMIN)) {
            return true;
        }

        // Load per-module writer role names from DB (roles that have WRITE access)
        ModuleConfigEntry entry = moduleConfigRepository.getModuleConfigEntry(moduleId);
        List<Role> writerRoleNames = (entry != null)
                ? entry.getWriterRoleNames()
                : List.of();

        if (!writerRoleNames.isEmpty()) {
            if (accessType == IHomeApiTargetAccessType.WRITE) {
                // WRITE access requires the user's role to be explicitly listed
                boolean allowed = userRoles.stream().anyMatch(writerRoleNames::contains);

                if (!allowed) {
                    log.debug("Module WRITE permission DENIED by per-module writer role list: user '{}' (roles={}) on module {}",
                            auth.getName(), userRoles, moduleId);
                }
                return allowed;
            }
            // READ access falls through to system-wide defaults below
        }

        // Fall back to system-wide role defaults (for READ or when writerRoleNames is empty)
        boolean allowed = hasPermissionForAnyRole(userRoles, IHomeApiTargetType.MODULE, accessType);
        if (!allowed) {
            log.debug("Module permission DENIED by fallback rules: user '{}' (roles={}) cannot {} on module {}",
                    auth.getName(), userRoles, accessType, moduleId);
        }
        return allowed;
    }

    // ---- Role-to-permission mapping ----

    /**
     * Checks if the user has the given permission across any of their roles.
     * Used as a fallback when no per-module permissions are defined.
     */
    private boolean hasPermissionForAnyRole(Set<Role> userRoles,
                                            IHomeApiTargetType targetType,
                                            IHomeApiTargetAccessType accessType) {
        for (Role role : userRoles) {
            if (hasPermissionForRole(role, targetType, accessType, new Serializable[0])) {
                return true;
            }
        }
        return false;
    }

    private boolean hasPermissionForRole(Role role,
                                         IHomeApiTargetType targetType,
                                         IHomeApiTargetAccessType accessType,
                                         Serializable[] ids) {
        return switch (role) {
            case ADMIN -> true; // handled above, but kept for completeness

            case SUPERVISOR -> switch (targetType) {
                case MODULE -> accessType == IHomeApiTargetAccessType.READ
                        || accessType == IHomeApiTargetAccessType.WRITE;
                case USER -> accessType == IHomeApiTargetAccessType.READ;
                case SYSTEM -> accessType == IHomeApiTargetAccessType.READ;
                case UNDEFINED -> accessType == IHomeApiTargetAccessType.READ;
            };

            case CHILDREN_ROOM1_MANAGER, CHILDREN_ROOM2_MANAGER -> switch (targetType) {
                case MODULE -> accessType == IHomeApiTargetAccessType.READ;
                // Future: could add WRITE scoped to specific module IDs passed via 'ids' parameter
                case USER -> accessType == IHomeApiTargetAccessType.READ;
                case SYSTEM -> accessType == IHomeApiTargetAccessType.READ;
                case UNDEFINED -> accessType == IHomeApiTargetAccessType.READ;
            };

            case AUTHORIZED_GUEST -> switch (targetType) {
                case MODULE -> accessType == IHomeApiTargetAccessType.READ;
                case SYSTEM -> accessType == IHomeApiTargetAccessType.READ;
                default -> false;
            };

            case UNDEFINED -> false;
        };
    }

    private Set<IHomeApiTargetAccessType> getPermissionsForRole(Role role, IHomeApiTargetType targetType) {
        EnumSet<IHomeApiTargetAccessType> all = EnumSet.allOf(IHomeApiTargetAccessType.class);
        EnumSet<IHomeApiTargetAccessType> readOnly = EnumSet.of(IHomeApiTargetAccessType.READ);

        return switch (role) {
            case ADMIN -> all;
            case SUPERVISOR -> switch (targetType) {
                case MODULE -> EnumSet.of(IHomeApiTargetAccessType.READ, IHomeApiTargetAccessType.WRITE);
                case USER, SYSTEM, UNDEFINED -> readOnly;
            };
            case CHILDREN_ROOM1_MANAGER, CHILDREN_ROOM2_MANAGER -> readOnly;
            case AUTHORIZED_GUEST -> switch (targetType) {
                case MODULE, SYSTEM -> readOnly;
                default -> EnumSet.noneOf(IHomeApiTargetAccessType.class);
            };
            case UNDEFINED -> EnumSet.noneOf(IHomeApiTargetAccessType.class);
        };
    }

    private Set<Role> extractRoles(Collection<? extends GrantedAuthority> authorities) {
        EnumSet<Role> roles = EnumSet.noneOf(Role.class);
        for (GrantedAuthority authority : authorities) {
            String auth = authority.getAuthority();
            if (auth.startsWith("ROLE_")) {
                try {
                    Role role = Role.valueOf(auth.substring(5));
                    roles.add(role);
                } catch (IllegalArgumentException e) {
                    // Unknown role, skip
                }
            }
        }
        return roles;
    }
}
