package technology.positivehome.ihome.server.service.core.module;

import technology.positivehome.ihome.model.constant.ModuleStartupMode;
import technology.positivehome.ihome.model.runtime.module.ModuleConfigEntry;
import technology.positivehome.ihome.model.runtime.module.OutputPortStatus;
import technology.positivehome.ihome.server.service.core.SystemManager;

import java.util.concurrent.TimeUnit;

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
