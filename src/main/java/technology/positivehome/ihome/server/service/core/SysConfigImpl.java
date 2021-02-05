package technology.positivehome.ihome.server.service.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import technology.positivehome.ihome.domain.constant.ControllerMode;
import technology.positivehome.ihome.domain.constant.IHomePropertyType;
import technology.positivehome.ihome.domain.runtime.event.IHomeProperty;
import technology.positivehome.ihome.server.persistence.LogRepository;
import technology.positivehome.ihome.server.service.util.IHomeEventBus;

import java.net.URL;
import java.util.*;

/**
 * Created by maxim on 6/25/19.
 **/
@Service
public class SysConfigImpl implements SysConfig, InitializingBean {

    private static final Log log = LogFactory.getLog(SysConfigImpl.class);
    private static final String CONFIG_PROPERTIES = "ihome.properties";
    private final Map<IHomePropertyType, String> iHomeProperties = new HashMap<>();
    private String iHomeBaseUrl;
    private String iHomeLoginUrl;
    private String iHomeAuthUrl;

    private long startTimeInMills;

    private final LogRepository logRepository;
    private final IHomeEventBus eventBus;

    @Autowired
    public SysConfigImpl(LogRepository logRepository, IHomeEventBus eventBus) {
        this.logRepository = logRepository;
        this.eventBus = eventBus;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        startTimeInMills = System.currentTimeMillis();
        URL url = Thread.currentThread().getContextClassLoader().getResource(CONFIG_PROPERTIES);
        if (url == null) {
            log.warn("The configuration could not be found: " + CONFIG_PROPERTIES);
        } else {
            Properties props = new Properties();
            try {
                props.load(url.openStream());
                iHomeProperties.clear();
                for (IHomePropertyType key : IHomePropertyType.values()) {
                    Object value = props.get(key.name().toLowerCase());
                    if (value != null) {
                        iHomeProperties.put(key, value.toString());
                    }
                }

                iHomeBaseUrl = iHomeProperties.get(IHomePropertyType.PROTOCOL) + "://" +
                        iHomeProperties.get(IHomePropertyType.HOST) + ":" +
                        iHomeProperties.get(IHomePropertyType.PORT) + "/" +
                        iHomeProperties.get(IHomePropertyType.PATH);

                iHomeLoginUrl = iHomeBaseUrl + "/" + iHomeProperties.get(IHomePropertyType.LOGIN_PAGE);

                iHomeAuthUrl = iHomeBaseUrl + "/" + iHomeProperties.get(IHomePropertyType.START_PAGE);
                logRepository.writeStartupMessage();
            } catch (java.io.IOException e) {
                log.error("Could not read configuration file from URL [" + url + "].", e);
            }
        }
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
    public List<IHomeProperty> getProperties() {
        List<IHomeProperty> properties = new ArrayList<>();
        for (Map.Entry<IHomePropertyType, String> entry : iHomeProperties.entrySet()) {
            properties.add(new IHomeProperty(entry.getKey(), entry.getValue()));
        }
        return properties;
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
    public String getIHomeDistVersion() {
        return iHomeProperties.get(IHomePropertyType.DIST_VERSION);
    }

    @Override
    public String getIHomeBuildVersion() {
        return iHomeProperties.get(IHomePropertyType.BUILD_VERSION);
    }

    @Override
    public String getFileCacheDir() {
        return iHomeProperties.get(IHomePropertyType.FILE_CACHE_DIR);
    }

    @Override
    public String getHome() {
        return iHomeProperties.get(IHomePropertyType.IHOME_DIR);
    }

    @Override
    public String getLogsDir() {
        return "";
    }

    @Override
    public String getLogNames() {
        return "";
    }

    @Override
    public ControllerMode getControllerMode() {
        if ("true".equals(iHomeProperties.get(IHomePropertyType.EMULATION_MODE))) {
            return ControllerMode.EMULATED;
        }
        return ControllerMode.LIVE;
    }
}
