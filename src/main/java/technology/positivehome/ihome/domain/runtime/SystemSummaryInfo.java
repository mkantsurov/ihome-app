package technology.positivehome.ihome.domain.runtime;

import technology.positivehome.ihome.domain.runtime.event.MeasurementLogEntity;
import technology.positivehome.ihome.domain.runtime.sensor.Bme280TempHumidityPressureSensorData;
import technology.positivehome.ihome.domain.runtime.sensor.Dds238PowerMeterData;
import technology.positivehome.ihome.domain.runtime.sensor.Dht21TempHumiditySensorData;
import technology.positivehome.ihome.domain.runtime.sensor.Ds18b20TempSensorData;

/**
 * Created by maxim on 6/9/19.
 **/
public record SystemSummaryInfo(long upTime, int loadAvg, int heapMax, int heapUsage, int sfTemperature, int sfHumidity,
                                int pressure, int gfTemperature, int outDoorTemperature, int outDoorHumidity,
                                int garageTemperature,
                                int garageHumidity, int boilerTemperature, int luminosity,
                                int extPwrVoltage, int extPwrCurrent, int extPwrFrequency, int extPwrConsumption,
                                int intPwrVoltage, int intPwrCurrent, int intPwrFrequency, int intPwrConsumption,
                                int intBckPwrVoltage, int intBckPwrCurrent, int intBckPwrFrequency, int intBckPwrConsumption,
                                int securityMode, int pwSrcConverterMode, int pwSrcDirectMode, int heatingPumpFFMode,
                                int heatingPumpSFMode) {

    public static Builder builder(long startTime) {
        return new Builder(startTime);
    }
    public static SystemSummaryInfo of(MeasurementLogEntity entry) {
        return new SystemSummaryInfo(0, entry.loadAvg(), entry.heapMax(), entry.heapUsage(),
                entry.indoorSfTemp(), entry.indoorSfHumidity(), entry.pressure(), entry.indoorGfTemp(),
                entry.outdoorTemp(), entry.outdoorHumidity(), entry.garageTemp(), entry.garageHumidity(),
                entry.boilerTemperature(), entry.luminosity(),
                entry.extPwrVoltage(), entry.extPwrCurrent(), entry.extPwrFrequency(), entry.extPwrConsumption(),
                entry.intPwrVoltage(), entry.intPwrCurrent(), entry.intPwrFrequency(), entry.intPwrConsumption(),
                entry.intBckPwrVoltage(), entry.intBckPwrCurrent(), entry.intBckPwrFrequency(), entry.intBckPwrConsumption(),
                entry.securityMode(), entry.pwSrcConverterMode(), entry.pwSrcDirectMode(),
                entry.heatingPumpFFMode(), entry.heatingPumpSFMode());
    }

    public static class Builder {
        private long upTime;
        private int loadAvg;
        private int heapMax;
        private int heapUsage;
        private int sfTemperature;
        private int sfHumidity;
        private int pressure;
        private int gfTemperature;
        private int outDoorTemperature;
        private int outDoorHumidity;
        private int garageTemperature;
        private int garageHumidity;
        private int boilerTemperature;
        private int luminosity;
        private int extPwrVoltage;
        private int extPwrCurrent;
        private int extPwrFrequency;
        private int extPwrConsumption;
        private int intPwrVoltage;
        private int intPwrCurrent;
        private int intPwrFrequency;
        private int intPwrConsumption;
        private int intBckPwrVoltage;
        private int intBckPwrCurrent;
        private int intBckPwrFrequency;
        private int intBckPwrConsumption;
        private int securityMode;
        private int pwSrcConverterMode;
        private int pwSrcDirectMode;
        private int heatingPumpFFMode;
        private int heatingPumpSFMode;

        public Builder(long startTime) {
            this.upTime = System.currentTimeMillis() - startTime;
        }

        public Builder indoorData(Bme280TempHumidityPressureSensorData data, Ds18b20TempSensorData sfTempSensData, Ds18b20TempSensorData dht21TempHumiditySensorReading) {
            sfTemperature = (int) Math.round(sfTempSensData.getData() * 100);
            sfHumidity = (int) Math.round(data.getHumidity() * 100);
            pressure = (int) Math.round(data.getPressure() * 100);
            gfTemperature = (int) Math.round(dht21TempHumiditySensorReading.getData() * 100);
            return this;
        }

        public Builder outDoorData(Dht21TempHumiditySensorData data) {
            outDoorTemperature = (int) Math.round(data.getTemperature() * 100);
            outDoorHumidity = (int) Math.round(data.getHumidity() * 100);
            return this;
        }

        public Builder garageData(Dht21TempHumiditySensorData data) {
            garageTemperature = (int) Math.round(data.getTemperature() * 100);
            garageHumidity = (int) Math.round(data.getHumidity() * 100);
            return this;
        }

        public Builder boilerData(Ds18b20TempSensorData data) {
            boilerTemperature = (int) Math.round(data.getData() * 100);
            return this;
        }

        public Builder luminosityData(Double adcSensorReading) {
            luminosity = (int) Math.round(adcSensorReading * 100);
            return this;
        }

        public Builder extPowerData(Dds238PowerMeterData powerSensorReading) {
            extPwrVoltage = (int) Math.round(powerSensorReading.voltage() * 10);
            extPwrCurrent = (int) Math.round(powerSensorReading.current() * 10);
            extPwrFrequency = (int) Math.round(powerSensorReading.freq() * 10);
            extPwrConsumption = (int) Math.round(powerSensorReading.total() * 10);
            return this;
        }

        public Builder intPowerData(Dds238PowerMeterData powerSensorReading) {
            intPwrVoltage = (int) Math.round(powerSensorReading.voltage() * 10);
            intPwrCurrent = (int) Math.round(powerSensorReading.current() * 10);
            intPwrFrequency = (int) Math.round(powerSensorReading.freq() * 10);
            intPwrConsumption = (int) Math.round(powerSensorReading.total() * 10);
            return this;
        }
        public Builder intBckPowerData(Dds238PowerMeterData powerSensorReading) {
            intBckPwrVoltage = (int) Math.round(powerSensorReading.voltage() * 10);
            intBckPwrCurrent = (int) Math.round(powerSensorReading.current() * 10);
            intBckPwrFrequency = (int) Math.round(powerSensorReading.freq() * 10);
            intBckPwrConsumption = (int) Math.round(powerSensorReading.total() * 10);
            return this;
        }

        public Builder securityMode(int securityModeSensorReading) {
            securityMode = securityModeSensorReading;
            return this;
        }

        public Builder pwSrcConverterMode(int pwSrcConverterModeSensorReading) {
            pwSrcConverterMode = pwSrcConverterModeSensorReading;
            return this;
        }

        public Builder pwSrcDirectModeMode(int pwSrcDirectModeSensorReading) {
            pwSrcDirectMode = pwSrcDirectModeSensorReading;
            return this;
        }

        public Builder heatingPumpFFMode(int heatingPumpFFModeSensorReading) {
            heatingPumpFFMode = heatingPumpFFModeSensorReading;
            return this;
        }

        public Builder heatingPumpSFMode(int heatingPumpSFModeSensorReading) {
            heatingPumpSFMode = heatingPumpSFModeSensorReading;
            return this;
        }

        public Builder systemLoadStatsData(int loadAvg, int heapMax, int heapUsage) {
            this.loadAvg = loadAvg;
            this.heapMax = heapMax;
            this.heapUsage = heapUsage;
            return this;
        }

        public SystemSummaryInfo build() {
            return new SystemSummaryInfo(upTime, loadAvg, heapMax, heapUsage, sfTemperature, sfHumidity, pressure,
                    gfTemperature, outDoorTemperature, outDoorHumidity,
                    garageTemperature, garageHumidity, boilerTemperature, luminosity,
                    extPwrVoltage, extPwrCurrent, extPwrFrequency, extPwrConsumption,
                    intPwrVoltage, intPwrCurrent, intPwrFrequency, intPwrConsumption,
                    intBckPwrVoltage, intBckPwrCurrent, intBckPwrFrequency, intBckPwrConsumption,
                    securityMode, pwSrcConverterMode, pwSrcDirectMode, heatingPumpFFMode, heatingPumpSFMode);
        }

    }
}
