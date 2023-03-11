package technology.positivehome.ihome.domain.runtime.module;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public record ModuleUpdateRequest(boolean enabledOnStartup,
                                  boolean moduleActive,
                                  int outputValue) implements Serializable {

    public ModuleUpdateRequest(
            @JsonProperty("enabledOnStartup") boolean enabledOnStartup,
            @JsonProperty("moduleActive") boolean moduleActive,
            @JsonProperty("outputValue") int outputValue) {
        this.enabledOnStartup = enabledOnStartup;
        this.moduleActive = moduleActive;
        this.outputValue = outputValue;
    }
}
