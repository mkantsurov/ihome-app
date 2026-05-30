package technology.positivehome.ihome.server.service.core.module;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import technology.positivehome.ihome.domain.constant.*;
import technology.positivehome.ihome.domain.runtime.event.BinaryInputInitiatedHwEvent;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.domain.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.domain.runtime.module.*;
import technology.positivehome.ihome.server.model.command.IHomeCommandFactory;
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

    private final SystemManager mgr;

    private final long moduleId;
    private final String name;
    private final AtomicReference<ModuleOperationMode> moduleOperationMode = new AtomicReference<>(ModuleOperationMode.UNDEFINED);
    private final AtomicReference<ModuleStartupMode> moduleStartupMode = new AtomicReference<>(ModuleStartupMode.DISABLED);

    private final Map<Long, ModuleConfigElementEntry> inputPorts = new HashMap<>();
    private final Set<Long> inputPortIds = new HashSet<>();
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
        this.moduleStartupMode.set(configEntry.getStartupMode());
        this.groupId = configEntry.getModuleGroupEntry().getId();

        for (ModuleConfigElementEntry ent : configEntry.getControls()) {
            switch (ent.getType()) {
                case RELAY:
                case DIMMER:
                    break;
                default:
                    inputPorts.put(ent.getId(), ent);
                    inputPortIds.add(ent.getPort());
            }
        }

        for (ModulePropertyValue ent : configEntry.getProperties()) {
            properties.put(ent.getKey(), ent);
        }
    }

    public void initDefaultState() {
        try {
            setOutputStatus(ModuleStartupMode.ENABLED.equals(moduleStartupMode.get()) ? OutputPortStatus.enabled() : OutputPortStatus.disabled());
            log.info("Module " + getName() + " initialized with status " + moduleStartupMode.get().name());
        } catch (Exception e) {
            log.error("Unable to initialize default module state", e);
        }
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
            ModuleConfigElementEntry cfg = inputPorts.get(entry.getId());
            switch (entry.getType()) {
                case BUTTON:
                case REED_SWITCH:
                    state.getBinarySensorData().put(entry.getId(), mgr.runCommand(IHomeCommandFactory.cmdGetBinarySensorReading(cfg.getPort())));
                    break;
                case DS18B20_TEMP_SENSOR:
                    state.getTemperatureSensorData().put(entry.getId(), mgr.runCommand(IHomeCommandFactory.cmdGetDs1820TemperatureSensorReading(cfg.getPort())));
                    break;
                case DHT21_TEMP_HUMIDITY_SENSOR:
                    state.getTempHumiditySensorData().put(entry.getId(), mgr.runCommand(IHomeCommandFactory.cmdGetDht21TempHumiditySensorReading(cfg.getPort())));
                    break;
                case BME280_TEMP_HUMIDITY_PRESS_SENSOR:
                    state.getBme280TempHumidityPressureSensorData().put(entry.getId(), mgr.runCommand(IHomeCommandFactory.cmdGetBme280TempHumidityPressureSensorReading(cfg.getPort())));
                    break;
                case TSL2591_LUMINOSITY_SENSOR:
                    state.getTsl2591LuminositySensorData().put(entry.getId(), mgr.runCommand(IHomeCommandFactory.cmdGetTsl2591LuminositySensorReading(cfg.getPort())));
                    break;
                case DDS_238_POWER_METER:
                    state.getDds238PowerMeterData().put(entry.getId(), mgr.runCommand(IHomeCommandFactory.cmdGetDds238Reading(cfg.getPort())));
                    break;
            }
        }
        state.setOutputPortStatus(getOutputPortStatus());
        return state;
    }

    public void onButtonClick(long buttonId) {
        ModuleConfigElementEntry conf = inputPorts.get(buttonId);
        mgr.getEventPublisher().publishEvent(new BinaryInputInitiatedHwEvent(this, conf.getPort(), IHomePortType.BINARY_INPUT, new ControllerEventInfo.Builder().count("1").mode("1").build()));
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
            results.add(getOutputPortStatus(ent));
        }
        return OutputPortStatus.summarize(results);
    }

    protected abstract OutputPortStatus getOutputPortStatus(ModuleConfigElementEntry ent) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException;

    public OutputPortStatus setOutputStatus(OutputPortStatus status) throws URISyntaxException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, MegadApiMallformedUrlException, InterruptedException {

        List<ModuleConfigElementEntry> outputPorts = getOutputPorts();

        List<OutputPortStatus> results = new ArrayList<>();
        for (ModuleConfigElementEntry ent : outputPorts) {
            results.add(updateOutputPortState(ent, status));
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

    public boolean hasInputPort(long portId) {
        return inputPortIds.contains(portId);
    }

    protected abstract List<ModuleConfigElementEntry> getOutputPorts();

    protected abstract OutputPortStatus updateOutputPortState(ModuleConfigElementEntry port, OutputPortStatus status) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, InterruptedException, IOException;

    @Override
    public ModuleAssignment getAssignment() {
        return assignment;
    }

    @Override
    public ModuleOperationMode getMode() {
        return moduleOperationMode.get();
    }

    @Override
    public ModuleStartupMode getStartupMode() {
        return moduleStartupMode.get();
    }

    @Override
    public void  updateStartupMode(ModuleStartupMode moduleStartupMode) {
        this.moduleStartupMode.set(moduleStartupMode);
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

    protected long controllerPort(Long moduleElementId) {
        ModuleConfigElementEntry conf = inputPorts.get(moduleElementId);
        if (conf == null) {
            throw new IllegalArgumentException("Module element with ID# " + moduleElementId + " is absent in module configuration");
        }
        return conf.getPort();
    }

    /**
     * Override that method to handle event properly
     * @param event
     */
    public void handleEvent(BinaryInputInitiatedHwEvent event) {}

}
