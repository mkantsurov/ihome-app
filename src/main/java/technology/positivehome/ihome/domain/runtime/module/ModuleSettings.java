package technology.positivehome.ihome.domain.runtime.module;

import technology.positivehome.ihome.domain.constant.ModuleOperationMode;
import technology.positivehome.ihome.domain.constant.ModuleType;

/**
 * Created by maxim on 6/27/19.
 **/
public class ModuleSettings {
    private long id;
    private long moduleGroupEntryId;
    private String moduleName;
    private ModuleOperationMode mode;
    private ModuleType type;

    public ModuleSettings() {
    }

    public ModuleSettings(long id, String moduleName, ModuleType type, ModuleOperationMode mode, long moduleGroupEntryId) {
        this.id = id;
        this.moduleGroupEntryId = moduleGroupEntryId;
        this.moduleName = moduleName;
        this.mode = mode;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getModuleGroupEntryId() {
        return moduleGroupEntryId;
    }

    public void setModuleGroupEntryId(long moduleGroupEntryId) {
        this.moduleGroupEntryId = moduleGroupEntryId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public ModuleOperationMode getMode() {
        return mode;
    }

    public void setMode(ModuleOperationMode mode) {
        this.mode = mode;
    }

    public ModuleType getType() {
        return type;
    }

    public void setType(ModuleType type) {
        this.type = type;
    }
}
