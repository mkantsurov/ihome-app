package technology.positivehome.ihome.server.persistence.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import technology.positivehome.ihome.domain.runtime.module.ModuleGroupEntry;

import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * Created by maxim on 6/28/19.
 **/
@Component
public class ModuleGroupEntryRowMapper implements RowMapper<ModuleGroupEntry> {
    @Override
    public ModuleGroupEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
        ModuleGroupEntry res = new ModuleGroupEntry();
        res.setId(rs.getLong("id"));
        res.setName(rs.getString("name"));
        res.setPriority(rs.getInt("priority"));
        return res;
    }
}
