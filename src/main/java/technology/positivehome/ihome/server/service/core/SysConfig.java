package technology.positivehome.ihome.server.service.core;

import technology.positivehome.ihome.domain.constant.ControllerMode;
import technology.positivehome.ihome.domain.runtime.event.IHomeProperty;
import technology.positivehome.ihome.server.service.util.IHomeEventBus;

import java.util.List;

/**
 * Created by maxim on 6/25/19.
 **/
public interface SysConfig {

    String getIHomeAuthUrl();

    String getIHomeBaseUrl();

    String getIHomeLoginUrl();

    List<IHomeProperty> getProperties();

    IHomeEventBus getEventBus();

    long getUpTimeInMills();

    String getIHomeDistVersion();

    String getIHomeBuildVersion();

    String getFileCacheDir();

    String getHome();

    String getLogsDir();

    String getLogNames();

    ControllerMode getControllerMode();

}
