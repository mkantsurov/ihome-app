package technology.positivehome.ihome.domain.runtime;

import technology.positivehome.ihome.domain.runtime.event.MeasurementLogEntry;
import technology.positivehome.ihome.domain.runtime.sensor.Bme280TempHumidityPressureSensorData;
import technology.positivehome.ihome.domain.runtime.sensor.Dht21TempHumiditySensorData;
import technology.positivehome.ihome.domain.runtime.sensor.Ds18b20TempSensorData;

/**
 * Created by maxim on 6/9/19.
 **/
public class SystemSummaryInfo {

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

    public SystemSummaryInfo() {
    }

    public SystemSummaryInfo(Builder bld) {
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

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

        public Builder indoorData(Bme280TempHumidityPressureSensorData data, Ds18b20TempSensorData dht21TempHumiditySensorReading) {
            sfTemperature = (int) Math.round(data.getTemperature() * 100);
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

        public Builder luminosityData(Double tsl2591LuminositySensorReading) {
            luminosity = (int) Math.round(tsl2591LuminositySensorReading * 100);
            return this;
        }

        public Builder powerData(int powerSensorReading) {
            powerStatus = powerSensorReading;
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
