package technology.positivehome.ihome.server.service.core.module;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import technology.positivehome.ihome.domain.constant.BinaryPortStatus;
import technology.positivehome.ihome.domain.constant.ModuleOperationMode;
import technology.positivehome.ihome.domain.constant.UiControlType;
import technology.positivehome.ihome.domain.runtime.event.BinaryInputInitiatedHwEvent;
import technology.positivehome.ihome.domain.runtime.module.ModuleConfigElementEntry;
import technology.positivehome.ihome.domain.runtime.module.ModuleConfigEntry;
import technology.positivehome.ihome.domain.runtime.module.OutputPortStatus;
import technology.positivehome.ihome.server.service.core.SystemManager;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class BathRoomMirrorLightPowerControlModule extends AbstractRelayBasedIHomeModule implements IHomeModule {

    private static final Log log = LogFactory.getLog(BathRoomMirrorLightPowerControlModule.class);

    public static final long LIGHT_TTL = TimeUnit.MINUTES.toMillis(5L);

    private final CronModuleJob[] moduleJobs;
    private final AtomicLong timeMotionDetected = new AtomicLong(0L);
    private final Map<Long, String> portsToListen = new HashMap<>();
    private final AtomicBoolean lightState = new AtomicBoolean(false);

    public BathRoomMirrorLightPowerControlModule(SystemManager mgr,
                                                 ModuleConfigEntry configEntry) {
        super(mgr, configEntry);
        moduleJobs = new CronModuleJob[]{
                new CronModuleJob(TimeUnit.MINUTES.toMillis(1)) {
                    @Override
                    protected void execute() throws Exception {
                        if (Objects.requireNonNull(getMode()) == ModuleOperationMode.AUTO) {
                            lightState.set(getOutputPortStatus().isEnabled());
                            if (lightState.get() && timeMotionDetected.get() + LIGHT_TTL < System.currentTimeMillis()) {
                                lightState.set(setOutputStatus(OutputPortStatus.disabled()).isEnabled());
                            }
                        }
                    }
                }
        };
        for (ModuleConfigElementEntry ent : getInputPorts()) {
            portsToListen.put(ent.getPort(), ent.getName());
        }
    }

    @Override
    protected CronModuleJob[] getJobList() {
        return moduleJobs;
    }

    public void handleEvent(BinaryInputInitiatedHwEvent event) {
        Optional.ofNullable(portsToListen.get(event.getPortId())).ifPresent(s -> {
            timeMotionDetected.set(System.currentTimeMillis());
            if (!s.contains("motion")) {
                enableByClickEvent(event);
            }
        });
    }

    private void enableByClickEvent(BinaryInputInitiatedHwEvent event) {
        try {
            boolean wasEnabled = lightState.get();
            lightState.set(setOutputStatus(!wasEnabled ? OutputPortStatus.enabled() : OutputPortStatus.disabled()).isEnabled());
        } catch (Exception ex) {
            log.error("Unable to switch light by event initiated by port# " + event.getPortId(), ex);
        }
    }

}
