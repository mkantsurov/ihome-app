package technology.positivehome.ihome.server.persistence;

import technology.positivehome.ihome.domain.runtime.event.MeasurementLogEntry;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by maxim on 9/1/19.
 **/
public interface MeasurementsLogRepository {
    void writeLogEntry(MeasurementLogEntry logEntry);

    List<MeasurementLogEntry> readDataForPeriod(LocalDateTime startDate, LocalDateTime endDate);
}
