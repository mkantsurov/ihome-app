package technology.positivehome.ihome.server.service.core;

import technology.positivehome.ihome.domain.constant.ControllerMode;

/**
 * Created by maxim on 6/25/19.
 **/
public interface SysConfig {

    String getIHomeAuthUrl();

    String getIHomeBaseUrl();

    String getIHomeLoginUrl();

    long getUpTimeInMills();

    ControllerMode getControllerMode();

}
