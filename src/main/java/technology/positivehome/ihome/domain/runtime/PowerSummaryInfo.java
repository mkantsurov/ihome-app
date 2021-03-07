package technology.positivehome.ihome.domain.runtime;

/**
 * Created by maxim on 6/9/19.
 **/
public class PowerSummaryInfo {

    private int luminosity;
    private int powerStatus;
    private int securityMode;
    private int pwSrcConverterMode;
    private int pwSrcDirectMode;

    public PowerSummaryInfo() {
    }

    public PowerSummaryInfo(double luminosity, int powerStatus, int securityMode, int pwSrcConverterMode, int pwSrcDirectMode) {
        this.luminosity = (int) Math.round(luminosity * 100);
        this.powerStatus = powerStatus;
        this.securityMode = securityMode;
        this.pwSrcConverterMode = pwSrcConverterMode;
        this.pwSrcDirectMode = pwSrcDirectMode;
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

    public int getSecurityMode() {
        return securityMode;
    }

    public void setSecurityMode(int securityMode) {
        this.securityMode = securityMode;
    }

    public int getPwSrcConverterMode() {
        return pwSrcConverterMode;
    }

    public void setPwSrcConverterMode(int pwSrcConverterMode) {
        this.pwSrcConverterMode = pwSrcConverterMode;
    }

    public int getPwSrcDirectMode() {
        return pwSrcDirectMode;
    }

    public void setPwSrcDirectMode(int pwSrcDirectMode) {
        this.pwSrcDirectMode = pwSrcDirectMode;
    }
}
