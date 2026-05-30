package technology.positivehome.ihome.server.service.core.module;

import technology.positivehome.ihome.domain.constant.BinaryPortStatus;
import technology.positivehome.ihome.domain.constant.ModuleStartupMode;
import technology.positivehome.ihome.domain.runtime.module.ModuleConfigElementEntry;
import technology.positivehome.ihome.domain.runtime.module.ModuleConfigEntry;
import technology.positivehome.ihome.domain.runtime.module.OutputPortStatus;
import technology.positivehome.ihome.server.model.command.IHomeCommandFactory;
import technology.positivehome.ihome.server.service.core.SystemManager;

import java.util.concurrent.TimeUnit;

import static technology.positivehome.ihome.server.processor.SystemProcessor.SECURITY_MODE_SENSOR_PORT_ID;

/**
 * Created by maxim on 4/17/23.
 **/
public class SolarSystemPumpPowerControlModule extends AbstractRelayBasedIHomeModule implements IHomeModule {

    private final CronModuleJob[] moduleJobs;
    public SolarSystemPumpPowerControlModule(SystemManager mgr, ModuleConfigEntry configEntry) {
        super(mgr, configEntry);
        moduleJobs = new CronModuleJob[]{
                new CronModuleJob(TimeUnit.MINUTES.toMillis(15)) {
                    @Override
                    protected void execute() throws Exception {
                        switch (getMode()) {
                            case AUTO:
                                if (ModuleStartupMode.ENABLED.equals(getStartupMode()) && !getOutputPortStatus().isEnabled()) {
                                    setOutputStatus(OutputPortStatus.enabled());
                                }
                                break;
                        }
                    }
                }
        };
    }

    @Override
    protected CronModuleJob[] getJobList() {
        return moduleJobs;
    }
}
