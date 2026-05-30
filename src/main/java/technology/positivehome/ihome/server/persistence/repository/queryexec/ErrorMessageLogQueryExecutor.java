package technology.positivehome.ihome.server.persistence.repository.queryexec;

import jakarta.annotation.Nullable;
import technology.positivehome.ihome.domain.constant.ErrorMessageLogSortRule;
import technology.positivehome.ihome.server.model.SearchParam;
import technology.positivehome.ihome.server.persistence.model.ErrorMessageLogEntity;

import java.util.List;

public interface ErrorMessageLogQueryExecutor {
    ErrorMessageLogQueryExecutor filter(@Nullable List<SearchParam> filters);

    long getCount();

    PagingAndExecuteSpec order(@Nullable List<ErrorMessageLogSortRule> sortRules);

    interface PagingAndExecuteSpec extends ExecuteSpec {
        ExecuteSpec page(@Nullable Integer page, @Nullable Integer size);
    }

    interface ExecuteSpec {
        List<ErrorMessageLogEntity> executeQuery();
    }
}
