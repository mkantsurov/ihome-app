package technology.positivehome.ihome.server.service.core.module;

import technology.positivehome.ihome.domain.constant.BinaryPortStatus;
import technology.positivehome.ihome.domain.runtime.module.ModuleConfigEntry;
import technology.positivehome.ihome.domain.runtime.module.OutputPortStatus;
import technology.positivehome.ihome.server.service.core.SystemManager;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by maxim on 7/2/19.
 **/
public class GenericInputPowerDependentRelayPowerControlModule extends AbstractRelayBasedIHomeModule implements IHomeModule {

    private static final long POWER_CHECK_INTERVAL = TimeUnit.SECONDS.toMillis(30);
    private static final long MAX_POWER_ABSENT_DELAY = TimeUnit.MINUTES.toMillis(7);
    private static final long POWER_CHECKING_DELAY = TimeUnit.MINUTES.toMillis(5);

    public static final int POWER_SENSOR_PORT_ID = 29;

    private final CronModuleJob[] moduleJobs;

    private final AtomicLong lastPowerOkTs = new AtomicLong(System.currentTimeMillis());
    private final AtomicLong lastPowerFailTs = new AtomicLong(System.currentTimeMillis());

    public GenericInputPowerDependentRelayPowerControlModule(SystemManager mgr, ModuleConfigEntry configEntry) {
        super(mgr, configEntry);
        moduleJobs = new CronModuleJob[]{
                new CronModuleJob(POWER_CHECK_INTERVAL) {
                    @Override
                    protected void execute() throws Exception {
                        switch (getMode()) {
                            case AUTO:
                                OutputPortStatus status = getOutputPortStatus();
                                BinaryPortStatus state = getMgr().getBinSensorsState(POWER_SENSOR_PORT_ID);
                                long now = System.currentTimeMillis();
                                switch (state) {
                                    case ENABLED:
                                        lastPowerOkTs.set(System.currentTimeMillis());
                                        if (status.isDisabled() && now - POWER_CHECKING_DELAY > lastPowerFailTs.get()) {
                                            setOutputStatus(OutputPortStatus.enabled());
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
