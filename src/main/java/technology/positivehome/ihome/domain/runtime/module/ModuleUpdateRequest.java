package technology.positivehome.ihome.domain.runtime.module;

import java.io.Serializable;

/**
 * Created by maxim on 3/7/21.
 **/
public class ModuleUpdateRequest implements Serializable {

    private boolean moduleActive;
    private boolean outputPortEnabled;

    public boolean isModuleActive() {
        return moduleActive;
    }

    public void setModuleActive(boolean moduleActive) {
        this.moduleActive = moduleActive;
    }

    public boolean isOutputPortEnabled() {
        return outputPortEnabled;
    }

    public void setOutputPortEnabled(boolean outputPortEnabled) {
        this.outputPortEnabled = outputPortEnabled;
    }

}
