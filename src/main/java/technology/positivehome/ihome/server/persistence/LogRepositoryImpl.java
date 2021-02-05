package technology.positivehome.ihome.server.persistence;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import technology.positivehome.ihome.domain.runtime.event.AuditLogEntry;

/**
 * Created by maxim on 6/25/19.
 **/
@Repository
public class LogRepositoryImpl implements LogRepository {

    private static final String ADD_LOG_ENTRY =
            "INSERT INTO audit_log_entry (session_id, client_session_id, log_action, processor_type, processor_id, " +
                    "obj_type, obj_id, parent_obj_type, parent_obj_id, descr, status_code) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private final JdbcTemplate jdbcTemplate;

    public LogRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void writeStartupMessage() {
        writeLogEntry(AuditLogEntry.startupMessage());
    }

    @Override
    public void writeLogEntry(AuditLogEntry logEntry) {
        jdbcTemplate.update(ADD_LOG_ENTRY,
                logEntry.getSessionId(),
                logEntry.getClientSessionId(),
                logEntry.getAction().ordinal(),
                logEntry.getProcessorType().ordinal(),
                logEntry.getProcessorId(),
                logEntry.getObjType().ordinal(),
                logEntry.getObjId(),
                logEntry.getParentObjType().ordinal(),
                logEntry.getParentObjId(),
                logEntry.getDescr(),
                logEntry.getStatusCode());
    }


}
