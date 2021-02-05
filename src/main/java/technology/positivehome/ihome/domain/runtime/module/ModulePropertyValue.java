package technology.positivehome.ihome.domain.runtime.module;

import technology.positivehome.ihome.domain.constant.ModuleProperty;

/**
 * Created by maxim on 6/27/19.
 **/
public class ModulePropertyValue {

    private long id;
    private ModuleProperty key;
    private String stringValue;
    private Long longValue;

    public ModulePropertyValue() {
    }

    public ModulePropertyValue(long id, ModuleProperty key, String stringValue, Long longValue) {
        this.id = id;
        this.key = key;
        this.stringValue = stringValue;
        this.longValue = longValue;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ModuleProperty getKey() {
        return key;
    }

    public void setKey(ModuleProperty key) {
        this.key = key;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public Long getLongValue() {
        return longValue;
    }

    public void setLongValue(Long longValue) {
        this.longValue = longValue;
    }

    public double getDoubleValue() {
        return getLongValue() / 100.0;
    }
}
