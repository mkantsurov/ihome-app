package technology.positivehome.ihome.server.persistence;

import technology.positivehome.ihome.model.runtime.event.MeasurementLogEntity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by maxim on 9/1/19.
 **/
public interface MeasurementsLogRepository {
    void writeLogEntry(MeasurementLogEntity logEntry);

    List<MeasurementLogEntity> readDataForPeriod(LocalDateTime startDate, LocalDateTime endDate);
}
