package technology.positivehome.ihome.server.processor;

import org.springframework.stereotype.Service;
import technology.positivehome.ihome.domain.constant.ErrorMessageLogSortRule;
import technology.positivehome.ihome.domain.runtime.module.ErrorMessageLogEntryInfo;
import technology.positivehome.ihome.server.model.SearchParam;

import java.util.List;


public interface ErrorMessageLogService {
    long countMessages(List<SearchParam> searchParams);

    List<ErrorMessageLogEntryInfo> searchMessages(List<SearchParam> searchParams, Integer page, Integer size, List<ErrorMessageLogSortRule> sort);
}
