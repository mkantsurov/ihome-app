package technology.positivehome.ihome.server.service.core.module;

import com.google.common.eventbus.Subscribe;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import technology.positivehome.ihome.domain.constant.ModuleOperationMode;
import technology.positivehome.ihome.domain.runtime.event.BinaryInputInitiatedHwEvent;
import technology.positivehome.ihome.domain.runtime.module.ModuleConfigEntry;
import technology.positivehome.ihome.domain.runtime.module.OutputPortStatus;
import technology.positivehome.ihome.server.service.core.SystemManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by maxim on 4/28/20.
 **/
public class HomeLightDayLightDependentMotionSensorRelayBasedControlModule extends HomeLightMotionSensorRelayBasedControlModule {

    private static final Log log = LogFactory.getLog(HomeLightDayLightDependentMotionSensorRelayBasedControlModule.class);

    public HomeLightDayLightDependentMotionSensorRelayBasedControlModule(SystemManager mgr, ModuleConfigEntry configEntry) {
        super(mgr, configEntry);
    }

    @Subscribe
    public void handleEvent(BinaryInputInitiatedHwEvent event) throws IOException {
        if (ModuleOperationMode.AUTO.equals(getMode()) && portsToListen.contains(event.getPortId())) {
            boolean wasEnabled = lightState.get();
            timeWhenLiteEnabled.set(System.currentTimeMillis());
            if (!wasEnabled) {
                try {
                    if (getMgr().getInputPowerSupplySourceCalc().getAvgValue(TimeUnit.MINUTES.toMillis(3)) < 1000.0) {
                        lightState.set(setOutputStatus(OutputPortStatus.enabled()).isEnabled());
                    }
                } catch (Exception ex) {
                    log.error("Unable enable light by event initiated by port# " + event.getPortId(), ex);
                }
            }
        }
    }
}
