package technology.positivehome.ihome.domain.runtime.event;

import java.util.Date;

/**
 * Created by maxim on 6/25/19.
 **/
public class MeasurementLogEntry {

    private long id;
    private Date created = new Date();
    private int loadAvg;
    private int heapMax;
    private int heapUsage;
    private int pressure;
    private int outdoorTemp;
    private int outdoorHumidity;
    private int indoorSfTemp;
    private int indoorSfHumidity;
    private int indoorGfTemp;
    private int garageTemp;
    private int garageHumidity;
    private int boilerTemperature;
    private int luminosity;
    private int powerStatus;

    public MeasurementLogEntry(long id, Date created, int loadAvg, int heapMax, int heapUsage, int pressure, int outdoorTemp, int outdoorHumidity,
                               int indoorSfTemp, int indoorSfHumidity, int indoorGfTemp, int garageTemp,
                               int garageHumidity, int boilerTemperature, int luminosity, int powerStatus) {
        this.id = id;
        this.created = created;
        this.loadAvg = loadAvg;
        this.heapMax = heapMax;
        this.heapUsage = heapUsage;
        this.pressure = pressure;
        this.outdoorTemp = outdoorTemp;
        this.outdoorHumidity = outdoorHumidity;
        this.indoorSfTemp = indoorSfTemp;
        this.indoorSfHumidity = indoorSfHumidity;
        this.indoorGfTemp = indoorGfTemp;
        this.garageTemp = garageTemp;
        this.garageHumidity = garageHumidity;
        this.boilerTemperature = boilerTemperature;
        this.luminosity = luminosity;
        this.powerStatus = powerStatus;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
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

    public int getPressure() {
        return pressure;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public int getOutdoorTemp() {
        return outdoorTemp;
    }

    public void setOutdoorTemp(int outdoorTemp) {
        this.outdoorTemp = outdoorTemp;
    }

    public int getOutdoorHumidity() {
        return outdoorHumidity;
    }

    public void setOutdoorHumidity(int outdoorHumidity) {
        this.outdoorHumidity = outdoorHumidity;
    }

    public int getIndoorSfTemp() {
        return indoorSfTemp;
    }

    public void setIndoorSfTemp(int indoorSfTemp) {
        this.indoorSfTemp = indoorSfTemp;
    }

    public int getIndoorSfHumidity() {
        return indoorSfHumidity;
    }

    public void setIndoorSfHumidity(int indoorSfHumidity) {
        this.indoorSfHumidity = indoorSfHumidity;
    }

    public int getIndoorGfTemp() {
        return indoorGfTemp;
    }

    public void setIndoorGfTemp(int indoorGfTemp) {
        this.indoorGfTemp = indoorGfTemp;
    }

    public int getGarageTemp() {
        return garageTemp;
    }

    public void setGarageTemp(int garageTemp) {
        this.garageTemp = garageTemp;
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
        return powerStatus;
    }

    public void setPowerStatus(int powerStatus) {
        this.powerStatus = powerStatus;
    }
}
