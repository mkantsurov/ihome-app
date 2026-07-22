package technology.positivehome.ihome.server.persistence;

import technology.positivehome.ihome.model.constant.ModuleOperationMode;
import technology.positivehome.ihome.model.constant.ModuleStartupMode;
import technology.positivehome.ihome.model.runtime.module.ModuleConfigElementEntry;
import technology.positivehome.ihome.model.runtime.module.ModuleConfigEntry;
import technology.positivehome.ihome.model.runtime.module.ModuleSettings;

import java.util.Collection;
import java.util.List;


/**
 * Created by maxim on 6/25/19.
 **/
public interface ModuleConfigRepository {

    List<ModuleConfigEntry> loadModuleConfig();

    /**
     * Returns true if at least one module exists whose {@code module_assignment} value
     * is contained in {@code assignmentNames}.  Used to determine whether a role that
     * can only write specific assignment types actually has any writable modules.
     *
     * @param assignmentNames the set of {@link technology.positivehome.ihome.model.constant.ModuleAssignment}
     *                        enum names to match against
     * @return true if at least one matching module is found
     */
    boolean hasAnyModuleWithAssignments(Collection<String> assignmentNames);

    /**
     * Returns true if at least one module exists whose {@code permission} column
     * (the per-module writer role whitelist) contains the specified role name.
     * <p>
     * Used to determine whether a non-ADMIN, non-SUPERVISOR role has any writable
     * modules via the per-module permission override mechanism (e.g., CHILDREN_ROOM1_MANAGER
     * is listed as a writer on children room #1 light modules).
     *
     * @param roleName the role name to look for in the permission column
     * @return true if at least one module grants writer access to this role
     */
    boolean hasAnyModuleWithWriterRole(String roleName);

    ModuleConfigEntry updateModuleMode(long moduleId, ModuleOperationMode newMode);

    ModuleConfigEntry updateModuleStartupMode(long moduleId, ModuleStartupMode moduleStartupMode);

    List<ModuleConfigEntry> addNewModule(ModuleSettings moduleSettings);

    ModuleConfigEntry updateModuleProperties(ModuleSettings moduleConfigProperties);

    ModuleConfigElementEntry updateModuleConfigElement(ModuleConfigElementEntry value);

    ModuleConfigEntry getModuleConfigEntry(long moduleId);
}
