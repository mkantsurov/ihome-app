package technology.positivehome.ihome.domain.runtime.controller;

import technology.positivehome.ihome.domain.constant.MegadPortType;

/**
 * Created by maxim on 6/27/19.
 **/
public class ControllerPortConfigEntry {

    private long id;
    private int portAdress;
    private MegadPortType type;
    private String description;

    public ControllerPortConfigEntry() {
    }

    public ControllerPortConfigEntry(long id, String description, MegadPortType type, int portAdress) {
        this.id = id;
        this.portAdress = portAdress;
        this.type = type;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPortAdress() {
        return portAdress;
    }

    public void setPortAdress(int portAdress) {
        this.portAdress = portAdress;
    }

    public MegadPortType getType() {
        return type;
    }

    public void setType(MegadPortType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
