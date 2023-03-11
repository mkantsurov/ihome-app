package technology.positivehome.ihome.server.model;

import technology.positivehome.ihome.domain.constant.IHomePortType;

/**
 * Created by maxim on 3/11/23.
 **/
public class PortInfo {

    private long portId;
    private IHomePortType portType;

    public PortInfo(long portId, IHomePortType portType) {
        this.portId = portId;
        this.portType = portType;
    }

    public long getPortId() {
        return portId;
    }

    public IHomePortType getPortType() {
        return portType;
    }
}
