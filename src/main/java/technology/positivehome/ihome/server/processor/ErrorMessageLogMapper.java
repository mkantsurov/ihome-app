package technology.positivehome.ihome.server.processor;


import technology.positivehome.ihome.domain.runtime.module.ErrorMessageLogEntryInfo;
import technology.positivehome.ihome.server.persistence.model.ErrorMessageLogEntity;

import java.time.ZoneId;

public class ErrorMessageLogMapper {
    public static ErrorMessageLogEntryInfo from(ErrorMessageLogEntity ent) {
        return new ErrorMessageLogEntryInfo(
                ent.id().toString(),
                ent.created().atZone(ZoneId.systemDefault()),
                ent.type(),
                ent.message());
    }
}
