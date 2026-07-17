package technology.positivehome.ihome.server.persistence.repository.queryexec;

import jakarta.annotation.Nullable;
import technology.positivehome.ihome.server.model.SearchParam;
import technology.positivehome.ihome.server.persistence.model.UserEntity;

import java.util.List;

public interface UserSearchQueryExecutor {
    UserSearchQueryExecutor filter(@Nullable List<SearchParam> filters);

    long getCount();

    PagingAndExecuteSpec order();

    interface PagingAndExecuteSpec extends ExecuteSpec {
        ExecuteSpec page(@Nullable Integer page, @Nullable Integer size);
    }

    interface ExecuteSpec {
        List<UserEntity> executeQuery();
    }
}
