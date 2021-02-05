package technology.positivehome.ihome.server.service.core.module;

import technology.positivehome.ihome.domain.runtime.module.ModuleConfigEntry;
import technology.positivehome.ihome.domain.runtime.module.OutputPortStatus;
import technology.positivehome.ihome.domain.runtime.sensor.Dht21TempHumiditySensorData;
import technology.positivehome.ihome.server.service.core.SystemManager;

import java.util.concurrent.TimeUnit;

/**
 * Created by maxim on 7/2/19.
 **/
public class GarageVentilationControlModule extends AbstractRelayBasedIHomeModule implements IHomeModule {


    private static final long CHECK_TEMPERATURE_INTERVAL = TimeUnit.MINUTES.toMillis(5);
    private static final long GARAGE_TEMPERATURE_PORT_ID = 6;

    private final CronModuleJob[] moduleJobs;

    public GarageVentilationControlModule(SystemManager mgr, ModuleConfigEntry configEntry) {
        super(mgr, configEntry);

        moduleJobs = new CronModuleJob[]{new CronModuleJob(CHECK_TEMPERATURE_INTERVAL) {
            @Override
            protected void execute() throws Exception {
                switch (getMode()) {
                    case AUTO:
                        OutputPortStatus status = getOutputPortStatus();
                        Dht21TempHumiditySensorData data = getMgr().getDht21TempHumiditySensorReading(GARAGE_TEMPERATURE_PORT_ID);
                        if (data.getTemperature() > 28.5 && (!status.isEnabled() || status.isUndefined())) {
                            setOutputStatus(OutputPortStatus.enabled());
                        } else if (data.getTemperature() < 28 && (status.isEnabled() || status.isUndefined())) {
                            setOutputStatus(OutputPortStatus.disabled());
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
