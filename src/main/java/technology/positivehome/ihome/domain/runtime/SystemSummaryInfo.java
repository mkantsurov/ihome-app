package technology.positivehome.ihome.domain.runtime;

import technology.positivehome.ihome.domain.runtime.event.MeasurementLogEntry;
import technology.positivehome.ihome.domain.runtime.sensor.Bme280TempHumidityPressureSensorData;
import technology.positivehome.ihome.domain.runtime.sensor.Dht21TempHumiditySensorData;
import technology.positivehome.ihome.domain.runtime.sensor.Ds18b20TempSensorData;

/**
 * Created by maxim on 6/9/19.
 **/
public class SystemSummaryInfo {

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
    private int powerStatus;
    private int securityMode;
    private int pwSrcConverterMode;
    private int pwSrcDirectMode;
    private int heatingPumpFFMode;
    private int heatingPumpSFMode;

    public SystemSummaryInfo() {
    }

    public SystemSummaryInfo(Builder bld) {
        upTime = bld.upTime;
        loadAvg = bld.loadAvg;
        heapMax = bld.heapMax;
        heapUsage = bld.heapUsage;
        sfTemperature = bld.sfTemperature;
        sfHumidity = bld.sfHumidity;
        pressure = bld.pressure;
        gfTemperature = bld.gfTemperature;
        outDoorTemperature = bld.outDoorTemperature;
        outDoorHumidity = bld.outDoorHumidity;
        garageTemperature = bld.garageTemperature;
        garageHumidity = bld.garageHumidity;
        boilerTemperature = bld.boilerTemperature;
        luminosity = bld.luminosity;
        powerStatus = bld.powerStatus;
        securityMode = bld.securityMode;
        pwSrcConverterMode = bld.pwSrcConverterMode;
        pwSrcDirectMode = bld.pwSrcDirectMode;
        heatingPumpFFMode = bld.heatingPumpFFMode;
        heatingPumpSFMode = bld.heatingPumpSFMode;
    }

    public SystemSummaryInfo(MeasurementLogEntry entry) {
        loadAvg = entry.getLoadAvg();
        heapMax = entry.getHeapMax();
        heapUsage = entry.getHeapUsage();
        sfTemperature = entry.getIndoorSfTemp();
        sfHumidity = entry.getIndoorSfHumidity();
        pressure = entry.getPressure();
        gfTemperature = entry.getIndoorGfTemp();
        outDoorTemperature = entry.getOutdoorTemp();
        outDoorHumidity = entry.getOutdoorHumidity();
        garageTemperature = entry.getGarageTemp();
        garageHumidity = entry.getGarageHumidity();
        boilerTemperature = entry.getBoilerTemperature();
        luminosity = entry.getLuminosity();
        powerStatus = entry.getPowerStatus();
        securityMode = entry.getSecurityMode();
    }

    public long getUpTime() {
        return upTime;
    }

    public int getLoadAvg() {
        return loadAvg;
    }

    public void setLoadAvg(int loadAvg) {
        this.loadAvg = loadAvg;
    }

    public int getHeapMax() {
        return heapMax;
    }

    public void setHeapMax(int heapMax) {
        this.heapMax = heapMax;
    }

    public int getHeapUsage() {
        return heapUsage;
    }

    public void setHeapUsage(int heapUsage) {
        this.heapUsage = heapUsage;
    }

    public int getSfTemperature() {
        return sfTemperature;
    }

    public void setSfTemperature(int sfTemperature) {
        this.sfTemperature = sfTemperature;
    }

    public void setSfHumidity(int sfHumidity) {
        this.sfHumidity = sfHumidity;
    }

    public int getGfTemperature() {
        return gfTemperature;
    }

    public void setGfTemperature(int gfTemperature) {
        this.gfTemperature = gfTemperature;
    }

    public int getSfHumidity() {
        return sfHumidity;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public int getPressure() {
        return pressure;
    }

    public int getOutDoorTemperature() {
        return outDoorTemperature;
    }

    public void setOutDoorTemperature(int outDoorTemperature) {
        this.outDoorTemperature = outDoorTemperature;
    }

    public int getOutDoorHumidity() {
        return outDoorHumidity;
    }

    public void setOutDoorHumidity(int outDoorHumidity) {
        this.outDoorHumidity = outDoorHumidity;
    }

    public int getGarageTemperature() {
        return garageTemperature;
    }

    public void setGarageTemperature(int garageTemperature) {
        this.garageTemperature = garageTemperature;
    }

    public int getGarageHumidity() {
        return garageHumidity;
    }

    public void setGarageHumidity(int garageHumidity) {
        this.garageHumidity = garageHumidity;
    }

    public int getBoilerTemperature() {
        return boilerTemperature;
    }

    public int getLuminosity() {
        return luminosity;
    }

    public void setLuminosity(int luminosity) {
        this.luminosity = luminosity;
    }

    public int getPowerStatus() {
        return this.powerStatus;
    }

    public int getSecurityMode() {
        return securityMode;
    }

    public int getPwSrcConverterMode() {
        return pwSrcConverterMode;
    }

    public int getPwSrcDirectMode() {
        return pwSrcDirectMode;
    }

    public int getHeatingPumpFFMode() {
        return heatingPumpFFMode;
    }

    public int getHeatingPumpSFMode() {
        return heatingPumpSFMode;
    }

    public static Builder builder(long startTime) {
        return new Builder(startTime);
    }

    public static class Builder {

        public long upTime;
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
        private int powerStatus;
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

        public Builder powerData(int powerSensorReading) {
            powerStatus = powerSensorReading;
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
            return new SystemSummaryInfo(this);
        }


    }
}
