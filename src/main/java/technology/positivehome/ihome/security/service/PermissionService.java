package technology.positivehome.ihome.security.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import technology.positivehome.ihome.model.constant.ModuleOperationMode;
import technology.positivehome.ihome.model.constant.ModuleStartupMode;
import technology.positivehome.ihome.model.runtime.module.ModuleConfigEntry;
import technology.positivehome.ihome.security.auth.JwtAuthenticationToken;
import technology.positivehome.ihome.security.model.permissionproc.MooduleUpdateRequest;
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
 *   <li><b>AUTHORIZED_GUEST</b> — no MODULE or SYSTEM permissions via PermissionService.
 *       Their data access is limited to unauthenticated GuestController endpoints
 *       (outdoor temperature, pressure, external power voltage, external power summary).</li>
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
     * Checks if the user has WRITE permission on at least one module.
     * <p>
     * ADMIN always qualifies — they can write every module.
     * SUPERVISOR qualifies only if at least one module with a SUPERVISOR-controllable
     * assignment type ({@link #SUPERVISOR_CONTROLLABLE_ASSIGNMENTS}) exists in the system.
     * CHILDREN_ROOM1_MANAGER, CHILDREN_ROOM2_MANAGER, and other per-module roles qualify
     * if they appear as a writer in any module's permission column.
     * All other roles are denied.
     * <p>
     * Used by the AI chat layer to determine whether to expose "control" tools to the user.
     */
    public boolean hasAnyWritePermission(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        Set<Role> userRoles = extractRoles(authentication.getAuthorities());
        if (userRoles.contains(Role.ADMIN)) {
            return true; // ADMIN can write every module unconditionally
        }
        if (userRoles.contains(Role.SUPERVISOR)) {
            // SUPERVISOR is restricted to specific assignment types; only grant access when
            // at least one such module actually exists in the system
            return moduleConfigRepository.hasAnyModuleWithAssignments(SUPERVISOR_CONTROLLABLE_ASSIGNMENTS);
        }
        // For per-module-assigned roles (CHILDREN_ROOM1_MANAGER, CHILDREN_ROOM2_MANAGER, etc.),
        // check if any module has this role in its explicit writer role list.
        for (Role role : userRoles) {
            if (role != Role.UNDEFINED && role != Role.AUTHORIZED_GUEST) {
                if (moduleConfigRepository.hasAnyModuleWithWriterRole(role.name())) {
                    return true;
                }
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
        // Check per-module writer roles (e.g. CHILDREN_ROOM1_MANAGER)
        for (Role role : roles) {
            if (role != Role.UNDEFINED && role != Role.AUTHORIZED_GUEST) {
                if (moduleConfigRepository.hasAnyModuleWithWriterRole(role.name())) {
                    return "specific modules assigned to " + role.name();
                }
            }
        }
        return "none (read-only access)";
    }

    /**
     * Checks whether the user has the specified permission on a specific module.
     * <p>
     * <b>WRITE access logic:</b>
     * <ol>
     *   <li>ADMIN always has access.</li>
     *   <li>If the module has a non-empty {@code writerRoleNames} list, only roles in that list
     *       are granted WRITE access (explicit per-module override).</li>
     *   <li>If {@code writerRoleNames} is empty, WRITE access falls back to
     *       {@link #canControlModuleAssignment} — SUPERVISOR can write only to permitted
     *       assignment types (lights, gates, etc.), all other roles are denied.</li>
     * </ol>
     * <p>
     * <b>READ access</b> is always determined by system-wide role defaults.
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

        // Load per-module config from DB (writer roles + assignment type)
        ModuleConfigEntry entry = moduleConfigRepository.getModuleConfigEntry(moduleId);
        List<Role> writerRoleNames = (entry != null)
                ? entry.getWriterRoleNames()
                : List.of();

        if (accessType == IHomeApiTargetAccessType.WRITE) {
            if (!writerRoleNames.isEmpty()) {
                // Explicit per-module writer list — user must appear in it
                boolean allowed = userRoles.stream().anyMatch(writerRoleNames::contains);
                if (!allowed) {
                    log.debug("Module WRITE permission DENIED by per-module writer role list: user '{}' (roles={}) on module {}",
                            auth.getName(), userRoles, moduleId);
                }
                return allowed;
            }
            // No explicit writer list — fall back to assignment-type-based check.
            // SUPERVISOR can only control specific assignment types (lights, gates, etc.).
            if (entry == null || entry.getModuleAssignment() == null) {
                log.debug("Module WRITE permission DENIED: module {} has no assignment configured", moduleId);
                return false;
            }
            return canControlModuleAssignment(auth, entry.getModuleAssignment().name());
        }

        // READ access: use system-wide role defaults
        boolean allowed = hasPermissionForAnyRole(userRoles, IHomeApiTargetType.MODULE, accessType);
        if (!allowed) {
            log.debug("Module READ permission DENIED by fallback rules: user '{}' (roles={}) on module {}",
                    auth.getName(), userRoles, moduleId);
        }
        return allowed;
    }

    /**
     * Checks whether the authenticated user has permission to update a module via a
     * {@link MooduleUpdateRequest}.
     * <p>
     * This method enforces a two-tier permission model:
     * <ol>
     *   <li><b>Generic WRITE access</b> — the user must have WRITE permission on the module
     *       (as determined by {@link #hasModulePermission(Authentication, long, IHomeApiTargetAccessType)}).
     *       This covers changes such as power on/off (output value).</li>
     *   <li><b>Mode / Startup-mode changes</b> — changing the module's operation mode
     *       ({@link ModuleOperationMode}) or startup mode ({@link ModuleStartupMode})
     *       is restricted to <b>ADMIN</b> only. Non-ADMIN users with generic WRITE access
     *       may only perform output-value changes (e.g. turning the module on or off),
     *       but cannot change the mode from the current values.</li>
     * </ol>
     *
     * @param authenticationToken the authenticated JWT token
     * @param req                 the module update request containing the target module ID,
     *                            desired mode (moduleActive), startup mode (enableOnStartup),
     *                            and output value
     * @return {@code true} if the user is authorized to perform the requested update;
     *         {@code false} otherwise
     */
    public boolean hasModulePermission(JwtAuthenticationToken authenticationToken, MooduleUpdateRequest req) {
        boolean genericWritePermission = hasModulePermission(authenticationToken, req.moduleId(), IHomeApiTargetAccessType.WRITE);
        if (!genericWritePermission) {
            return false;
        }

        // ADMIN is allowed to change any field, including mode and startup mode
        Set<Role> roles = extractRoles(authenticationToken.getAuthorities());
        if (roles.contains(Role.ADMIN)) {
            return true;
        }

        // Non-ADMIN users with WRITE permission may only change the output value
        // (power on/off). Changing the module operation mode or startup mode is
        // ADMIN-only.
        ModuleConfigEntry entry = moduleConfigRepository.getModuleConfigEntry(req.moduleId());
        if (entry != null) {
            ModuleOperationMode newMode = req.moduleActive() ? ModuleOperationMode.AUTO : ModuleOperationMode.MANUAL;
            ModuleStartupMode newStartupMode = req.enableOnStartup() ? ModuleStartupMode.ENABLED : ModuleStartupMode.DISABLED;

            if (!newMode.equals(entry.getMode())) {
                log.debug("Module mode change DENIED for non-ADMIN user '{}' (roles={}) on module {}",
                        authenticationToken.getName(), roles, req.moduleId());
                return false;
            }
            if (!newStartupMode.equals(entry.getStartupMode())) {
                log.debug("Module startup mode change DENIED for non-ADMIN user '{}' (roles={}) on module {}",
                        authenticationToken.getName(), roles, req.moduleId());
                return false;
            }
        }

        // Only output value changes — allowed for any user with generic WRITE permission
        return true;
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
                // AUTHORIZED_GUEST has no MODULE or SYSTEM access via PermissionService.
                // Their data access is limited to GuestController endpoints
                // (outdoor temp, pressure, external power voltage, external power summary)
                // which are unauthenticated and bypass this check entirely.
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
            case AUTHORIZED_GUEST -> EnumSet.noneOf(IHomeApiTargetAccessType.class);
            case UNDEFINED -> EnumSet.noneOf(IHomeApiTargetAccessType.class);
        };
    }

    public Set<Role> extractRoles(Collection<? extends GrantedAuthority> authorities) {
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
