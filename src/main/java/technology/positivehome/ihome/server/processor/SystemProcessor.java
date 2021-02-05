package technology.positivehome.ihome.server.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import technology.positivehome.ihome.domain.constant.ModuleOperationMode;
import technology.positivehome.ihome.domain.runtime.SystemSummaryInfo;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.domain.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.domain.runtime.module.ModuleState;
import technology.positivehome.ihome.domain.runtime.module.ModuleStateData;
import technology.positivehome.ihome.domain.runtime.module.ModuleSummary;
import technology.positivehome.ihome.domain.runtime.module.OutputPortStatus;
import technology.positivehome.ihome.server.service.core.SystemManager;
import technology.positivehome.ihome.server.service.core.module.IHomeModuleSummary;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static technology.positivehome.ihome.server.processor.ModuleMapper.from;

@Component
public class SystemProcessor {

    public static final long SFLOOR_PRESS_TEMP_SENSOR_ID = 36L;
    public static final long GFLOOR_TEMP_SENSOR_ID = 67;
    public static final long OUTDOOR_TEMP_HUMIDITY_SENSOR_ID = 11L;
    public static final long GARAGE_TEMP_HUMIDITY_SENSOR_ID = 6L;
    public static final long BOILER_TEMP_SENSOR_ID = 9L;
    public static final long LUMINOSITY_SENSOR_ID = 58L;

    private final SystemManager systemManager;

    private static final Logger log = LoggerFactory.getLogger(SystemProcessor.class);

    public SystemProcessor(SystemManager systemManager) {
        this.systemManager = systemManager;
    }

    public SystemSummaryInfo getSummaryInfo() throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException {
        return SystemSummaryInfo.builder()
                .indoorData(systemManager.getBme280TempHumidityPressureSensorReading(SFLOOR_PRESS_TEMP_SENSOR_ID),
                        systemManager.getDs18b20SensorReading(GFLOOR_TEMP_SENSOR_ID))
                .outDoorData(systemManager.getDht21TempHumiditySensorReading(OUTDOOR_TEMP_HUMIDITY_SENSOR_ID))
                .garageData(systemManager.getDht21TempHumiditySensorReading(GARAGE_TEMP_HUMIDITY_SENSOR_ID))
                .boilerData(systemManager.getDs18b20SensorReading(BOILER_TEMP_SENSOR_ID))
                .luminosityData(systemManager.getInputPowerSupplySourceCalc().getAvgValue(60000))
                .systemLoadStatsData(
                        (int) Math.round(ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage() * 100),
                        (int) (ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax() / 1048576L),
                        (int) (ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1048576L))
                .build();
    }

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

    public ModuleStateData getModuleData(long moduleId) throws MegadApiMallformedUrlException, IOException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, URISyntaxException, InterruptedException {
        IHomeModuleSummary moduleSummary = systemManager.getModuleSummary(moduleId);
        ModuleState moduleState = systemManager.getModuleStateData(moduleId);
        return from(moduleSummary, moduleState);
    }

}
