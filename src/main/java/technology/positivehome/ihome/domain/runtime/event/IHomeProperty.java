package technology.positivehome.ihome.domain.runtime.event;

import technology.positivehome.ihome.domain.constant.IHomePropertyType;

/**
 * Created by maxim on 6/25/19.
 **/
public class IHomeProperty {

    private IHomePropertyType type;
    private String value;

    public IHomeProperty() {
    }

    public IHomeProperty(IHomePropertyType type, String value) {
        this.type = type;
        this.value = value;
    }

    public IHomePropertyType getType() {
        return type;
    }

    public void setType(IHomePropertyType type) {
        this.type = type;
    }


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
