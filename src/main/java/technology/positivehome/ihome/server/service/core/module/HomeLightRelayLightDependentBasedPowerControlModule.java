package technology.positivehome.ihome.server.service.core.module;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import technology.positivehome.ihome.model.constant.ErrorEventType;
import technology.positivehome.ihome.model.constant.ModuleOperationMode;
import technology.positivehome.ihome.model.runtime.event.BinaryInputInitiatedHwEvent;
import technology.positivehome.ihome.model.runtime.module.ModuleConfigElementEntry;
import technology.positivehome.ihome.model.runtime.module.ModuleConfigEntry;
import technology.positivehome.ihome.model.runtime.module.OutputPortStatus;
import technology.positivehome.ihome.model.runtime.event.IHomeErrorEvent;
import technology.positivehome.ihome.server.service.core.SystemManager;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by maxim on 7/2/19.
 **/
public class HomeLightRelayLightDependentBasedPowerControlModule extends AbstractRelayBasedIHomeModule {

    private static final Log log = LogFactory.getLog(HomeLightRelayLightDependentBasedPowerControlModule.class);

    private final Set<Long> portsToListen = new HashSet<>();
    private AtomicBoolean lightState = new AtomicBoolean(false);
    private final CronModuleJob[] moduleJobs;

    public HomeLightRelayLightDependentBasedPowerControlModule(SystemManager mgr, ModuleConfigEntry configEntry) {
        super(mgr, configEntry);
        moduleJobs = new CronModuleJob[]{
                new CronModuleJob(TimeUnit.MINUTES.toMillis(15), mgr.getEventPublisher()) {
                    @Override
                    protected void execute() throws Exception {
                        if (Objects.requireNonNull(getMode()) == ModuleOperationMode.AUTO) {
                            lightState.set(getOutputPortStatus().isEnabled());
                            if (lightState.get() && !isExternalLightRequired()) {
                                lightState.set(setOutputStatus(OutputPortStatus.disabled()).isEnabled());
                            }
                        }
                    }
                }
        };

        for (ModuleConfigElementEntry ent : getInputPorts()) {
            portsToListen.add(ent.getPort());
        }
    }

    @Override
    protected CronModuleJob[] getJobList() {
        return moduleJobs;
    }

    public void handleEvent(BinaryInputInitiatedHwEvent event) {
        if (portsToListen.contains(event.getPortId())) {
            try {
                boolean wasEnabled = lightState.get();
                if (wasEnabled) {
                    lightState.set(setOutputStatus(OutputPortStatus.disabled()).isEnabled());
                } else if (isExternalLightRequired()) {
                    lightState.set(setOutputStatus(OutputPortStatus.enabled()).isEnabled());
                }
            } catch (Exception ex) {
                log.error("Unable to switch light", ex);
                getMgr().getEventPublisher().publishEvent(new IHomeErrorEvent(this, ErrorEventType.MODULE_LIGHT_TOGGLE, "Unable to switch light: " + ex.getMessage()));
            }
        }
    }

    private boolean isExternalLightRequired() {
        return getMgr().getInputPowerSupplySourceCalc().getAvgValue(TimeUnit.MINUTES.toMillis(20)) < 50;
    }
}
