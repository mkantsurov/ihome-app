package technology.positivehome.ihome.server.service.core;

import technology.positivehome.ihome.server.service.core.controller.ControllerEventInfo;

import java.util.Optional;

/**
 * Created by maxim on 8/3/19.
 **/
public interface ControllerEventListener {

    Optional<Long> getControllerIdByAddress(String address);

    boolean isControllerExists(Long controllerId);

    void onControllerEvent(ControllerEventInfo eventInfo);
}
