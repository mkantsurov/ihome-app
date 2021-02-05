package technology.positivehome.ihome.domain.runtime.controller;

import java.util.List;

/**
 * Created by maxim on 6/27/19.
 **/
public class ControllerConfigEntry {

    private long id;
    private String ipAddress;
    private String controllerName;
    private List<ControllerPortConfigEntry> portConfig;

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

    public List<ControllerPortConfigEntry> getPortConfig() {
        return portConfig;
    }

    public void setPortConfig(List<ControllerPortConfigEntry> portConfig) {
        this.portConfig = portConfig;
    }

}
