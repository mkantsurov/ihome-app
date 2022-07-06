package technology.positivehome.ihome.server.service.core.module;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import technology.positivehome.ihome.domain.constant.BinaryPortStatus;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.domain.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.domain.runtime.module.ModuleConfigEntry;
import technology.positivehome.ihome.domain.runtime.module.OutputPortStatus;
import technology.positivehome.ihome.domain.runtime.sensor.Dht21TempHumiditySensorData;
import technology.positivehome.ihome.server.service.core.SystemManager;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class RecuperationPowerControlModule extends AbstractRelayBasedIHomeModule implements IHomeModule {

    private static final Log log = LogFactory.getLog(RecuperationPowerControlModule.class);
    private static final long POWER_CHECK_INTERVAL = TimeUnit.MINUTES.toMillis(5);
    private static final long MAX_POWER_ABSENT_DELAY = TimeUnit.MINUTES.toMillis(90);
    private static final long POWER_CHECKING_DELAY = TimeUnit.MINUTES.toMillis(5);

    private static final long OUTDOOR_TEMPERATURE_SENS_PORT_ID = 11;

    public static final int POWER_SENSOR_PORT_ID = 29;

    private final CronModuleJob[] moduleJobs;

    private final AtomicLong lastPowerOkTs = new AtomicLong(System.currentTimeMillis());
    private final AtomicLong lastPowerFailTs = new AtomicLong(System.currentTimeMillis());
    private final AtomicBoolean disabledManually = new AtomicBoolean(true);

    public RecuperationPowerControlModule(SystemManager mgr, ModuleConfigEntry configEntry) {
        super(mgr, configEntry);
        moduleJobs = new CronModuleJob[]{
                new CronModuleJob(POWER_CHECK_INTERVAL) {
                    @Override
                    protected void execute() throws Exception {
                        switch (getMode()) {
                            case AUTO:
                                OutputPortStatus status = getOutputPortStatus();
                                BinaryPortStatus state = getMgr().getBinSensorsState(POWER_SENSOR_PORT_ID);
                                Dht21TempHumiditySensorData data = getMgr().getDht21TempHumiditySensorReading(OUTDOOR_TEMPERATURE_SENS_PORT_ID);
                                long now = System.currentTimeMillis();
                                log.info("RecuperationModule: " +
                                        "\n disabled manually: " + disabledManually.get() +
                                        "\n ext power state: " + state +
                                        "\n module out port state: " + status.isEnabled() +
                                        "\n last power ok ts: " + (System.currentTimeMillis() - lastPowerOkTs.get()) +
                                        "\n temperature: " + data.getTemperature());
                                switch (state) {
                                    case ENABLED:
                                        lastPowerOkTs.set(System.currentTimeMillis());
                                        if (status.isDisabled() && now - POWER_CHECKING_DELAY > lastPowerFailTs.get()
                                                && data.getTemperature() < 24.5
                                                && !disabledManually.get()) {
                                            setOutputStatus(OutputPortStatus.enabled());
                                        }  else if (status.isEnabled() && data.getTemperature() > 25) {
                                            setOutputStatus(OutputPortStatus.disabled());
                                        }
                                        break;
                                    case DISABLED:
                                        lastPowerFailTs.set(System.currentTimeMillis());
                                        if (status.isEnabled() && now - MAX_POWER_ABSENT_DELAY > lastPowerOkTs.get()) {
                                            setOutputStatus(OutputPortStatus.disabled());
                                            disabledManually.set(false);
                                        }
                                        break;
                                }
                                break;
                        }
                    }
                }};
    }

    @Override
    public OutputPortStatus setOutputStatus(OutputPortStatus status) throws URISyntaxException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, MegadApiMallformedUrlException, InterruptedException {
        disabledManually.set(true);
        return super.setOutputStatus(status);
    }

    @Override
    protected CronModuleJob[] getJobList() {
        return moduleJobs;
    }
}
