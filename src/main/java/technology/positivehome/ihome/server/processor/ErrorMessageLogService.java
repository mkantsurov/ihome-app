package technology.positivehome.ihome.server.processor;

import technology.positivehome.ihome.model.constant.ErrorMessageLogSortRule;
import technology.positivehome.ihome.model.runtime.module.ErrorMessageLogEntryInfo;
import technology.positivehome.ihome.server.model.SearchParam;

import java.util.List;


public interface ErrorMessageLogService {
    long countMessages(List<SearchParam> searchParams);

    List<ErrorMessageLogEntryInfo> searchMessages(List<SearchParam> searchParams, Integer page, Integer size, List<ErrorMessageLogSortRule> sort);
}
