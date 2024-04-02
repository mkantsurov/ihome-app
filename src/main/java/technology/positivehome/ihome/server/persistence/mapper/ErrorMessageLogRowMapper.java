package technology.positivehome.ihome.server.persistence.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import technology.positivehome.ihome.domain.constant.ErrorEventType;
import technology.positivehome.ihome.domain.constant.IHomePortType;
import technology.positivehome.ihome.server.persistence.model.ControllerPortConfigEntity;
import technology.positivehome.ihome.server.persistence.model.ErrorMessageLogEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class ErrorMessageLogRowMapper implements RowMapper<ErrorMessageLogEntity> {

    @Override
    public ErrorMessageLogEntity mapRow(ResultSet rs, int rowNum) throws SQLException {

        return new ErrorMessageLogEntity(
                rs.getObject("id", UUID.class),
                rs.getObject("created", LocalDateTime.class),
                ErrorEventType.values()[rs.getInt("type")],
                rs.getString("message"));
    }
}
