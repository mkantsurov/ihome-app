package technology.positivehome.ihome.server.service.core.module;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import technology.positivehome.ihome.domain.constant.BinaryPortStatus;
import technology.positivehome.ihome.domain.runtime.module.ModuleConfigEntry;
import technology.positivehome.ihome.domain.runtime.module.OutputPortStatus;
import technology.positivehome.ihome.domain.runtime.sensor.Dht21TempHumiditySensorData;
import technology.positivehome.ihome.server.model.command.IHomeCommandFactory;
import technology.positivehome.ihome.server.service.core.SystemManager;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class RecuperationPowerControlModule extends AbstractRelayBasedIHomeModule implements IHomeModule {

    private static final Log log = LogFactory.getLog(RecuperationPowerControlModule.class);
    private static final long POWER_CHECK_INTERVAL = TimeUnit.SECONDS.toMillis(120);
    private static final long MAX_POWER_ABSENT_DELAY = TimeUnit.MINUTES.toMillis(3);
    private static final long POWER_CHECKING_DELAY = TimeUnit.MINUTES.toMillis(3);

    private static final long OUTDOOR_TEMPERATURE_SENS_PORT_ID = 11;

    public static final int POWER_SENSOR_PORT_ID = 29;

    private final CronModuleJob[] moduleJobs;

    private final AtomicLong lastPowerOkTs = new AtomicLong(System.currentTimeMillis());
    private final AtomicLong lastPowerFailTs = new AtomicLong(System.currentTimeMillis());

    public RecuperationPowerControlModule(SystemManager mgr, ModuleConfigEntry configEntry) {
        super(mgr, configEntry);
        moduleJobs = new CronModuleJob[]{
                new CronModuleJob(POWER_CHECK_INTERVAL) {
                    @Override
                    protected void execute() throws Exception {
                        switch (getMode()) {
                            case AUTO:
                                OutputPortStatus status = getOutputPortStatus();
                                BinaryPortStatus state = getMgr().runCommand(IHomeCommandFactory.cmdGetBinarySensorReading(POWER_SENSOR_PORT_ID));
                                Dht21TempHumiditySensorData data = getMgr().runCommand(IHomeCommandFactory.cmdGetDht21TempHumiditySensorReading(OUTDOOR_TEMPERATURE_SENS_PORT_ID));
                                long now = System.currentTimeMillis();
                                log.info("RecuperationModule: " +
                                        "\n ext power state: " + state +
                                        "\n module out port state: " + status.isEnabled() +
                                        "\n last power ok ts: " + (System.currentTimeMillis() - lastPowerOkTs.get()) +
                                        "\n temperature: " + data.getTemperature());
                                switch (state) {
                                    case ENABLED:
                                        lastPowerOkTs.set(System.currentTimeMillis());
                                        if (status.isDisabled()
                                                && now - POWER_CHECKING_DELAY > lastPowerFailTs.get()
                                                && data.getTemperature() < 24.5) {
                                            setOutputStatus(OutputPortStatus.enabled());
                                        }  else if (status.isEnabled() && data.getTemperature() > 25) {
                                            setOutputStatus(OutputPortStatus.disabled());
                                        }
                                        break;
                                    case DISABLED:
                                        lastPowerFailTs.set(System.currentTimeMillis());
                                        if (status.isEnabled() && now - MAX_POWER_ABSENT_DELAY > lastPowerOkTs.get()) {
                                            setOutputStatus(OutputPortStatus.disabled());
                                        }
                                        break;
                                }
                                break;
                        }
                    }
                }};
    }

    @Override
    protected CronModuleJob[] getJobList() {
        return moduleJobs;
    }
}
