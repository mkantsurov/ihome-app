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

public class RecuperationPowerControlModule extends AbstractRelayBasedIHomeModule implements IHomeModule {

    private static final Log log = LogFactory.getLog(RecuperationPowerControlModule.class);
    private static final long TEMPERATURE_CHECK_INTERVAL = TimeUnit.SECONDS.toMillis(120);
    private static final long OUTDOOR_TEMPERATURE_SENS_PORT_ID = 11;

    public static final int POWER_SENSOR_PORT_ID = 29;

    private final CronModuleJob[] moduleJobs;

    public RecuperationPowerControlModule(SystemManager mgr, ModuleConfigEntry configEntry) {
        super(mgr, configEntry);
        moduleJobs = new CronModuleJob[]{
                new CronModuleJob(TEMPERATURE_CHECK_INTERVAL) {
                    @Override
                    protected void execute() throws Exception {
                        switch (getMode()) {
                            case AUTO:
                                OutputPortStatus status = getOutputPortStatus();
                                Dht21TempHumiditySensorData data = getMgr().runCommand(IHomeCommandFactory.cmdGetDht21TempHumiditySensorReading(OUTDOOR_TEMPERATURE_SENS_PORT_ID));
                                long now = System.currentTimeMillis();
                                log.info("RecuperationModule: " +
                                        "\n module out port state: " + status.isEnabled() +
                                        "\n temperature: " + data.getTemperature());
                                if (status.isDisabled()
                                        && data.getTemperature() < 24.5) {
                                    setOutputStatus(OutputPortStatus.enabled());
                                } else if (status.isEnabled() && data.getTemperature() > 25) {
                                    setOutputStatus(OutputPortStatus.disabled());
                                }
                        }
                    }
                }};
    }

    @Override
    protected CronModuleJob[] getJobList() {
        return moduleJobs;
    }
}
