package technology.positivehome.ihome.server.service.core.module;

import com.google.common.eventbus.Subscribe;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import technology.positivehome.ihome.domain.constant.ModuleOperationMode;
import technology.positivehome.ihome.domain.constant.ModuleProperty;
import technology.positivehome.ihome.domain.runtime.event.BinaryInputInitiatedHwEvent;
import technology.positivehome.ihome.domain.runtime.module.ModuleConfigElementEntry;
import technology.positivehome.ihome.domain.runtime.module.ModuleConfigEntry;
import technology.positivehome.ihome.domain.runtime.module.OutputPortStatus;
import technology.positivehome.ihome.server.service.core.SystemManager;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by maxim on 7/2/19.
 **/
public class HomeLightMotionSensorRelayBasedControlModule extends AbstractRelayBasedIHomeModule {
    private static final Log log = LogFactory.getLog(HomeLightRelayBasedPowerControlModule.class);

    public static final long DEFAULT_LIGHT_TTL = TimeUnit.MINUTES.toMillis(10L);
    private final AtomicLong lightTtl = new AtomicLong(DEFAULT_LIGHT_TTL);

    private final CronModuleJob[] moduleJobs;
    protected final Set<Long> portsToListen = new HashSet<>();
    protected AtomicLong timeWhenLiteEnabled = new AtomicLong(0L);
    protected AtomicBoolean lightState = new AtomicBoolean(false);

    public HomeLightMotionSensorRelayBasedControlModule(SystemManager mgr, ModuleConfigEntry configEntry) {
        super(mgr, configEntry);

        if (getProperty(ModuleProperty.LIGHT_TTL_IN_MINUTES).isPresent()) {
            lightTtl.set(TimeUnit.MINUTES.toMillis(getProperty(ModuleProperty.LIGHT_TTL_IN_MINUTES).get().getLongValue()));
        }

        moduleJobs = new CronModuleJob[]{
                new CronModuleJob(TimeUnit.MINUTES.toMillis(1)) {
                    @Override
                    protected void execute() throws Exception {
                        switch (getMode()) {
                            case AUTO:
                                lightState.set(getOutputPortStatus().isEnabled());
                                if (timeWhenLiteEnabled.get() + lightTtl.get() < System.currentTimeMillis()) {
                                    lightState.set(setOutputStatus(OutputPortStatus.disabled()).isEnabled());
                                }
                                break;
                        }
                    }
                }
        };
        getMgr().getEventBus().register(this);
        for (ModuleConfigElementEntry ent : getInputPorts()) {
            portsToListen.add(ent.getPort());
        }
    }

    @Override
    protected CronModuleJob[] getJobList() {
        return moduleJobs;
    }

    @Subscribe
    public void handleEvent(BinaryInputInitiatedHwEvent event) throws IOException {
        if (ModuleOperationMode.AUTO.equals(getMode()) && portsToListen.contains(event.getPortId())) {
            boolean wasEnabled = lightState.get();
            timeWhenLiteEnabled.set(System.currentTimeMillis());
            if (!wasEnabled) {
                try {
                    lightState.set(setOutputStatus(OutputPortStatus.enabled()).isEnabled());
                } catch (Exception ex) {
                    log.error("Unable enable light by event initiated by port# " + event.getPortId(), ex);
                }
            }
        }
    }

}
