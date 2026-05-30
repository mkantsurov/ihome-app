package technology.positivehome.ihome.server.persistence;

import technology.positivehome.ihome.domain.runtime.event.AuditLogEntry;

/**
 * Created by maxim on 6/25/19.
 **/
public interface LogRepository {
    void writeStartupMessage();

    void writeLogEntry(AuditLogEntry logEntry);
}
