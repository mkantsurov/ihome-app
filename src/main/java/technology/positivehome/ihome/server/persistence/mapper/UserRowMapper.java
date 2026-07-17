package technology.positivehome.ihome.server.persistence.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import technology.positivehome.ihome.security.model.user.Role;
import technology.positivehome.ihome.server.persistence.model.UserEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserRowMapper implements RowMapper<UserEntity> {

    @Override
    public UserEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        long id = rs.getLong("id");
        String username = rs.getString("username");
        String password = rs.getString("password");

        // Roles are populated separately via UserWithRolesExtractor
        return new UserEntity(id, username, password, new ArrayList<>());
    }

    public static final class UserWithRolesExtractor implements org.springframework.jdbc.core.ResultSetExtractor<List<UserEntity>> {

        @Override
        public List<UserEntity> extractData(java.sql.ResultSet rs) throws SQLException {
            java.util.Map<Long, UserEntity> userMap = new java.util.LinkedHashMap<>();

            while (rs.next()) {
                long userId = rs.getLong("id");
                UserEntity user = userMap.get(userId);
                if (user == null) {
                    user = new UserEntity(
                            userId,
                            rs.getString("username"),
                            rs.getString("password"),
                            new ArrayList<>()
                    );
                    userMap.put(userId, user);
                }

                int roleOrdinal = rs.getInt("role");
                if (!rs.wasNull()) {
                    List<Role> roles = new ArrayList<>(user.roles());
                    roles.add(Role.values()[roleOrdinal]);
                    // Replace with new UserEntity having updated roles
                    userMap.put(userId, new UserEntity(user.id(), user.username(), user.password(), roles));
                }
            }

            return new ArrayList<>(userMap.values());
        }
    }
}
