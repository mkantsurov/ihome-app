package technology.positivehome.ihome.domain.runtime.module;

import technology.positivehome.ihome.domain.constant.ModuleDisplayMode;
import technology.positivehome.ihome.domain.constant.UiControlType;

/**
 * Created by maxim on 6/27/19.
 **/
public class ModuleConfigElementEntry {

    private long id;
    private String name;
    private UiControlType type;
    private ModuleDisplayMode displayMode;
    private long port;

    public ModuleConfigElementEntry() {
    }

    public ModuleConfigElementEntry(long id, String name, UiControlType type, ModuleDisplayMode displayMode, long port) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.displayMode = displayMode;
        this.port = port;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UiControlType getType() {
        return type;
    }

    public void setType(UiControlType type) {
        this.type = type;
    }

    public ModuleDisplayMode getDisplayMode() {
        return displayMode;
    }

    public void setDisplayMode(ModuleDisplayMode displayMode) {
        this.displayMode = displayMode;
    }

    public long getPort() {
        return port;
    }

    public void setPort(long port) {
        this.port = port;
    }
}
