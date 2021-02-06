package technology.positivehome.ihome.server.service.core;

import technology.positivehome.ihome.domain.constant.ControllerMode;
import technology.positivehome.ihome.server.service.util.IHomeEventBus;

/**
 * Created by maxim on 6/25/19.
 **/
public interface SysConfig {

    String getIHomeAuthUrl();

    String getIHomeBaseUrl();

    String getIHomeLoginUrl();

    IHomeEventBus getEventBus();

    long getUpTimeInMills();

    ControllerMode getControllerMode();

}
