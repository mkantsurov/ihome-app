package technology.positivehome.ihome.server.service.core.module;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import technology.positivehome.ihome.domain.constant.ModuleProperty;
import technology.positivehome.ihome.domain.runtime.event.BinaryInputInitiatedHwEvent;
import technology.positivehome.ihome.domain.runtime.module.ModuleConfigElementEntry;
import technology.positivehome.ihome.domain.runtime.module.ModuleConfigEntry;
import technology.positivehome.ihome.domain.runtime.module.ModulePropertyValue;
import technology.positivehome.ihome.domain.runtime.module.OutputPortStatus;
import technology.positivehome.ihome.domain.runtime.sensor.Bme280TempHumidityPressureSensorData;
import technology.positivehome.ihome.domain.runtime.sensor.Dht21TempHumiditySensorData;
import technology.positivehome.ihome.server.service.core.SystemManager;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by maxim on 7/2/19.
 **/
public class BathRoomVentilationControlModule extends AbstractRelayBasedIHomeModule implements IHomeModule {

    private static final Log log = LogFactory.getLog(BathRoomVentilationControlModule.class);
    public static final int DIFF_IN_HUMIDITY_INDOOR_BATHROOM = 18;
    private static final long MILLS_VENTILATION_TIME = TimeUnit.MINUTES.toMillis(2L);

    private final Optional<ModulePropertyValue> checkHumidityInterval;
    private final Optional<ModulePropertyValue> bathRoomHumiditySensorId;
    private final Optional<ModulePropertyValue> indoorHumiditySensorId;
    private final Optional<ModulePropertyValue> bathRoomMotionSensorId;

    private final Map<Long, Long> portsToListen = new ConcurrentHashMap<>();
    private final CronModuleJob[] moduleJobs;
    private AtomicBoolean state = new AtomicBoolean(false);
    private AtomicLong timeWhenEnabled = new AtomicLong(0L);
    private AtomicLong timeMotionDetected = new AtomicLong(0L);

    public BathRoomVentilationControlModule(SystemManager mgr, ModuleConfigEntry configEntry) {
        super(mgr, configEntry);

        checkHumidityInterval = getProperty(ModuleProperty.CHECK_BATH_ROOM_HUMIDITY_INTERVAL);
        bathRoomHumiditySensorId = getProperty(ModuleProperty.BATH_ROOM_HUMIDITY_SENSOR);
        indoorHumiditySensorId = getProperty(ModuleProperty.INDOOR_HUMIDITY_SENSOR);
        bathRoomMotionSensorId = getProperty(ModuleProperty.BATH_ROOM_MOTION_SENSOR);

        for (ModuleConfigElementEntry ent : getInputPorts()) {
            portsToListen.put(ent.getPort(), ent.getId());
        }

        if (checkHumidityInterval.isPresent() && bathRoomHumiditySensorId.isPresent() && indoorHumiditySensorId.isPresent()) {

            final long checkPeriod = TimeUnit.SECONDS.toMillis(checkHumidityInterval.get().getLongValue());

            moduleJobs = new CronModuleJob[]{new CronModuleJob(checkPeriod) {
                @Override
                protected void execute() throws Exception {
                    switch (getMode()) {
                        case AUTO:
                            OutputPortStatus status = getOutputPortStatus();
                            Dht21TempHumiditySensorData data = getTemperatureHumiditySensorData(bathRoomHumiditySensorId.get().getLongValue());
                            Bme280TempHumidityPressureSensorData indoorData = getBme280TempHumidityPressureSensorReading(indoorHumiditySensorId.get().getLongValue());

                            double diff = 0;

                            if (data.getHumidity() > 0 && indoorData.getHumidity() > 0) {
                                diff = data.getHumidity() - indoorData.getHumidity();
                            }

                            if (System.currentTimeMillis() - timeMotionDetected.get() < MILLS_VENTILATION_TIME || diff > DIFF_IN_HUMIDITY_INDOOR_BATHROOM) {
                                if (!status.isEnabled() || status.isUndefined()) {
                                    setOutputStatus(OutputPortStatus.enabled());
                                }
                            } else if (diff <= DIFF_IN_HUMIDITY_INDOOR_BATHROOM && (status.isEnabled() || status.isUndefined())) {
                                setOutputStatus(OutputPortStatus.disabled());
                            }
                            break;
                    }
                }
            }};
        } else {
            moduleJobs = new CronModuleJob[0];
        }

    }

    @Override
    protected CronModuleJob[] getJobList() {
        return moduleJobs;
    }

    public void handleEvent(BinaryInputInitiatedHwEvent event) {
        Long elementId;
        if ((elementId = portsToListen.get(event.getPortId())) != null) {
            if (bathRoomMotionSensorId.isPresent() && bathRoomMotionSensorId.get().getLongValue().equals(elementId)) {
                timeMotionDetected.set(System.currentTimeMillis());
            }
        }
    }
}
