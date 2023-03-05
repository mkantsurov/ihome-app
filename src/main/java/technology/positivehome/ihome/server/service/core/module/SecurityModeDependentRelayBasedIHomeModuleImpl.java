package technology.positivehome.ihome.server.service.core.module;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import technology.positivehome.ihome.domain.constant.BinaryPortStatus;
import technology.positivehome.ihome.domain.runtime.event.BinaryInputInitiatedHwEvent;
import technology.positivehome.ihome.domain.runtime.module.ModuleConfigElementEntry;
import technology.positivehome.ihome.domain.runtime.module.ModuleConfigEntry;
import technology.positivehome.ihome.domain.runtime.module.OutputPortStatus;
import technology.positivehome.ihome.server.model.command.IHomeCommandFactory;
import technology.positivehome.ihome.server.service.core.SystemManager;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static technology.positivehome.ihome.server.processor.SystemProcessor.SECURITY_MODE_SENSOR_PORT_ID;

/**
 * Created by maxim on 2/21/21.
 **/
public class SecurityModeDependentRelayBasedIHomeModuleImpl extends AbstractRelayBasedIHomeModule {

    private static final Log log = LogFactory.getLog(SecurityModeDependentRelayBasedIHomeModuleImpl.class);
    private final CronModuleJob[] moduleJobs;
    private final Set<Long> portsToListen = new HashSet<>();
    private final AtomicBoolean powerState = new AtomicBoolean(false);

    public SecurityModeDependentRelayBasedIHomeModuleImpl(SystemManager mgr, ModuleConfigEntry configEntry) {
        super(mgr, configEntry);
        moduleJobs = new CronModuleJob[]{
                new CronModuleJob(TimeUnit.MINUTES.toMillis(15)) {
                    @Override
                    protected void execute() throws Exception {
                        switch (getMode()) {
                            case AUTO:
                                BinaryPortStatus state = getMgr().runCommand(IHomeCommandFactory.cmdGetBinarySensorReading(SECURITY_MODE_SENSOR_PORT_ID));
                                switch (state) {
                                    case ENABLED:
                                        if (powerState.get()) {
                                            powerState.set(setOutputStatus(OutputPortStatus.disabled()).isEnabled());
                                        }
                                        break;
                                    case DISABLED:
                                        if (!powerState.get()) {
                                            powerState.set(setOutputStatus(OutputPortStatus.enabled()).isEnabled());
                                        }
                                        break;
                                }
                        }
                    }
                }
        };

        for (ModuleConfigElementEntry ent : getInputPorts()) {
            portsToListen.add(ent.getPort());
        }
    }

    public void handleEvent(BinaryInputInitiatedHwEvent event) {
        if (portsToListen.contains(event.getPortId())) {
            try {
                boolean wasEnabled = powerState.get();
                powerState.set(setOutputStatus(wasEnabled ? OutputPortStatus.disabled() : OutputPortStatus.enabled()).isEnabled());
            } catch (Exception ex) {
                log.error("Unable to switch light", ex);
            }

        }
    }

    @Override
    protected CronModuleJob[] getJobList() {
        return moduleJobs;
    }
}
