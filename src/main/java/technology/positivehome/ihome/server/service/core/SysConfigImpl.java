package technology.positivehome.ihome.server.service.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import technology.positivehome.ihome.domain.constant.ControllerMode;
import technology.positivehome.ihome.server.persistence.LogRepository;
import technology.positivehome.ihome.server.service.util.IHomeEventBus;

import java.util.Optional;

/**
 * Created by maxim on 6/25/19.
 **/
@Service
public class SysConfigImpl implements SysConfig, InitializingBean {

    private static final Log log = LogFactory.getLog(SysConfigImpl.class);
    private final String iHomeBaseUrl;
    private final String iHomeLoginUrl;
    private final String iHomeAuthUrl;
    private final ControllerMode mode;

    private long startTimeInMills;

    private final LogRepository logRepository;
    private final IHomeEventBus eventBus;

    @Autowired
    public SysConfigImpl(@Value("${ihome.app.url}") String iHomeBaseUrl, @Value("${ihome.app.login-page}") String loginPage, @Value("${ihome.app.auth-page}") String authPage, @Value("${ihome.app.emulation-mode}") String mode, LogRepository logRepository, IHomeEventBus eventBus) {
        this.iHomeBaseUrl = Optional.ofNullable(iHomeBaseUrl).orElseThrow();
        this.iHomeLoginUrl = iHomeBaseUrl + Optional.ofNullable(loginPage).orElseThrow();
        this.iHomeAuthUrl = iHomeBaseUrl + Optional.ofNullable(authPage).orElseThrow();
        this.mode = (Optional.ofNullable(mode).orElseThrow().equals("true")) ? ControllerMode.EMULATED : ControllerMode.LIVE;
        this.logRepository = logRepository;
        this.eventBus = eventBus;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        startTimeInMills = System.currentTimeMillis();
        logRepository.writeStartupMessage();
    }

    @Override
    public String getIHomeAuthUrl() {
        return iHomeAuthUrl;
    }

    @Override
    public String getIHomeBaseUrl() {
        return iHomeBaseUrl;
    }

    @Override
    public String getIHomeLoginUrl() {
        return iHomeLoginUrl;
    }

    @Override
    public IHomeEventBus getEventBus() {
        return eventBus;
    }

    @Override
    public long getUpTimeInMills() {
        return System.currentTimeMillis() - startTimeInMills;
    }

    @Override
    public ControllerMode getControllerMode() {
        return mode;
    }
}
