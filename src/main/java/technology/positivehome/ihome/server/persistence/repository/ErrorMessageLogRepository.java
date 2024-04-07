package technology.positivehome.ihome.server.persistence.repository;

import jakarta.annotation.Nullable;
import technology.positivehome.ihome.domain.constant.ErrorMessageLogSortRule;
import technology.positivehome.ihome.server.model.SearchParam;
import technology.positivehome.ihome.server.persistence.model.ErrorMessageLogEntity;

import java.util.List;
import java.util.UUID;

public interface ErrorMessageLogRepository extends GenericIHomeRepository<ErrorMessageLogEntity, UUID> {
    List<ErrorMessageLogEntity> searchErrorMessages(@Nullable List<SearchParam> filter, @Nullable Integer page, @Nullable Integer size, @Nullable List<ErrorMessageLogSortRule> sort);

    long countUserAuditLogEntries(@Nullable List<SearchParam> filter);
}
