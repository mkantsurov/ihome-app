package technology.positivehome.ihome.server.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import technology.positivehome.ihome.domain.constant.BinaryPortStatus;
import technology.positivehome.ihome.domain.constant.ModuleOperationMode;
import technology.positivehome.ihome.domain.constant.ModuleStartupMode;
import technology.positivehome.ihome.domain.runtime.ExternalPowerSummaryInfo;
import technology.positivehome.ihome.domain.runtime.HeatingSummaryInfo;
import technology.positivehome.ihome.domain.runtime.PowerSummaryInfo;
import technology.positivehome.ihome.domain.runtime.SystemSummaryInfo;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.domain.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.domain.runtime.module.*;
import technology.positivehome.ihome.domain.runtime.sensor.Dds238PowerMeterData;
import technology.positivehome.ihome.server.model.command.IHomeCommandFactory;
import technology.positivehome.ihome.server.service.core.SystemManager;
import technology.positivehome.ihome.server.service.core.module.IHomeModuleSummary;

import jakarta.annotation.Nullable;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static technology.positivehome.ihome.server.processor.ModuleMapper.from;
import static technology.positivehome.ihome.server.service.core.module.GenericInputPowerDependentRelayPowerControlModule.*;

@Component
public class SystemProcessor {

    public static final long SFLOOR_PRESS_TEMP_SENSOR_ID = 36L;
    public static final long SFLOOR_TEMP_SENSOR_ID = 78L;
    public static final long GFLOOR_TEMP_SENSOR_ID = 67;
    public static final long OUTDOOR_TEMP_HUMIDITY_SENSOR_ID = 11L;
    public static final long GARAGE_TEMP_HUMIDITY_SENSOR_ID = 6L;
    public static final long BOILER_TEMP_SENSOR_ID = 9L;
    public static final long LUMINOSITY_SENSOR_ID = 77L;
    public static final long SECURITY_MODE_SENSOR_PORT_ID = 73L;
    public static final long DIRECT_POWER_SUPPLY_PORT = 56L;
    public static final long CONVERTER_POWER_SUPPLY_PORT = 57L;

    private final SystemManager systemManager;
    private final AtomicLong startTime = new AtomicLong(System.currentTimeMillis());

    private static final Logger log = LoggerFactory.getLogger(SystemProcessor.class);

    public SystemProcessor(SystemManager systemManager) {
        this.systemManager = systemManager;
    }

    public ExternalPowerSummaryInfo getExtPowerSummaryInfo() throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException {
        BinaryPortStatus state = systemManager.runCommand(IHomeCommandFactory.cmdGetBinarySensorReading(POWER_SENSOR_PORT_ID));
        Dds238PowerMeterData data;
        if (BinaryPortStatus.ENABLED.equals(state)) {
            data = systemManager.runCommand(IHomeCommandFactory.cmdGetDds238Reading(POWER_METER_PORT_ID));
        } else {
            data = new Dds238PowerMeterData(.0, .0, .0, .0);
        }
        return new ExternalPowerSummaryInfo(
                (int) Math.round(data.voltage() * 10),
                (int) Math.round(data.freq() * 100));
    }

    public PowerSummaryInfo getPowerSummaryInfo() throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException {
        BinaryPortStatus state = systemManager.runCommand(IHomeCommandFactory.cmdGetBinarySensorReading(POWER_SENSOR_PORT_ID));
        Dds238PowerMeterData extData;
        if (BinaryPortStatus.ENABLED.equals(state)) {
            extData = systemManager.runCommand(IHomeCommandFactory.cmdGetDds238Reading(POWER_METER_PORT_ID));
        } else {
            extData = new Dds238PowerMeterData(.0, .0, .0, .0);
        }
        Dds238PowerMeterData intData = systemManager.runCommand(IHomeCommandFactory.cmdGetDds238Reading(INT_POWER_METER_PORT_ID));

        Dds238PowerMeterData intBckData = systemManager.runCommand(IHomeCommandFactory.cmdGetDds238Reading(INT_BCK_POWER_METER_PORT_ID));
        return new PowerSummaryInfo(
                (int) Math.round(systemManager.getInputPowerSupplySourceCalc().getAvgValue(60000) * 100),
                (int) Math.round(extData.voltage() * 10),
                (int) Math.round(extData.current() * 10),
                (int) Math.round(extData.freq() * 100),
                (int) Math.round(extData.total() * 10),
                (int) Math.round(intData.voltage() * 10),
                (int) Math.round(intData.current() * 10),
                (int) Math.round(intData.freq() * 100),
                (int) Math.round(intData.total() * 10),
                (int) Math.round(intBckData.voltage() * 10),
                (int) Math.round(intBckData.current() * 10),
                (int) Math.round(intBckData.freq() * 100),
                (int) Math.round(intBckData.total() * 10),
                BinaryPortStatus.ENABLED.equals(systemManager.runCommand(IHomeCommandFactory.cmdGetBinarySensorReading(SECURITY_MODE_SENSOR_PORT_ID))) ? 1 : 0,
                BinaryPortStatus.ENABLED.equals(systemManager.runCommand(IHomeCommandFactory.cmdGetRelayStatus(DIRECT_POWER_SUPPLY_PORT))) ? 1 : 0,
                BinaryPortStatus.ENABLED.equals(systemManager.runCommand(IHomeCommandFactory.cmdGetRelayStatus(CONVERTER_POWER_SUPPLY_PORT))) ? 1 : 0
        );
    }

    public HeatingSummaryInfo getHeatingSummaryInfo() throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, InterruptedException, IOException {
        return HeatingSummaryInfo.builder()
                .indoorData(
                        systemManager.runCommand(IHomeCommandFactory.cmdGetBme280TempHumidityPressureSensorReading(SFLOOR_PRESS_TEMP_SENSOR_ID)),
                        systemManager.runCommand(IHomeCommandFactory.cmdGetDs1820TemperatureSensorReading(SFLOOR_TEMP_SENSOR_ID)),
                        systemManager.runCommand(IHomeCommandFactory.cmdGetDs1820TemperatureSensorReading(GFLOOR_TEMP_SENSOR_ID)))
                .outDoorData(systemManager.runCommand(IHomeCommandFactory.cmdGetDht21TempHumiditySensorReading(OUTDOOR_TEMP_HUMIDITY_SENSOR_ID)))
                .garageData(systemManager.runCommand(IHomeCommandFactory.cmdGetDht21TempHumiditySensorReading(GARAGE_TEMP_HUMIDITY_SENSOR_ID)))
                .boilerData(systemManager.runCommand(IHomeCommandFactory.cmdGetDs1820TemperatureSensorReading(BOILER_TEMP_SENSOR_ID))).build();
    }

    public SystemSummaryInfo getSummaryInfo() throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException {
        BinaryPortStatus state = systemManager.runCommand(IHomeCommandFactory.cmdGetBinarySensorReading(POWER_SENSOR_PORT_ID));
        Dds238PowerMeterData extPwrData;
        if (BinaryPortStatus.ENABLED.equals(state)) {
            extPwrData = systemManager.runCommand(IHomeCommandFactory.cmdGetDds238Reading(POWER_METER_PORT_ID));
        } else {
            extPwrData = new Dds238PowerMeterData(.0, .0, .0, .0);
        }
        return SystemSummaryInfo.builder(startTime.get())
                .indoorData(
                        systemManager.runCommand(IHomeCommandFactory.cmdGetBme280TempHumidityPressureSensorReading(SFLOOR_PRESS_TEMP_SENSOR_ID)),
                        systemManager.runCommand(IHomeCommandFactory.cmdGetDs1820TemperatureSensorReading(SFLOOR_TEMP_SENSOR_ID)),
                        systemManager.runCommand(IHomeCommandFactory.cmdGetDs1820TemperatureSensorReading(GFLOOR_TEMP_SENSOR_ID)))
                .outDoorData(systemManager.runCommand(IHomeCommandFactory.cmdGetDht21TempHumiditySensorReading(OUTDOOR_TEMP_HUMIDITY_SENSOR_ID)))
                .garageData(systemManager.runCommand(IHomeCommandFactory.cmdGetDht21TempHumiditySensorReading(GARAGE_TEMP_HUMIDITY_SENSOR_ID)))
                .boilerData(systemManager.runCommand(IHomeCommandFactory.cmdGetDs1820TemperatureSensorReading(BOILER_TEMP_SENSOR_ID)))
                .luminosityData(systemManager.getInputPowerSupplySourceCalc().getAvgValue(60000))
                .extPowerData(extPwrData)
                .intPowerData(systemManager.runCommand(IHomeCommandFactory.cmdGetDds238Reading(INT_POWER_METER_PORT_ID)))
                .intBckPowerData(systemManager.runCommand(IHomeCommandFactory.cmdGetDds238Reading(INT_BCK_POWER_METER_PORT_ID)))
                .securityMode(BinaryPortStatus.ENABLED.equals(systemManager.runCommand(IHomeCommandFactory.cmdGetBinarySensorReading(SECURITY_MODE_SENSOR_PORT_ID))) ? 1 : 0)
                .pwSrcDirectModeMode(BinaryPortStatus.ENABLED.equals(systemManager.runCommand(IHomeCommandFactory.cmdGetRelayStatus(DIRECT_POWER_SUPPLY_PORT))) ? 1 : 0)
                .pwSrcConverterMode(BinaryPortStatus.ENABLED.equals(systemManager.runCommand(IHomeCommandFactory.cmdGetRelayStatus(CONVERTER_POWER_SUPPLY_PORT))) ? 1 : 0)
                .systemLoadStatsData(
                        (int) Math.round(ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage() * 100),
                        (int) (ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax() / 1048576L),
                        (int) (ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1048576L))
                .build();
    }

    @Deprecated
    public List<ModuleSummary> getModuleListByGroup(long group) {
        List<IHomeModuleSummary> moduleList = systemManager.getModuleList();
        List<ModuleSummary> result = new ArrayList<>();
        moduleList.forEach(iHomeModule -> {
            if (group == iHomeModule.getGroupId()) {
                try {
                    result.add(from(iHomeModule));
                } catch (Exception ex) {
                    log.error("Unable to request module state", ex);
                }
            }
        });
        return result;
    }

    public ModuleSummary updateModuleMode(long moduleId, int moduleMode) throws MegadApiMallformedUrlException, URISyntaxException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException {
        return from(systemManager.updateModuleMode(moduleId, ModuleOperationMode.values()[moduleMode]));
    }

    public ModuleSummary updateModuleOutputState(long moduleId, int outputStatus) throws InterruptedException, MegadApiMallformedUrlException, IOException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, URISyntaxException {
        return from(systemManager.updateModuleOutputState(moduleId, new OutputPortStatus(outputStatus)));
    }

    public ModuleSummary[] getModuleList(@Nullable Integer assignment, @Nullable Long group) {
        return systemManager.getModuleList().stream().filter(iHomeModuleSummary -> {
                    boolean testRes = true;
                    if (assignment != null && !assignment.equals(iHomeModuleSummary.getAssignment().ordinal())) {
                        testRes = false;
                    }
                    if (testRes && group != null && !group.equals(iHomeModuleSummary.getGroupId())) {
                        testRes = false;
                    }
                    return testRes;
                }).map(iHomeModuleSummary -> {
                    try {
                        return from(iHomeModuleSummary);
                    } catch (Exception e) {
                        throw new IllegalStateException("Unable to load module status", e);
                    }
                }).sorted(Comparator.comparingInt((ModuleSummary ms) -> ms.getAssignment().ordinal())
                        .thenComparing(ModuleSummary::getName).thenComparingLong(ModuleSummary::getGroup))
                .toArray(ModuleSummary[]::new);
    }

    public ModuleEntry getModuleData(long moduleId) throws MegadApiMallformedUrlException, IOException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, URISyntaxException, InterruptedException {
        IHomeModuleSummary moduleSummary = systemManager.getModuleSummary(moduleId);
        ModuleState moduleState = systemManager.getModuleStateData(moduleId);
        return from(moduleSummary, moduleState);
    }

    public void updateModuleProps(long moduleId, ModuleUpdateRequest moduleUpdateRequest) throws InterruptedException, MegadApiMallformedUrlException, IOException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, URISyntaxException {
        systemManager.updateModuleStartupMode(moduleId, moduleUpdateRequest.enabledOnStartup() ? ModuleStartupMode.ENABLED : ModuleStartupMode.DISABLED);
        systemManager.updateModuleMode(moduleId, moduleUpdateRequest.moduleActive() ? ModuleOperationMode.AUTO : ModuleOperationMode.MANUAL);
        IHomeModuleSummary moduleSummary = systemManager.getModuleSummary(moduleId);
        systemManager.updateModuleOutputState(moduleId, OutputPortStatus.of(moduleSummary.dimmableOutput(), moduleUpdateRequest.outputValue()));
    }
}
