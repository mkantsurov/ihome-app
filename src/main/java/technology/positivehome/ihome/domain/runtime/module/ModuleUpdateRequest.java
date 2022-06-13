package technology.positivehome.ihome.domain.runtime.module;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by maxim on 3/7/21.
 **/
public class ModuleUpdateRequest implements Serializable {

    private final boolean enabledOnStartup;
    private final boolean moduleActive;
    private final boolean outputPortEnabled;

    public ModuleUpdateRequest(
            @JsonProperty("enabledOnStartup") boolean enabledOnStartup,
            @JsonProperty("moduleActive") boolean moduleActive,
            @JsonProperty("outputPortEnabled") boolean outputPortEnabled) {
        this.enabledOnStartup = enabledOnStartup;
        this.moduleActive = moduleActive;
        this.outputPortEnabled = outputPortEnabled;
    }

    public boolean isEnabledOnStartup() {
        return enabledOnStartup;
    }
    public boolean isModuleActive() {
        return moduleActive;
    }

    public boolean isOutputPortEnabled() {
        return outputPortEnabled;
    }

}
