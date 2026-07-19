package technology.positivehome.ihome.model.runtime.module;

import technology.positivehome.ihome.model.constant.ModuleAssignment;
import technology.positivehome.ihome.model.constant.ModuleOperationMode;
import technology.positivehome.ihome.model.constant.ModuleStartupMode;
import technology.positivehome.ihome.model.constant.ModuleType;
import technology.positivehome.ihome.security.model.user.Role;

import java.util.Collections;
import java.util.List;

/**
 * Persistence entity for a module_config_entry row.
 * Contains ALL fields mapped from the DB (including the raw FK).
 * Used internally by the repository; converted to {@link ModuleConfigEntry} at the boundary.
 *
 * @param id               the module config entry primary key
 * @param moduleName       the module name
 * @param moduleAssignment the module assignment enum
 * @param mode             the operation mode enum
 * @param startupMode      the startup mode enum
 * @param type             the module type enum
 * @param writerRoleNames  role names that have WRITE access to this module
 *                         (READ is implicit for all roles via system-wide defaults)
 * @param groupId          raw FK to module_group_entry
 * @param moduleGroupEntry the joined group metadata (id, name, priority)
 */
public record ModuleConfigEntity(
        long id,
        String moduleName,
        ModuleAssignment moduleAssignment,
        ModuleOperationMode mode,
        ModuleStartupMode startupMode,
        ModuleType type,
        List<Role> writerRoleNames,
        long groupId,
        ModuleGroupEntry moduleGroupEntry
) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private long id;
        private String moduleName;
        private ModuleAssignment moduleAssignment;
        private ModuleOperationMode mode;
        private ModuleStartupMode startupMode;
        private ModuleType type;
        private List<Role> writerRoleNames = Collections.emptyList();
        private long groupId;
        private ModuleGroupEntry moduleGroupEntry;

        private Builder() {
        }

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Builder moduleName(String moduleName) {
            this.moduleName = moduleName;
            return this;
        }

        public Builder moduleAssignment(ModuleAssignment moduleAssignment) {
            this.moduleAssignment = moduleAssignment;
            return this;
        }

        public Builder mode(ModuleOperationMode mode) {
            this.mode = mode;
            return this;
        }

        public Builder startupMode(ModuleStartupMode startupMode) {
            this.startupMode = startupMode;
            return this;
        }

        public Builder type(ModuleType type) {
            this.type = type;
            return this;
        }

        public Builder writerRoleNames(List<Role> writerRoleNames) {
            this.writerRoleNames = writerRoleNames;
            return this;
        }

        public Builder groupId(long groupId) {
            this.groupId = groupId;
            return this;
        }

        public Builder moduleGroupEntry(ModuleGroupEntry moduleGroupEntry) {
            this.moduleGroupEntry = moduleGroupEntry;
            return this;
        }

        public ModuleConfigEntity build() {
            return new ModuleConfigEntity(
                    id, moduleName, moduleAssignment,
                    mode, startupMode, type,
                    writerRoleNames,
                    groupId, moduleGroupEntry
            );
        }
    }
}
