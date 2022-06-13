package technology.positivehome.ihome.domain.runtime.module;

import com.fasterxml.jackson.annotation.JsonProperty;
import technology.positivehome.ihome.domain.constant.ModuleAssignment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maxim on 7/4/19.
 **/
public class ModuleSummary {

    private final long moduleId;
    private final String name;
    private final int mode;

    private final int startupMode;

    private final int outputPortState;
    private final ModuleAssignment assignment;
    private final long group;

    private final List<ModulePropertyValue> properties = new ArrayList<>();

    public ModuleSummary(@JsonProperty("moduleId") long moduleId,
                         @JsonProperty("name") String name,
                         @JsonProperty("mode") int mode,
                         @JsonProperty("startupMode")int startupMode,
                         @JsonProperty("outputPortState") int outputPortState,
                         @JsonProperty("assignment") ModuleAssignment assignment,
                         @JsonProperty("group") long group) {
        this.moduleId = moduleId;
        this.name = name;
        this.mode = mode;
        this.startupMode = startupMode;
        this.outputPortState = outputPortState;
        this.assignment = assignment;
        this.group = group;
    }

    public long getModuleId() {
        return moduleId;
    }

    public String getName() {
        return name;
    }

    public int getMode() {
        return mode;
    }

    public int getStartupMode() {
        return startupMode;
    }

    public int getOutputPortState() {
        return outputPortState;
    }

    public ModuleAssignment getAssignment() {
        return assignment;
    }

    public long getGroup() {
        return group;
    }

    public List<ModulePropertyValue> getProperties() {
        return properties;
    }
}
