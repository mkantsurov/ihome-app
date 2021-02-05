package technology.positivehome.ihome.server.service.core.module;

import technology.positivehome.ihome.domain.runtime.module.ModuleConfigEntry;
import technology.positivehome.ihome.server.service.core.SystemManager;

/**
 * Created by maxim on 7/2/19.
 **/
public class GenericDimmerPowerControlModule extends AbstractDimmerBasedIHomeModule implements IHomeModule {

    public GenericDimmerPowerControlModule(SystemManager mgr, ModuleConfigEntry configEntry) {
        super(mgr, configEntry);
    }

    @Override
    protected CronModuleJob[] getJobList() {
        return new CronModuleJob[0];
    }
}
