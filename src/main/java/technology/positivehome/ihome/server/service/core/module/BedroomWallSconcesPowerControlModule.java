package technology.positivehome.ihome.server.service.core.module;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import technology.positivehome.ihome.domain.constant.BinaryPortStatus;
import technology.positivehome.ihome.domain.runtime.event.BinaryInputInitiatedHwEvent;
import technology.positivehome.ihome.domain.runtime.module.ModuleConfigElementEntry;
import technology.positivehome.ihome.domain.runtime.module.ModuleConfigEntry;
import technology.positivehome.ihome.domain.runtime.module.OutputPortStatus;
import technology.positivehome.ihome.server.service.core.SystemManager;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class BedroomWallSconcesPowerControlModule extends AbstractRelayBasedIHomeModule implements IHomeModule {

    private static final Log log = LogFactory.getLog(BedroomWallSconcesPowerControlModule.class);

    private static final long SCONCES_LIGHT_SW_BT_LEFT = 90L;
    private static final long SCONCES_LIGHT_SW_BT_RIGHT = 91L;

    private final Set<Long> portsToListen = new HashSet<>();
    private final AtomicBoolean lightState = new AtomicBoolean(false);

    public BedroomWallSconcesPowerControlModule(SystemManager mgr, ModuleConfigEntry configEntry) {
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
            if (SCONCES_LIGHT_SW_BT_LEFT == event.getPortId()) { // garage door sensor
                enableLightByClickEvent(event);
            } else if (SCONCES_LIGHT_SW_BT_RIGHT == event.getPortId()) { // garage gate sensor
                enableLightByClickEvent(event);
            }
        }
    }

    private void enableLightByClickEvent(BinaryInputInitiatedHwEvent event) {
        if (Objects.requireNonNull(event.getMode()) == BinaryPortStatus.ENABLED) {
            try {
                boolean wasEnabled = lightState.get();
                lightState.set(setOutputStatus(!wasEnabled ? OutputPortStatus.enabled() : OutputPortStatus.disabled()).isEnabled());
            } catch (Exception ex) {
                log.error("Unable to switch light by event initiated by port# " + event.getPortId(), ex);
            }
        }
    }

}
