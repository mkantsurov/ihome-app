package technology.positivehome.ihome.domain.runtime.module;

import technology.positivehome.ihome.domain.constant.ErrorEventType;

import java.time.ZonedDateTime;

public record ErrorMessageLogEntryInfo(String id, ZonedDateTime created, ErrorEventType errorEventType, String message) {
}
