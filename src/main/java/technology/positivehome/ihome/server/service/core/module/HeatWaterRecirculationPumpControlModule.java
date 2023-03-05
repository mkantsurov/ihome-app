package technology.positivehome.ihome.server.service.core.module;

import org.apache.commons.lang3.StringUtils;
import technology.positivehome.ihome.domain.constant.UiControlType;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.domain.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.domain.runtime.module.ModuleConfigElementEntry;
import technology.positivehome.ihome.domain.runtime.module.ModuleConfigEntry;
import technology.positivehome.ihome.domain.runtime.module.OutputPortStatus;
import technology.positivehome.ihome.server.model.command.IHomeCommandFactory;
import technology.positivehome.ihome.server.service.core.SystemManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by maxim on 7/2/19.
 **/
public class HeatWaterRecirculationPumpControlModule extends AbstractRelayBasedIHomeModule implements IHomeModule {

    private static final double MINIMUM_BOILER_TEMPERATURE = 43.0;
    private static final double MAX_WATER_TEMPERATURE = 35.0;

    private static final long RECIRQULATION_PUMP_ON_TIME = TimeUnit.SECONDS.toMillis(30);
    private static final long RECIRQULATION_PUMP_OFF_TIME = TimeUnit.SECONDS.toMillis(15);
    private final CronModuleJob[] moduleJobs;
    private final long boilerTemperatureSensorPort;
    private final long waterTemperatureSensorPort;

    public HeatWaterRecirculationPumpControlModule(SystemManager mgr, ModuleConfigEntry configEntry) {
        super(mgr, configEntry);

        Long boilerTempSensorPortId = -1L;
        Long waterTempSensorPortId = -1L;

        for (ModuleConfigElementEntry entry : getInputPorts()) {
            if (UiControlType.DS18B20_TEMP_SENSOR.equals(entry.getType())) {
                if (StringUtils.containsIgnoreCase(entry.getName(), "boiler")) {
                    boilerTempSensorPortId = entry.getId();
                } else {
                    waterTempSensorPortId = entry.getId();
                }
            }
        }
        boilerTemperatureSensorPort = boilerTempSensorPortId;
        waterTemperatureSensorPort = waterTempSensorPortId;

        moduleJobs = new CronModuleJob[]{
                new CronModuleJob(TimeUnit.MINUTES.toMillis(1)) {
                    @Override
                    protected void execute() throws Exception {
                        switch (getMode()) {
                            case AUTO: {

                                double boilerTemperature = getBoilerTemperature();
                                double waterTemperature = getWaterTemperature();

                                OutputPortStatus status = getOutputPortStatus();

                                if (status.isEnabled()) {
                                    if (boilerTemperature < MINIMUM_BOILER_TEMPERATURE || System.currentTimeMillis() - getLastEnableEventTs() > RECIRQULATION_PUMP_ON_TIME) {
                                        setOutputStatus(OutputPortStatus.disabled());
                                    }
                                } else if (!status.isEnabled() && !status.isUndefined()) {
                                    if (boilerTemperature > MINIMUM_BOILER_TEMPERATURE) {

                                        double diff = MAX_WATER_TEMPERATURE - waterTemperature;
                                        long boilerPumpOffInterval;
                                        if (diff < 0) {
                                            boilerPumpOffInterval = TimeUnit.HOURS.toMillis(5);
                                        } else if (diff >= 0 && diff < 0.5) {
                                            boilerPumpOffInterval = RECIRQULATION_PUMP_OFF_TIME * 10;
                                        } else if (diff >= 0.5 && diff < 1.5) {
                                            boilerPumpOffInterval = RECIRQULATION_PUMP_OFF_TIME * 5;
                                        } else if (diff >= 1.5 && diff < 2) {
                                            boilerPumpOffInterval = RECIRQULATION_PUMP_OFF_TIME * 2;
                                        } else {
                                            boilerPumpOffInterval = RECIRQULATION_PUMP_OFF_TIME;
                                        }

                                        if (System.currentTimeMillis() - getLastDisableEventTs() > boilerPumpOffInterval) {
                                            setOutputStatus(OutputPortStatus.enabled());
                                        }

                                    }
                                }
                            }
                            break;
                        }
                    }
                }
        };
    }

    private double getBoilerTemperature() throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException {
        return getMgr().runCommand(IHomeCommandFactory.cmdGetDs1820TemperatureSensorReading(boilerTemperatureSensorPort)).getData();
    }

    private double getWaterTemperature() throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException {
        return getMgr().runCommand(IHomeCommandFactory.cmdGetDs1820TemperatureSensorReading(waterTemperatureSensorPort)).getData();
    }

    @Override
    protected CronModuleJob[] getJobList() {
        return moduleJobs;
    }
}
