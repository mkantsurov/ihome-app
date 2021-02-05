package technology.positivehome.ihome.domain.runtime.module;

/**
 * Created by maxim on 7/14/19.
 **/
public class SensorReadingData {

    private long id;
    private int type;
    private String name;
    private int displayMode;

    public SensorReadingData() {
    }

    public SensorReadingData(long id, int type, String name, int displayMode) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.displayMode = displayMode;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDisplayMode() {
        return displayMode;
    }

    public void setDisplayMode(int displayMode) {
        this.displayMode = displayMode;
    }
}
