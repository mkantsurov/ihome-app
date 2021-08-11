package technology.positivehome.ihome.domain.runtime.event;

import org.springframework.context.ApplicationEvent;
import technology.positivehome.ihome.domain.constant.BinaryPortStatus;
import technology.positivehome.ihome.domain.constant.MegadPortType;
import technology.positivehome.ihome.server.service.core.controller.ControllerEventInfo;

/**
 * Created by maxim on 6/30/19.
 **/
public class BinaryInputInitiatedHwEvent extends ApplicationEvent {

    private long portId;
    private MegadPortType portType;
    private BinaryPortStatus mode = BinaryPortStatus.UNDEFINED;
    private Integer count;
    private Integer click;

    public BinaryInputInitiatedHwEvent(Object source, long portId, MegadPortType portType, ControllerEventInfo eventInfo) {
        super(source);
        this.portId = portId;
        this.portType = portType;

        if (eventInfo.getMode() != null) {
            switch (eventInfo.getMode()) {
                case 0:
                    mode = BinaryPortStatus.DISABLED;
                    break;
                case 1:
                    mode = BinaryPortStatus.ENABLED;
                    break;
                default:
                    mode = BinaryPortStatus.UNDEFINED;
            }
        }
        this.count = eventInfo.getCount();
        this.click = eventInfo.getClick();
    }

    public long getPortId() {
        return portId;
    }

    public MegadPortType getPortType() {
        return portType;
    }

    public BinaryPortStatus getMode() {
        return mode;
    }

    public Integer getCount() {
        return count;
    }

    public Integer getClick() {
        return click;
    }

}
