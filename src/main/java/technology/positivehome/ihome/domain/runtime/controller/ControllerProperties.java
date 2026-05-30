package technology.positivehome.ihome.domain.runtime.controller;

/**
 * Created by maxim on 6/27/19.
 **/
public class ControllerProperties {

    private long id;
    private String ipAddress;
    private String controllerName;

    public ControllerProperties() {
    }

    public ControllerProperties(long id, String controllerName, String ipAddress) {
        this.id = id;
        this.ipAddress = ipAddress;
        this.controllerName = controllerName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getControllerName() {
        return controllerName;
    }

    public void setControllerName(String controllerName) {
        this.controllerName = controllerName;
    }

}
