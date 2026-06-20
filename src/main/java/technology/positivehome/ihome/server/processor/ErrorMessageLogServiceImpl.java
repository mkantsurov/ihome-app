package technology.positivehome.ihome.server.processor;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import technology.positivehome.ihome.model.constant.ErrorMessageLogSortRule;
import technology.positivehome.ihome.model.runtime.event.IHomeErrorEvent;
import technology.positivehome.ihome.model.runtime.module.ErrorMessageLogEntryInfo;
import technology.positivehome.ihome.server.model.SearchParam;
import technology.positivehome.ihome.server.persistence.model.ErrorMessageLogEntity;
import technology.positivehome.ihome.server.persistence.repository.ErrorMessageLogRepository;

import java.time.LocalDateTime;
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

    @EventListener
    public void onIHomeErrorEvent(IHomeErrorEvent event) {
        ErrorMessageLogEntity entity = new ErrorMessageLogEntity(
                null,
                LocalDateTime.now(),
                event.getType(),
                event.getMessage()
        );
        errorMessageLogRepository.create(entity);
    }
}