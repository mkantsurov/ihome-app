package technology.positivehome.ihome.domain.runtime.module;

import technology.positivehome.ihome.domain.constant.ModuleAssignment;
import technology.positivehome.ihome.domain.constant.ModuleOperationMode;
import technology.positivehome.ihome.domain.constant.ModuleType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maxim on 6/27/19.
 **/
public class ModuleConfigEntry {

    private long id;
    private ModuleAssignment moduleAssignment;
    private ModuleGroupEntry moduleGroupEntry;
    private String moduleName;
    private List<ModuleConfigElementEntry> controls = new ArrayList<>();
    private List<ModulePropertyValue> properties = new ArrayList<>();
    private ModuleOperationMode mode;
    private ModuleType type;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public ModuleAssignment getModuleAssignment() {
        return moduleAssignment;
    }

    public void setModuleAssignment(ModuleAssignment moduleAssignment) {
        this.moduleAssignment = moduleAssignment;
    }

    public ModuleGroupEntry getModuleGroupEntry() {
        return moduleGroupEntry;
    }

    public void setModuleGroupEntry(ModuleGroupEntry moduleGroupEntry) {
        this.moduleGroupEntry = moduleGroupEntry;
    }

    public List<ModuleConfigElementEntry> getControls() {
        return controls;
    }

    public void setControls(List<ModuleConfigElementEntry> controls) {
        this.controls = controls;
    }


    public void setMode(ModuleOperationMode mode) {
        this.mode = mode;
    }

    public ModuleOperationMode getMode() {
        return mode;
    }

    public void setType(ModuleType type) {
        this.type = type;
    }

    public ModuleType getType() {
        return type;
    }

    public List<ModulePropertyValue> getProperties() {
        return properties;
    }

    public void setProperties(List<ModulePropertyValue> properties) {
        this.properties = properties;
    }

}
