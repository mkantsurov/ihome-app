package technology.positivehome.ihome.domain.runtime.module;

/**
 * Created by maxim on 6/27/19.
 **/
public class ModuleGroupEntry {

    private long id = 0;
    private String name = "Undefined";
    private int priority;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ModuleGroupEntry)) return false;

        ModuleGroupEntry that = (ModuleGroupEntry) o;

        return getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return (int) (getId() ^ (getId() >>> 32));
    }
}
