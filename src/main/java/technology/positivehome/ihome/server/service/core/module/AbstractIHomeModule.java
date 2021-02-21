package technology.positivehome.ihome.server.service.core.module;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import technology.positivehome.ihome.domain.constant.*;
import technology.positivehome.ihome.domain.runtime.event.BinaryInputInitiatedHwEvent;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.domain.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.domain.runtime.module.*;
import technology.positivehome.ihome.domain.runtime.sensor.Bme280TempHumidityPressureSensorData;
import technology.positivehome.ihome.domain.runtime.sensor.Dht21TempHumiditySensorData;
import technology.positivehome.ihome.domain.runtime.sensor.Ds18b20TempSensorData;
import technology.positivehome.ihome.domain.runtime.sensor.Tsl2591LuminositySensorData;
import technology.positivehome.ihome.server.service.core.SystemManager;
import technology.positivehome.ihome.server.service.core.controller.ControllerEventInfo;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by maxim on 7/1/19.
 **/
public abstract class AbstractIHomeModule implements IHomeModuleSummary {

    private static final Log log = LogFactory.getLog(AbstractIHomeModule.class);
    private final long groupId;
    private final ModuleAssignment assignment;

    private SystemManager mgr;

    private final long moduleId;
    private final String name;
    private final AtomicReference<ModuleOperationMode> moduleOperationMode = new AtomicReference<>(ModuleOperationMode.UNDEFINED);

    private final Map<Long, ModuleConfigElementEntry> inputPorts = new HashMap<>();
    private final Map<ModuleProperty, ModulePropertyValue> properties = new HashMap<>();


    private final AtomicLong lastEnableEventTs = new AtomicLong(0);
    private final AtomicLong lastDisableEventTs = new AtomicLong(0);
    protected final AtomicReference<OutputPortStatus> lastPortState = new AtomicReference<>(OutputPortStatus.undefined());

    public AbstractIHomeModule(SystemManager mgr, ModuleConfigEntry configEntry) {

        this.mgr = mgr;
        this.moduleId = configEntry.getId();
        this.name = configEntry.getModuleName();
        this.assignment = configEntry.getModuleAssignment();
        this.moduleOperationMode.set(configEntry.getMode());
        this.groupId = configEntry.getModuleGroupEntry().getId();

        for (ModuleConfigElementEntry ent : configEntry.getControls()) {
            switch (ent.getType()) {
                case RELAY:
                case DIMMER:
                    break;
                default:
                    inputPorts.put(ent.getId(), ent);
            }
        }

        for (ModulePropertyValue ent : configEntry.getProperties()) {
            properties.put(ent.getKey(), ent);
        }
    }

    public BinaryPortStatus getBinarySensorData(long elementId) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException {
        ModuleConfigElementEntry cfg = inputPorts.get(elementId);
        switch (cfg.getType()) {
            case BUTTON:
                return mgr.getBinSensorsState(cfg.getPort());
            case REED_SWITCH:
                return mgr.getBinSensorsState(cfg.getPort());
        }
        throw new IllegalStateException("Incompatible element " + cfg.getName() + " requested to get binary sensor reading");
    }

    public Ds18b20TempSensorData getTemperatureSensorReading(long elementId) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException {
        ModuleConfigElementEntry conf = inputPorts.get(elementId);
        if (conf == null) {
            throw new IllegalArgumentException("Element with ID# " + elementId + " is absent in module configuration");
        }
        return mgr.getDs18b20SensorReading(conf.getPort());
    }

    public Dht21TempHumiditySensorData getTemperatureHumiditySensorData(long elementId) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException {
        ModuleConfigElementEntry conf = inputPorts.get(elementId);
        if (conf == null) {
            throw new IllegalArgumentException("Element with ID# " + elementId + " is absent in module configuration");
        }
        return mgr.getDht21TempHumiditySensorReading(conf.getPort());
    }

    public Bme280TempHumidityPressureSensorData getBme280TempHumidityPressureSensorReading(long elementId) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException {
        ModuleConfigElementEntry conf = inputPorts.get(elementId);
        if (conf == null) {
            throw new IllegalArgumentException("Element with ID# " + elementId + " is absent in module configuration");
        }
        return mgr.getBme280TempHumidityPressureSensorReading(conf.getPort());
    }

    public Tsl2591LuminositySensorData getTsl2591LuminositySensorReading(long elementId) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException {
        ModuleConfigElementEntry conf = inputPorts.get(elementId);
        if (conf == null) {
            throw new IllegalArgumentException("Element with ID# " + elementId + " is absent in module configuration");
        }
        return mgr.getTsl2591LuminositySensorReading(conf.getPort());
    }

    public long getLastEnableEventTs() {
        return lastEnableEventTs.get();
    }

    public long getLastDisableEventTs() {
        return lastDisableEventTs.get();
    }

    protected abstract CronModuleJob[] getJobList();

    public void runCronTasks() {
        try {
            for (CronModuleJob job : getJobList()) {
                if (job.isNeedsToBeExecuted()) {
                    job.run();
                }
            }
        } catch (Exception ex) {
            log.error("Unable to execute cron tasks for module: " + name + " " + ex.getMessage());
        }
    }

    public List<ModuleConfigElementEntry> getInputPorts() {
        return new ArrayList<>(inputPorts.values());
    }

    protected SystemManager getMgr() {
        return mgr;
    }

    public ModuleState getSensorReadings() throws MegadApiMallformedUrlException, IOException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, URISyntaxException, InterruptedException {
        ModuleState state = new ModuleState();

        for (ModuleConfigElementEntry entry : inputPorts.values()) {
            switch (entry.getType()) {
                case BUTTON:
                    state.getBinarySensorData().put(entry.getId(), getBinarySensorData(entry.getId()));
                    break;
                case REED_SWITCH:
                    state.getBinarySensorData().put(entry.getId(), getBinarySensorData(entry.getId()));
                    break;
                case DS18B20_TEMP_SENSOR:
                    state.getTemperatureSensorData().put(entry.getId(), getTemperatureSensorReading(entry.getId()));
                    break;
                case DHT21_TEMP_HUMIDITY_SENSOR:
                    state.getTempHumiditySensorData().put(entry.getId(), getTemperatureHumiditySensorData(entry.getId()));
                    break;
                case BME280_TEMP_HUMIDITY_PRESS_SENSOR:
                    state.getBme280TempHumidityPressureSensorData().put(entry.getId(), getBme280TempHumidityPressureSensorReading(entry.getId()));
                    break;
                case TSL2591_LUMINOSITY_SENSOR:
                    state.getTsl2591LuminositySensorData().put(entry.getId(), getTsl2591LuminositySensorReading(entry.getId()));
                    break;
            }
        }
        state.setOutputPortStatus(getOutputPortStatus());
        return state;
    }

    public void onButtonClick(long buttonId) {
        ModuleConfigElementEntry conf = inputPorts.get(buttonId);
        mgr.getEventBus().post(new BinaryInputInitiatedHwEvent(conf.getPort(), MegadPortType.BINARY_INPUT, new ControllerEventInfo.Builder().count("1").mode("1").build()));
    }

    public ModuleOperationMode onUpdateMode(ModuleOperationMode newState) {
        ModuleOperationMode newMode = ModuleOperationMode.AUTO.equals(newState) ? ModuleOperationMode.AUTO : ModuleOperationMode.MANUAL;
        mgr.saveModuleMode(moduleId, newMode);
        moduleOperationMode.set(newMode);
        return moduleOperationMode.get();
    }

    public OutputPortStatus getOutputPortStatus() throws URISyntaxException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, MegadApiMallformedUrlException, InterruptedException {
        List<ModuleConfigElementEntry> outputPorts = getOutputPorts();

        List<OutputPortStatus> results = new ArrayList<>();
        for (ModuleConfigElementEntry ent : outputPorts) {
            results.add(getOutputPortStatus(ent.getPort()));
        }
        return OutputPortStatus.summarize(results);
    }

    protected abstract OutputPortStatus getOutputPortStatus(long port) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException;

    public OutputPortStatus setOutputStatus(OutputPortStatus status) throws URISyntaxException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, MegadApiMallformedUrlException, InterruptedException {

        List<ModuleConfigElementEntry> outputPorts = getOutputPorts();

        List<OutputPortStatus> results = new ArrayList<>();
        for (ModuleConfigElementEntry ent : outputPorts) {
            results.add(updateOutputPortState(ent.getPort(), status));
        }
        OutputPortStatus curStatus = OutputPortStatus.summarize(results);
        OutputPortStatus previousState = lastPortState.getAndSet(curStatus);

        if (!previousState.isEnabled() && curStatus.isEnabled()) {
            lastEnableEventTs.set(System.currentTimeMillis());
        } else if (previousState.isEnabled() && !curStatus.isEnabled() && !curStatus.isUndefined()) {
            lastDisableEventTs.set(System.currentTimeMillis());
        }
        return curStatus;
    }

    protected abstract List<ModuleConfigElementEntry> getOutputPorts();

    protected abstract OutputPortStatus updateOutputPortState(long port, OutputPortStatus status) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, InterruptedException, IOException;

    @Override
    public ModuleAssignment getAssignment() {
        return assignment;
    }

    @Override
    public ModuleOperationMode getMode() {
        return moduleOperationMode.get();
    }

    @Override
    public long getModuleId() {
        return moduleId;
    }

    @Override
    public long getGroupId() {
        return groupId;
    }

    public Optional<ModulePropertyValue> getProperty(ModuleProperty key) {
        return Optional.ofNullable(properties.get(key));
    }

    @Override
    public String getName() {
        return name;
    }

}
