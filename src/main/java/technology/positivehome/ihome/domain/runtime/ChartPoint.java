package technology.positivehome.ihome.domain.runtime;

import java.time.LocalDateTime;

/**
 * Created by maxim on 8/17/19.
 **/
public class ChartPoint {

    private LocalDateTime dt;
    private int value;

    public ChartPoint() {
    }

    public ChartPoint(LocalDateTime dt, int value) {
        this.dt = dt;
        this.value = value;
    }

    public LocalDateTime getDt() {
        return dt;
    }

    public void setDt(LocalDateTime dt) {
        this.dt = dt;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
