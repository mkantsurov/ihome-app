package technology.positivehome.ihome.domain.runtime.controller;

/**
 * Created by maxim on 6/27/19.
 **/
public class RemoveControllerPortRequest {

    private long controllerId;
    private long portId;

    public RemoveControllerPortRequest() {
    }

    public RemoveControllerPortRequest(long controllerId, long portId) {
        this.controllerId = controllerId;
        this.portId = portId;
    }

    public long getControllerId() {
        return controllerId;
    }

    public void setControllerId(long controllerId) {
        this.controllerId = controllerId;
    }

    public long getPortId() {
        return portId;
    }

    public void setPortId(long portId) {
        this.portId = portId;
    }

}
