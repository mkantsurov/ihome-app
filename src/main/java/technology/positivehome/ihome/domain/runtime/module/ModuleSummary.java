package technology.positivehome.ihome.domain.runtime.module;

import technology.positivehome.ihome.domain.constant.ModuleAssignment;

/**
 * Created by maxim on 7/4/19.
 **/
public class ModuleSummary {

    private long moduleId;
    private String name;
    private int mode;
    private int outputPortState;
    private ModuleAssignment assignment;
    private long group;

    public long getModuleId() {
        return moduleId;
    }

    public void setModuleId(long moduleId) {
        this.moduleId = moduleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getOutputPortState() {
        return outputPortState;
    }

    public void setOutputPortState(int outputPortState) {
        this.outputPortState = outputPortState;
    }

    public void setAssignment(ModuleAssignment assignment) {
        this.assignment = assignment;
    }

    public ModuleAssignment getAssignment() {
        return assignment;
    }

    public void setGroup(long group) {
        this.group = group;
    }

    public long getGroup() {
        return group;
    }
}
