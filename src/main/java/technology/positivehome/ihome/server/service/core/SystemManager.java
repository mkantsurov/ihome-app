package technology.positivehome.ihome.server.service.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import technology.positivehome.ihome.domain.constant.BinaryPortStatus;
import technology.positivehome.ihome.domain.constant.ModuleOperationMode;
import technology.positivehome.ihome.domain.runtime.controller.ControllerConfigEntry;
import technology.positivehome.ihome.domain.runtime.controller.ControllerPortConfigEntry;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.domain.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.domain.runtime.module.ModuleConfigEntry;
import technology.positivehome.ihome.domain.runtime.module.ModuleState;
import technology.positivehome.ihome.domain.runtime.module.OutputPortStatus;
import technology.positivehome.ihome.domain.runtime.sensor.Bme280TempHumidityPressureSensorData;
import technology.positivehome.ihome.domain.runtime.sensor.Dht21TempHumiditySensorData;
import technology.positivehome.ihome.domain.runtime.sensor.Ds18b20TempSensorData;
import technology.positivehome.ihome.domain.runtime.sensor.Tsl2591LuminositySensorData;
import technology.positivehome.ihome.server.persistence.ControllerConfigRepository;
import technology.positivehome.ihome.server.persistence.ModuleConfigRepository;
import technology.positivehome.ihome.server.service.core.controller.ControllerEventInfo;
import technology.positivehome.ihome.server.service.core.controller.EmulatedIHomeControllerImpl;
import technology.positivehome.ihome.server.service.core.controller.IHomeController;
import technology.positivehome.ihome.server.service.core.controller.IHomeControllerImpl;
import technology.positivehome.ihome.server.service.core.module.*;
import technology.positivehome.ihome.server.service.util.IHomeEventBus;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;

import static technology.positivehome.ihome.server.processor.SystemProcessor.LUMINOSITY_SENSOR_ID;

/**
 * Created by maxim on 6/24/19.
 **/
@Component
@EnableScheduling
public class SystemManager implements ControllerEventListener, InitializingBean {

    private static final Log log = LogFactory.getLog(SysConfigImpl.class);
    private final SysConfig sysConfig;
    private final ControllerConfigRepository controllerConfigRepository;
    private final ModuleConfigRepository moduleConfigRepository;

    private final Map<String, Long> controllerIdByAddress = new ConcurrentHashMap<>();
    private final Map<Long, Long> controllerIdByPort = new ConcurrentHashMap<>();
    private final Map<Long, IHomeController> controllerById = new ConcurrentHashMap<>();
    private final Map<Long, AbstractIHomeModule> moduleById = new ConcurrentHashMap<>();
    private final ForkJoinPool moduleCronExecutionPool = new ForkJoinPool(3);
    private final InputPowerSupplySourceCalc inputPowerSupplySourceCalc;


    @Autowired
    public SystemManager(SysConfig sysConfig, ControllerConfigRepository controllerConfigRepository, ModuleConfigRepository moduleConfigRepository, InputPowerSupplySourceCalc inputPowerSupplySourceCalc) {
        this.sysConfig = sysConfig;
        this.controllerConfigRepository = controllerConfigRepository;
        this.moduleConfigRepository = moduleConfigRepository;
        this.inputPowerSupplySourceCalc = inputPowerSupplySourceCalc;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for (ControllerConfigEntry configEntry : controllerConfigRepository.loadControllerConfig()) {
            IHomeController cnt;
            switch (sysConfig.getControllerMode()) {
                case LIVE:
                    cnt = new IHomeControllerImpl(getEventBus(), configEntry);
                    break;
                default:
                    cnt = new EmulatedIHomeControllerImpl(getEventBus(), configEntry);
            }
            controllerById.put(configEntry.getId(), cnt);
            controllerIdByAddress.put(configEntry.getIpAddress(), configEntry.getId());

            for (ControllerPortConfigEntry portConfig : configEntry.getPortConfig()) {
                controllerIdByPort.put(portConfig.getId(), configEntry.getId());
            }

        }

        AbstractIHomeModule moduleToInit;
        //TODO: Implement initialization by type there
        for (ModuleConfigEntry configEntry : moduleConfigRepository.loadModuleConfig()) {
            log.info("Initializing " + configEntry.getType().name());
            switch (configEntry.getType()) {
                case GENERIC_RELAY_POWER_CONTROL_MODULE:
                    moduleToInit = new GenericRelayPowerControlModule(this, configEntry);
                    break;
                case GENERIC_DIMMER_POWER_CONTROL_MODULE:
                    moduleToInit = new GenericDimmerPowerControlModule(this, configEntry);
                    break;
                case GENERIC_INPUT_POWER_DEPENDENT_RELAY_POWER_CONTROL_MODULE:
                    moduleToInit = new GenericInputPowerDependentRelayPowerControlModule(this, configEntry);
                    break;
                case GARAGE_LIGHT__POWER_CONTROL_MODULE:
                    moduleToInit = new GarageLightPowerControlModule(this, configEntry);
                    break;
                case GARAGE_VENTILATION_POWER_CONTROL_MODULE:
                    moduleToInit = new GarageVentilationControlModule(this, configEntry);
                    break;
                case HEATING_SYSTEM_PUMP_POWER_CONTROL_MODULE:
                    moduleToInit = new HeatingSystemPumpControlModule(this, configEntry);
                    break;
                case HEAT_WATER_RECIRQULATION_POWER_CONTROL_MODULE:
                    moduleToInit = new HeatWaterRecirculationPumpControlModule(this, configEntry);
                    break;
                case HOME_LIGHT_RELAY_POWER_CONTROL_MODULE:
                    moduleToInit = new HomeLightRelayBasedPowerControlModule(this, configEntry);
                    break;
                case HOME_LIGHT_DIMMABLE_POWER_CONTROL_MODULE:
                    moduleToInit = new HomeLightDimmerBasedPowerControlModule(this, configEntry);
                    break;
                case HOME_LIGHT_MOVENMENT_SENSOR_RELAY_BASED_CONTROL_MODULE:
                    moduleToInit = new HomeLightMotionSensorRelayBasedControlModule(this, configEntry);
                    break;
                case HOME_LIGHT_DAYLIGHT_DEPENDENT_MOVENMENT_SENSOR_RELAY_BASED_CONTROL_MODULE:
                    moduleToInit = new HomeLightDayLightDependentMotionSensorRelayBasedControlModule(this, configEntry);
                    break;
                case HOME_VENTILATION_MOVENMENT_HUMIDITY_SENSOR_RELAY_BASED_CONTROL_MODULE:
                    moduleToInit = new BathRoomVentilationControlModule(this, configEntry);
                    break;
                case DIRECT_INPUT_POWER_SUPPLY_CONTROL_MODULE:
                    moduleToInit = new DirectInputPowerSupplyControlModule(this, configEntry);
                    break;
                case CONVERTER_INPUT_POWER_SUPPLY_CONTROL_MODULE:
                    moduleToInit = new ConverterInputPowerSupplyControlModule(this, configEntry);
                    break;
                case SECURITY_MODE_DEPENDENT_RELAY_BASED_IHOME_MODULE:
                    moduleToInit = new SecurityModeDependentRelayBasedIHomeModuleImpl(this, configEntry);
                    break;

                default:
                    throw new IllegalStateException("Module with #" + configEntry.getId() + " can't be initialized. (no config available)");
            }
            moduleById.put(moduleToInit.getModuleId(), moduleToInit);
        }
    }

    public IHomeController getController(Long controllerId) {
        IHomeController result;
        if ((result = controllerById.get(controllerId)) != null) {
            return result;
        }
        throw new IllegalArgumentException("Controller #" + controllerId + " is not defined");
    }

    public boolean getBinOutputStatus(long portId) throws PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, MegadApiMallformedUrlException, InterruptedException {
        return getController(controllerIdByPort.get(portId)).getRelayStatus(portId);
    }

    public boolean updateBinPortState(long portId, boolean enabled) throws PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, MegadApiMallformedUrlException, InterruptedException {
        return getController(controllerIdByPort.get(portId)).setRelayState(portId, enabled);
    }

    public int getDimmerOutputStatus(long portId) throws PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, MegadApiMallformedUrlException, InterruptedException {
        return getController(controllerIdByPort.get(portId)).getDimmerStatus(portId);
    }

    public int updateDimmerPortState(long portId, int value) throws PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, MegadApiMallformedUrlException, InterruptedException {
        return getController(controllerIdByPort.get(portId)).setDimmerState(portId, value);
    }

    public BinaryPortStatus getBinSensorsState(long portId) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException {
        return getController(controllerIdByPort.get(portId)).getBinaryPortStatus(portId);
    }

    public Ds18b20TempSensorData getDs18b20SensorReading(long portId) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException {
        return getController(controllerIdByPort.get(portId)).getTemperatureSensorPortData(portId);
    }

    public Dht21TempHumiditySensorData getDht21TempHumiditySensorReading(long portId) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException {
        return getController(controllerIdByPort.get(portId)).getTemperatureHumiditySensorPortData(portId);
    }

    public Bme280TempHumidityPressureSensorData getBme280TempHumidityPressureSensorReading(long portId) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException {
        return getController(controllerIdByPort.get(portId)).getBme280TempHumidityPressureSensorPortData(portId);
    }

    public Tsl2591LuminositySensorData getTsl2591LuminositySensorReading(long portId) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException {
        return getController(controllerIdByPort.get(portId)).getTsl2591LuminositySensorPortData(portId);
    }

    public IHomeEventBus getEventBus() {
        return sysConfig.getEventBus();
    }

    public void saveModuleMode(long moduleId, ModuleOperationMode newMode) {
        moduleConfigRepository.updateModuleMode(moduleId, newMode);
    }

    @Scheduled(fixedDelay = 15000, initialDelay = 10000)
    protected void checkState() {
        for (AbstractIHomeModule module : moduleById.values()) {
            moduleCronExecutionPool.execute(module::runCronTasks);
        }
    }

    @Scheduled(fixedDelay = 60000, initialDelay = 10000)
    protected void checkLuminosity() {
        try {
            inputPowerSupplySourceCalc.dataUpdate(getTsl2591LuminositySensorReading(LUMINOSITY_SENSOR_ID));
        } catch (Exception ex) {
            log.error("Error reading luminosity data ", ex);
        }
    }

    public List<IHomeModuleSummary> getModuleList() {
        return new ArrayList<>(this.moduleById.values());
    }

    public IHomeModuleSummary updateModuleMode(long moduleId, ModuleOperationMode value) {
        AbstractIHomeModule module = Objects.requireNonNull(moduleById.get(moduleId), "Module with ID:" + moduleId + " does not exist");
        module.onUpdateMode(value);
        return module;
    }

    public IHomeModuleSummary updateModuleOutputState(long moduleId, OutputPortStatus value) throws InterruptedException, MegadApiMallformedUrlException, IOException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, URISyntaxException {
        AbstractIHomeModule module = Objects.requireNonNull(moduleById.get(moduleId), "Module with ID:" + moduleId + " does not exist");
        module.setOutputStatus(value);
        return module;
    }

    public IHomeModuleSummary getModuleSummary(long moduleId) throws MegadApiMallformedUrlException, URISyntaxException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException {
        AbstractIHomeModule module = Objects.requireNonNull(moduleById.get(moduleId), "Module with ID:" + moduleId + " does not exist");
        return module;
    }

    public ModuleState getModuleStateData(long moduleId) throws MegadApiMallformedUrlException, URISyntaxException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException {
        AbstractIHomeModule module = Objects.requireNonNull(moduleById.get(moduleId), "Module with ID:" + moduleId + " does not exist");
        return module.getSensorReadings();
    }

    public InputPowerSupplySourceCalc getInputPowerSupplySourceCalc() {
        return inputPowerSupplySourceCalc;
    }

    @Override
    public Optional<Long> getControllerIdByAddress(String address) {
        if (address == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(controllerIdByAddress.get(address.trim()));
    }

    @Override
    public boolean isControllerExists(Long controllerId) {
        if (controllerId == null) {
            return false;
        }
        return controllerById.containsKey(controllerId);
    }

    public void onControllerEvent(ControllerEventInfo eventInfo) {
        getController(eventInfo.getSourceId()).onEvent(eventInfo);
    }

}
