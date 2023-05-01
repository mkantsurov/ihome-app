package technology.positivehome.ihome.server.service.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import technology.positivehome.ihome.domain.constant.ControllerMode;
import technology.positivehome.ihome.domain.constant.ModuleOperationMode;
import technology.positivehome.ihome.domain.constant.ModuleStartupMode;
import technology.positivehome.ihome.domain.runtime.controller.ControllerConfigEntry;
import technology.positivehome.ihome.domain.runtime.controller.ControllerPortConfigEntry;
import technology.positivehome.ihome.domain.runtime.event.BinaryInputInitiatedHwEvent;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.domain.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.domain.runtime.module.ModuleConfigEntry;
import technology.positivehome.ihome.domain.runtime.module.ModuleState;
import technology.positivehome.ihome.domain.runtime.module.OutputPortStatus;
import technology.positivehome.ihome.server.model.command.IHomeCommand;
import technology.positivehome.ihome.server.model.command.IHomeCommandFactory;
import technology.positivehome.ihome.server.persistence.ModuleConfigRepository;
import technology.positivehome.ihome.server.persistence.model.ControllerConfigEntity;
import technology.positivehome.ihome.server.persistence.repository.ControllerConfigRepository;
import technology.positivehome.ihome.server.persistence.repository.ControllerPortConfigRepository;
import technology.positivehome.ihome.server.service.core.controller.*;
import technology.positivehome.ihome.server.service.core.module.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import static technology.positivehome.ihome.server.processor.SystemProcessor.LUMINOSITY_SENSOR_ID;

/**
 * Created by maxim on 6/24/19.
 **/
@Service
@EnableScheduling
public class SystemManager implements ControllerEventListener, InitializingBean {

    private static final Log log = LogFactory.getLog(SysConfigImpl.class);
    private final ApplicationEventPublisher eventPublisher;
    private final SysConfig sysConfig;
    private final ControllerConfigRepository controllerConfigRepository;
    private final ControllerPortConfigRepository controllerPortConfigRepository;
    private final ModuleConfigRepository moduleConfigRepository;

    private final Map<String, Long> controllerIdByAddress = new ConcurrentHashMap<>();
    private final Map<Long, Long> controllerIdByPort = new ConcurrentHashMap<>();
    private final Map<Long, IHomeController> controllerById = new ConcurrentHashMap<>();
    private final Map<Long, AbstractIHomeModule> moduleById = new ConcurrentHashMap<>();
    private final InputPowerSupplySourceCalc inputPowerSupplySourceCalc;
    @Qualifier("moduleJobTaskExecutor")
    private final Executor moduleJobTaskExecutor;

    @Autowired
    public SystemManager(ApplicationEventPublisher eventPublisher, SysConfig sysConfig,
                         ControllerConfigRepository controllerConfigRepository,
                         ControllerPortConfigRepository controllerPortConfigRepository,
                         ModuleConfigRepository moduleConfigRepository,
                         InputPowerSupplySourceCalc inputPowerSupplySourceCalc,
                         Executor moduleJobTaskExecutor) {
        this.eventPublisher = eventPublisher;
        this.sysConfig = sysConfig;
        this.controllerConfigRepository = controllerConfigRepository;
        this.controllerPortConfigRepository = controllerPortConfigRepository;
        this.moduleConfigRepository = moduleConfigRepository;
        this.inputPowerSupplySourceCalc = inputPowerSupplySourceCalc;
        this.moduleJobTaskExecutor = moduleJobTaskExecutor;
    }

    @Override
    public void afterPropertiesSet() {
        for (ControllerConfigEntity configEntity : controllerConfigRepository.findAll()) {
            ControllerConfigEntry entry = ControllerConfigMapper.from(
                    configEntity, controllerPortConfigRepository.findByControllerId(configEntity.id()));
            IHomeController cnt;
            if (sysConfig.getControllerMode() == ControllerMode.LIVE) {
                switch (configEntity.type()) {
                    case MEGAD -> cnt = new MegadControllerImpl(eventPublisher, entry);
                    case USR404 -> cnt = new DR404ControllerImpl(eventPublisher, entry);
                    default -> throw new IllegalStateException("Invalid controller type: " + configEntity.type());
                }
            } else {
                switch (configEntity.type()) {
                    case MEGAD -> cnt = new EmulatedMegadControllerImpl(eventPublisher, entry);
                    case USR404 -> cnt = new DR404EmulatedControllerImpl(eventPublisher, entry);
                    default -> throw new IllegalStateException("Invalid controller type: " + configEntity.type());
                }

            }

            controllerById.put(entry.id(), cnt);
            controllerIdByAddress.put(entry.ipAddr(), entry.id());

            for (ControllerPortConfigEntry portConfig : entry.portConfig()) {
                controllerIdByPort.put(portConfig.id(), entry.id());
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
                case HOME_LIGHT_RELAY_LIGHT_DEPENDENT_POWER_CONTROL_MODULE:
                    moduleToInit = new HomeLightRelayLightDependentBasedPowerControlModule(this, configEntry);
                    break;
                case GARAGE_INVERTER_COOLING_CONTROL_MODULE:
                    moduleToInit = new GarageInverterCoolingControlModule(this, configEntry);
                    break;
                case RECUPERATOR_POWER_SUPPLY_CONTROL_MODULE:
                    moduleToInit = new RecuperationPowerControlModule(this, configEntry);
                    break;
                case SOLAR_SYSTEM_PUMP_POWER_CONTROL_MODULE:
                    moduleToInit = new SolarSystemPumpPowerControlModule(this, configEntry);
                    break;
                default:
                    throw new IllegalStateException("Module with #" + configEntry.getId() + " can't be initialized. (no config available)");
            }
            moduleById.put(moduleToInit.getModuleId(), moduleToInit);
        }

        //initialize default state
        for (AbstractIHomeModule module : moduleById.values()) {
            module.initDefaultState();
        }
    }

    public IHomeController getController(Long controllerId) {
        IHomeController result;
        if ((result = controllerById.get(controllerId)) != null) {
            return result;
        }
        throw new IllegalArgumentException("Controller #" + controllerId + " is not defined");
    }

    public <R> R runCommand(IHomeCommand<R> iHomeCommand) throws MegadApiMallformedResponseException, PortNotSupporttedFunctionException, IOException, MegadApiMallformedUrlException, InterruptedException {
        return getController(controllerIdByPort.get(iHomeCommand.getPortAddress())).runCommand(iHomeCommand);
    }

    public ApplicationEventPublisher getEventPublisher() {
        return eventPublisher;
    }

    public void saveModuleMode(long moduleId, ModuleOperationMode newMode) {
        moduleConfigRepository.updateModuleMode(moduleId, newMode);
    }

    public void updateModuleStartupMode(long moduleId, ModuleStartupMode moduleStartupMode) {
        moduleConfigRepository.updateModuleStartupMode(moduleId, moduleStartupMode);
        moduleById.get(moduleId).updateStartupMode(moduleStartupMode);
    }

    @Scheduled(fixedDelay = 15000, initialDelay = 10000)
    protected void checkState() {
        for (AbstractIHomeModule module : moduleById.values()) {
            moduleJobTaskExecutor.execute(module::runCronTasks);
        }
    }

    @Scheduled(fixedDelay = 60000, initialDelay = 10000)
    protected void checkLuminosity() {
        try {
            inputPowerSupplySourceCalc.dataUpdate(
                    getController(controllerIdByPort.get(LUMINOSITY_SENSOR_ID))
                            .runCommand(IHomeCommandFactory.cmdGetADCSensorReading(LUMINOSITY_SENSOR_ID)));
//            OutputPortStatus status = getOutputPortStatus();
//            double voltage = getMgr().runCommand(IHomeCommandFactory.cmdGetDds238Reading(POWER_METER_PORT_ID)).voltage();
//            boolean powerSupplyOk = voltage > 170 && voltage < 245;
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


    @EventListener
    public void onBinarySensorEvent(BinaryInputInitiatedHwEvent event) {
        moduleById.values().forEach(module -> {
            if (module.hasInputPort(event.getPortId())) {
                module.handleEvent(event);
            }
        });
    }


}
