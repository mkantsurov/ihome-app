package technology.positivehome.ihome.domain.shared;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;

import java.time.ZonedDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ChartPointInfo(
        @JsonSerialize(using = ZonedDateTimeSerializer.class) @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ") ZonedDateTime dt,
        int value) {

    public ChartPointInfo(@JsonProperty("dt") ZonedDateTime dt, @JsonProperty("value") int value) {
        this.dt = dt;
        this.value = value;
    }

    public static ChartPointInfo of(ZonedDateTime dt, int value) {
        return new ChartPointInfo(dt, value);
    }
}
