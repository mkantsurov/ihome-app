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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by maxim on 7/2/19.
 **/
public class GarageLightPowerControlModule extends AbstractRelayBasedIHomeModule implements IHomeModule {

    private static final Log log = LogFactory.getLog(GarageLightPowerControlModule.class);

    public static final long LIGHT_TTL = TimeUnit.SECONDS.toMillis(90L);

    private static final long GARAGE_LIGHT_SW_BT = 6L;
    private static final long GARAGE_DORS_SENSOR = 7L;
    private static final long GARAGE_GATE_SENSOR = 8L;
    private static final long GARAGE_MOTION_SENSOR = 87L;

    private final CronModuleJob[] moduleJobs;
    private AtomicLong timeWhenLiteEnabled = new AtomicLong(0L);
    private final Set<Long> portsToListen = new HashSet<>();
    private AtomicBoolean lightState = new AtomicBoolean(false);

    public GarageLightPowerControlModule(SystemManager mgr, ModuleConfigEntry configEntry) {
        super(mgr, configEntry);
        moduleJobs = new CronModuleJob[]{
                new CronModuleJob(TimeUnit.MINUTES.toMillis(1)) {
                    @Override
                    protected void execute() throws Exception {
                        switch (getMode()) {
                            case AUTO:
                                lightState.set(getOutputPortStatus().isEnabled());
                                if (timeWhenLiteEnabled.get() + LIGHT_TTL < System.currentTimeMillis()) {
                                    lightState.set(setOutputStatus(OutputPortStatus.disabled()).isEnabled());
                                }
                                break;
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
            if (GARAGE_LIGHT_SW_BT == event.getPortId()) { //garage light switch
                try {
                    boolean wasEnabled = lightState.get();
                    lightState.set(setOutputStatus(wasEnabled ? OutputPortStatus.disabled() : OutputPortStatus.enabled()).isEnabled());
                    if (!wasEnabled && lightState.get()
                            && !PreferredPowerSupplyMode.ONLY_LED.equals(getMgr().getInputPowerSupplySourceCalc().getPreferredPowerSupplyMode())) {
                        timeWhenLiteEnabled.set(System.currentTimeMillis());
                    }
                } catch (Exception ex) {
                    log.error("Unable to switch light", ex);
                }
            } else if (GARAGE_DORS_SENSOR == event.getPortId()) { // garage door sensor
                enableLightByDorsEvent(event);
            } else if (GARAGE_GATE_SENSOR == event.getPortId()) { // garage gate sensor
                enableLightByDorsEvent(event);
            } else if (GARAGE_MOTION_SENSOR == event.getPortId()) {
                enableLightByDorsEvent(event);
            }
        }
    }

    private void enableLightByDorsEvent(BinaryInputInitiatedHwEvent event) {
        if (lightState.get()) {
            timeWhenLiteEnabled.set(System.currentTimeMillis());
            return;
        }
        switch (event.getMode()) {
            case ENABLED:
                try {
                    lightState.set(setOutputStatus(OutputPortStatus.enabled()).isEnabled());
                    timeWhenLiteEnabled.set(System.currentTimeMillis());
                } catch (Exception ex) {
                    log.error("Unable to switch light by event initiated by port# " + event.getPortId(), ex);
                }
                break;
        }
    }
}
