package technology.positivehome.ihome.domain.runtime.controller;

import technology.positivehome.ihome.domain.constant.MegadPortType;

/**
 * Created by maxim on 6/27/19.
 **/
public class AppendControllerPortConfigEntryRequest extends ControllerPortConfigEntry {

    private long controllerId;

    public AppendControllerPortConfigEntryRequest() {
    }

    public AppendControllerPortConfigEntryRequest(long controllerId, String description, MegadPortType type, int portAdress) {
        super(-1, description, type, portAdress);
        this.controllerId = controllerId;
    }

    public long getControllerId() {
        return controllerId;
    }

    public void setControllerId(long controllerId) {
        this.controllerId = controllerId;
    }

}
