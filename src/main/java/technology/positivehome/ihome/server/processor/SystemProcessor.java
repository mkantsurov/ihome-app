package technology.positivehome.ihome.server.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import technology.positivehome.ihome.domain.constant.BinaryPortStatus;
import technology.positivehome.ihome.domain.constant.ModuleOperationMode;
import technology.positivehome.ihome.domain.runtime.HeatingSummaryInfo;
import technology.positivehome.ihome.domain.runtime.PowerSummaryInfo;
import technology.positivehome.ihome.domain.runtime.SystemSummaryInfo;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.domain.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.domain.runtime.module.*;
import technology.positivehome.ihome.server.service.core.SystemManager;
import technology.positivehome.ihome.server.service.core.module.IHomeModuleSummary;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static technology.positivehome.ihome.server.processor.ModuleMapper.from;
import static technology.positivehome.ihome.server.service.core.module.GenericInputPowerDependentRelayPowerControlModule.POWER_SENSOR_PORT_ID;

@Component
public class SystemProcessor {

    public static final long SFLOOR_PRESS_TEMP_SENSOR_ID = 36L;
    public static final long GFLOOR_TEMP_SENSOR_ID = 67;
    public static final long OUTDOOR_TEMP_HUMIDITY_SENSOR_ID = 11L;
    public static final long GARAGE_TEMP_HUMIDITY_SENSOR_ID = 6L;
    public static final long BOILER_TEMP_SENSOR_ID = 9L;
    public static final long LUMINOSITY_SENSOR_ID = 58L;
    public static final long SECURITY_MODE_SENSOR_PORT_ID = 73L;
    public static final long DIRECT_POWER_SUPPLY_PORT= 56L;
    public static final long CONVERTER_POWER_SUPPLY_PORT= 57L;

    private final SystemManager systemManager;
    private final AtomicLong startTime = new AtomicLong(System.currentTimeMillis());

    private static final Logger log = LoggerFactory.getLogger(SystemProcessor.class);

    public SystemProcessor(SystemManager systemManager) {
        this.systemManager = systemManager;
    }

    public PowerSummaryInfo getPowerSummaryInfo() throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException {
        return new PowerSummaryInfo(
                systemManager.getInputPowerSupplySourceCalc().getAvgValue(60000),
                BinaryPortStatus.ENABLED.equals(systemManager.getBinSensorsState(POWER_SENSOR_PORT_ID)) ? 1 : 0,
                BinaryPortStatus.ENABLED.equals(systemManager.getBinSensorsState(SECURITY_MODE_SENSOR_PORT_ID)) ? 1 : 0,
                BinaryPortStatus.ENABLED.equals(systemManager.getBinOutputStatus(DIRECT_POWER_SUPPLY_PORT)) ? 1 : 0,
                BinaryPortStatus.ENABLED.equals(systemManager.getBinOutputStatus(CONVERTER_POWER_SUPPLY_PORT)) ? 1 : 0
        );
    }

    public HeatingSummaryInfo getHeatingSummaryInfo() throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, InterruptedException, IOException {
        return HeatingSummaryInfo.builder().indoorData(systemManager.getBme280TempHumidityPressureSensorReading(SFLOOR_PRESS_TEMP_SENSOR_ID),
                systemManager.getDs18b20SensorReading(GFLOOR_TEMP_SENSOR_ID))
                .outDoorData(systemManager.getDht21TempHumiditySensorReading(OUTDOOR_TEMP_HUMIDITY_SENSOR_ID))
                .garageData(systemManager.getDht21TempHumiditySensorReading(GARAGE_TEMP_HUMIDITY_SENSOR_ID))
                .boilerData(systemManager.getDs18b20SensorReading(BOILER_TEMP_SENSOR_ID)).build();
    }

    public SystemSummaryInfo getSummaryInfo() throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException {
        return SystemSummaryInfo.builder(startTime.get())
                .indoorData(systemManager.getBme280TempHumidityPressureSensorReading(SFLOOR_PRESS_TEMP_SENSOR_ID),
                        systemManager.getDs18b20SensorReading(GFLOOR_TEMP_SENSOR_ID))
                .outDoorData(systemManager.getDht21TempHumiditySensorReading(OUTDOOR_TEMP_HUMIDITY_SENSOR_ID))
                .garageData(systemManager.getDht21TempHumiditySensorReading(GARAGE_TEMP_HUMIDITY_SENSOR_ID))
                .boilerData(systemManager.getDs18b20SensorReading(BOILER_TEMP_SENSOR_ID))
                .luminosityData(systemManager.getInputPowerSupplySourceCalc().getAvgValue(60000))
                .powerData(BinaryPortStatus.ENABLED.equals(systemManager.getBinSensorsState(POWER_SENSOR_PORT_ID)) ? 1 : 0)
                .securityMode(BinaryPortStatus.ENABLED.equals(systemManager.getBinSensorsState(SECURITY_MODE_SENSOR_PORT_ID)) ? 1 : 0)
                .pwSrcDirectModeMode(systemManager.getBinOutputStatus(DIRECT_POWER_SUPPLY_PORT) ? 1 : 0)
                .pwSrcConverterMode(systemManager.getBinOutputStatus(CONVERTER_POWER_SUPPLY_PORT) ? 1: 0)
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
        }).sorted(Comparator.comparingInt((ModuleSummary ms) -> ms.getAssignment().ordinal()).thenComparingLong(ModuleSummary::getGroup))
                .toArray(ModuleSummary[]::new);
    }

    public ModuleEntry getModuleData(long moduleId) throws MegadApiMallformedUrlException, IOException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, URISyntaxException, InterruptedException {
        IHomeModuleSummary moduleSummary = systemManager.getModuleSummary(moduleId);
        ModuleState moduleState = systemManager.getModuleStateData(moduleId);
        return from(moduleSummary, moduleState);
    }

    public void updateModuleProps(long moduleId, ModuleUpdateRequest moduleUpdateRequest) throws InterruptedException, MegadApiMallformedUrlException, IOException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, URISyntaxException {
        systemManager.updateModuleMode(moduleId, moduleUpdateRequest.isModuleActive() ? ModuleOperationMode.AUTO : ModuleOperationMode.MANUAL);
        systemManager.updateModuleOutputState(moduleId, moduleUpdateRequest.isOutputPortEnabled() ? OutputPortStatus.enabled() : OutputPortStatus.disabled());
    }
}
