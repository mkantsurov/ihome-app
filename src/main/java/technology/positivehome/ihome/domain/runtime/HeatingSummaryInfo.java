package technology.positivehome.ihome.domain.runtime;

import technology.positivehome.ihome.domain.runtime.sensor.Bme280TempHumidityPressureSensorData;
import technology.positivehome.ihome.domain.runtime.sensor.Dht21TempHumiditySensorData;
import technology.positivehome.ihome.domain.runtime.sensor.Ds18b20TempSensorData;

import java.io.Serializable;

/**
 * Created by maxim on 3/8/21.
 **/
public class HeatingSummaryInfo implements Serializable {

    private int sfTemperature;
    private int sfHumidity;

    private int gfTemperature;

    private int outDoorTemperature;
    private int outDoorHumidity;

    private int garageTemperature;
    private int garageHumidity;

    private int boilerTemperature;

    public HeatingSummaryInfo() {
    }

    public HeatingSummaryInfo(Builder bld) {
        sfTemperature = bld.sfTemperature;
        sfHumidity = bld.sfHumidity;
        gfTemperature = bld.gfTemperature;
        outDoorTemperature = bld.outDoorTemperature;
        outDoorHumidity = bld.outDoorHumidity;
        garageTemperature = bld.garageTemperature;
        garageHumidity = bld.garageHumidity;
        boilerTemperature = bld.boilerTemperature;
    }

    public int getSfTemperature() {
        return sfTemperature;
    }

    public void setSfTemperature(int sfTemperature) {
        this.sfTemperature = sfTemperature;
    }

    public int getSfHumidity() {
        return sfHumidity;
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

    public void setBoilerTemperature(int boilerTemperature) {
        this.boilerTemperature = boilerTemperature;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private int sfTemperature;
        private int sfHumidity;
        private int gfTemperature;
        private int pressure;
        private int outDoorTemperature;
        private int outDoorHumidity;
        private int garageTemperature;
        private int garageHumidity;
        private int boilerTemperature;

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

        public HeatingSummaryInfo build() {
            return new HeatingSummaryInfo(this);
        }
    }
}
