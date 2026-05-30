package technology.positivehome.ihome.server.processor;

import org.springframework.stereotype.Service;
import technology.positivehome.ihome.domain.constant.ErrorMessageLogSortRule;
import technology.positivehome.ihome.domain.runtime.module.ErrorMessageLogEntryInfo;
import technology.positivehome.ihome.server.model.SearchParam;
import technology.positivehome.ihome.server.persistence.repository.ErrorMessageLogRepository;

import java.util.List;

@Service
public class ErrorMessageLogServiceImpl implements ErrorMessageLogService {
    private final ErrorMessageLogRepository errorMessageLogRepository;

    public ErrorMessageLogServiceImpl(ErrorMessageLogRepository errorMessageLogRepository) {
        this.errorMessageLogRepository = errorMessageLogRepository;
    }

    @Override
    public long countMessages(List<SearchParam> searchParams) {
        return errorMessageLogRepository.countUserAuditLogEntries(searchParams);
    }

    @Override
    public List<ErrorMessageLogEntryInfo> searchMessages(List<SearchParam> searchParams, Integer page, Integer size, List<ErrorMessageLogSortRule> sort) {
        return errorMessageLogRepository.searchErrorMessages(searchParams, page, size, sort).stream()
                .map(ErrorMessageLogMapper::from).toList();
    }
}
