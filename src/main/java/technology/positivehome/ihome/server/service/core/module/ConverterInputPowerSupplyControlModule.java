package technology.positivehome.ihome.server.service.core.module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import technology.positivehome.ihome.domain.runtime.module.ModuleConfigEntry;
import technology.positivehome.ihome.domain.runtime.module.OutputPortStatus;
import technology.positivehome.ihome.server.service.core.SystemManager;

import javax.annotation.PreDestroy;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ConverterInputPowerSupplyControlModule extends AbstractRelayBasedIHomeModule implements IHomeModule {

    private static final Logger log = LoggerFactory.getLogger(DirectInputPowerSupplyControlModule.class);
    private final CronModuleJob[] moduleJobs;

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public ConverterInputPowerSupplyControlModule(SystemManager mgr, ModuleConfigEntry configEntry) {
        super(mgr, configEntry);
        log.info("Initializing ConverterInputPowerSupplyControlModule");
        final long checkPeriod = TimeUnit.SECONDS.toMillis(120);

        moduleJobs = new CronModuleJob[]{new CronModuleJob(checkPeriod) {
            @Override
            protected void execute() throws Exception {
                switch (getMode()) {
                    case AUTO:
                        OutputPortStatus status = getOutputPortStatus();
                        switch (mgr.getInputPowerSupplySourceCalc().getPreferredPowerSupplyMode()) {
                            case CONVERTER:
                                if (!status.isEnabled() || status.isUndefined()) {
                                    Runnable task = () -> {
                                        try {
                                            setOutputStatus(OutputPortStatus.enabled());
                                        } catch (Exception ex) {
                                            log.error("Error enabling converter input source");
                                        }
                                    };
                                    executor.schedule(task, 5, TimeUnit.SECONDS);
                                }
                                break;
                            case ONLY_LED:
                            case DIRECT:
                                setOutputStatus(OutputPortStatus.disabled());
                                break;
                        }
                        break;
                }
            }
        }};
    }

    @PreDestroy
    void onDestroey() {
        executor.shutdown();
    }

    @Override
    protected CronModuleJob[] getJobList() {
        return moduleJobs;
    }
}
