package technology.positivehome.ihome.domain.runtime.module;

/**
 * Created by maxim on 7/14/19.
 **/
public class BinarySensorData extends SensorReadingData {

    boolean enabled;

    public BinarySensorData() {
    }

    public BinarySensorData(long id, int type, String name, int displayMode, boolean enabled) {
        super(id, type, name, displayMode);
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
