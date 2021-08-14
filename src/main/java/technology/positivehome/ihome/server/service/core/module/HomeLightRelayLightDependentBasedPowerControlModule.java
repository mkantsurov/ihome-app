package technology.positivehome.ihome.server.service.core.module;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import technology.positivehome.ihome.domain.constant.PreferredPowerSupplyMode;
import technology.positivehome.ihome.domain.runtime.event.BinaryInputInitiatedHwEvent;
import technology.positivehome.ihome.domain.runtime.module.ModuleConfigElementEntry;
import technology.positivehome.ihome.domain.runtime.module.ModuleConfigEntry;
import technology.positivehome.ihome.domain.runtime.module.OutputPortStatus;
import technology.positivehome.ihome.server.service.core.SystemManager;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by maxim on 7/2/19.
 **/
public class HomeLightRelayLightDependentBasedPowerControlModule extends AbstractRelayBasedIHomeModule {

    private static final Log log = LogFactory.getLog(HomeLightRelayLightDependentBasedPowerControlModule.class);

    private final Set<Long> portsToListen = new HashSet<>();
    private AtomicBoolean lightState = new AtomicBoolean(false);

    public HomeLightRelayLightDependentBasedPowerControlModule(SystemManager mgr, ModuleConfigEntry configEntry) {
        super(mgr, configEntry);
        for (ModuleConfigElementEntry ent : getInputPorts()) {
            portsToListen.add(ent.getPort());
        }
    }

    @Override
    protected CronModuleJob[] getJobList() {
        return new CronModuleJob[0];
    }

    public void handleEvent(BinaryInputInitiatedHwEvent event) {
        if (portsToListen.contains(event.getPortId())) {
            try {
                boolean wasEnabled = lightState.get();
                if (wasEnabled) {
                    lightState.set(setOutputStatus(OutputPortStatus.disabled()).isEnabled());
                } else if (!PreferredPowerSupplyMode.ONLY_LED.equals(getMgr().getInputPowerSupplySourceCalc().getPreferredPowerSupplyMode())) {
                    lightState.set(setOutputStatus(OutputPortStatus.enabled()).isEnabled());
                }
            } catch (Exception ex) {
                log.error("Unable to switch light", ex);
            }
        }
    }
}
