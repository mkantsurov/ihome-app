package technology.positivehome.ihome.domain.runtime.controller;

/**
 * Created by maxim on 6/27/19.
 **/
public class ControllerPortConfigEntity extends ControllerPortConfigEntry {

    private long controllerId;

    public ControllerPortConfigEntity() {
    }

    public long getControllerId() {
        return controllerId;
    }

    public void setControllerId(long controllerId) {
        this.controllerId = controllerId;
    }
}
