package technology.positivehome.ihome.domain.runtime;

import java.time.LocalDateTime;

/**
 * Created by maxim on 8/17/19.
 **/
public record ChartPoint(LocalDateTime dt, int value) {
    public static ChartPoint of(LocalDateTime dt, int value) {
        return new ChartPoint(dt, value);
    }
}
