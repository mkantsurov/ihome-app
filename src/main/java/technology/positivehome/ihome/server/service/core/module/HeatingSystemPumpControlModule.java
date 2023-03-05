package technology.positivehome.ihome.server.service.core.module;

import com.google.common.util.concurrent.AtomicDouble;
import org.apache.commons.lang3.StringUtils;
import technology.positivehome.ihome.domain.constant.ModuleProperty;
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
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by maxim on 7/2/19.
 **/
public class HeatingSystemPumpControlModule extends AbstractRelayBasedIHomeModule implements IHomeModule {

    private static final double MINIMUM_BOILER_TEMPERATURE = 40.0;
    private static final double MAXIMUM_INDOOR_TEMPERATURE = 24.0;

    private static final long BOILER_PUMP_ON_TIME = TimeUnit.SECONDS.toMillis(15);
    private static final long BOILER_PUMP_OFF_TIME = TimeUnit.SECONDS.toMillis(5);
    private final CronModuleJob[] moduleJobs;
    private final long boilerTemperatureSensorPort;
    private final long indoorTemperatureSensorPort;
    private final AtomicDouble maxIndoorTemperature = new AtomicDouble(MAXIMUM_INDOOR_TEMPERATURE);

    public HeatingSystemPumpControlModule(SystemManager mgr, ModuleConfigEntry configEntry) {
        super(mgr, configEntry);

        if (getProperty(ModuleProperty.MAX_INDOOR_TEMPERATURE).isPresent()) {
            maxIndoorTemperature.set(getProperty(ModuleProperty.MAX_INDOOR_TEMPERATURE).get().getDoubleValue());
        }

        Long boilerTempSensorPortId = -1L;
        Long indoorTempSensorPortId = -1L;

        AtomicReference<UiControlType> indoorTemperatureSensorType = new AtomicReference<>(UiControlType.UNDEFINED);

        for (ModuleConfigElementEntry entry : getInputPorts()) {
            if (UiControlType.DS18B20_TEMP_SENSOR.equals(entry.getType()) && StringUtils.containsIgnoreCase(entry.getName(), "boiler")) {
                boilerTempSensorPortId = entry.getId();
//            } else if (UiControlType.BME280_TEMP_HUMIDITY_PRESS_SENSOR.equals(entry.getType())) {
//                indoorTempSensorPortId = entry.getId();
//                indoorTemperatureSensorType.set(UiControlType.BME280_TEMP_HUMIDITY_PRESS_SENSOR);
            } else if (UiControlType.DHT21_TEMP_HUMIDITY_SENSOR.equals(entry.getType())) {
                indoorTempSensorPortId = entry.getId();
                indoorTemperatureSensorType.set(UiControlType.DHT21_TEMP_HUMIDITY_SENSOR);
            } else if (UiControlType.DS18B20_TEMP_SENSOR.equals(entry.getType())) {
                indoorTempSensorPortId = entry.getId();
                indoorTemperatureSensorType.set(UiControlType.DS18B20_TEMP_SENSOR);
            }
        }
        boilerTemperatureSensorPort = boilerTempSensorPortId;
        indoorTemperatureSensorPort = indoorTempSensorPortId;

        moduleJobs = new CronModuleJob[]{
                new CronModuleJob(TimeUnit.MINUTES.toMillis(1)) {
                    @Override
                    protected void execute() throws Exception {
                        switch (getMode()) {
                            case AUTO: {

                                double boilerTemperature = getBoilerTemperature();
                                double indoorTemperature = getIndoorTemperature(indoorTemperatureSensorType.get());

                                OutputPortStatus status = getOutputPortStatus();

                                if (status.isEnabled()) {
                                    if (boilerTemperature < MINIMUM_BOILER_TEMPERATURE || System.currentTimeMillis() - getLastEnableEventTs() > BOILER_PUMP_ON_TIME) {
                                        setOutputStatus(OutputPortStatus.disabled());
                                    }
                                } else if (!status.isEnabled() && !status.isUndefined()) {
                                    if (boilerTemperature > MINIMUM_BOILER_TEMPERATURE) {

                                        double diff = maxIndoorTemperature.get() - indoorTemperature;
                                        long boilerPumpOffInterval;
                                        if (diff < 0) {
                                            boilerPumpOffInterval = TimeUnit.HOURS.toMillis(5);
                                        } else if (diff >= 0 && diff < 0.5) {
                                            boilerPumpOffInterval = BOILER_PUMP_OFF_TIME * 12;
                                        } else if (diff >= 0.5 && diff < 1.5) {
                                            boilerPumpOffInterval = BOILER_PUMP_OFF_TIME * 6;
                                        } else if (diff >= 1.5 && diff < 2) {
                                            boilerPumpOffInterval = BOILER_PUMP_OFF_TIME * 3;
                                        } else {
                                            boilerPumpOffInterval = BOILER_PUMP_OFF_TIME;
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

    private double getIndoorTemperature(UiControlType indoorTemperatureSensorType) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException {
        if (UiControlType.DS18B20_TEMP_SENSOR.equals(indoorTemperatureSensorType)) {
            return getMgr().runCommand(IHomeCommandFactory.cmdGetDs1820TemperatureSensorReading(indoorTemperatureSensorPort)).getData();
        } else if (UiControlType.DHT21_TEMP_HUMIDITY_SENSOR.equals(indoorTemperatureSensorType)) {
            return getMgr().runCommand(IHomeCommandFactory.cmdGetDht21TempHumiditySensorReading(indoorTemperatureSensorPort)).getTemperature();
        } else if (UiControlType.BME280_TEMP_HUMIDITY_PRESS_SENSOR.equals(indoorTemperatureSensorType)) {
            return getMgr().runCommand(IHomeCommandFactory.cmdGetBme280TempHumidityPressureSensorReading(indoorTemperatureSensorPort)).getTemperature();
        } else {
            throw new IllegalStateException("Unsupported sensor type");
        }

    }

    @Override
    protected CronModuleJob[] getJobList() {
        return moduleJobs;
    }
}
