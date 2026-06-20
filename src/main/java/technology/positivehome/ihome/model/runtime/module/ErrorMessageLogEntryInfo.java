package technology.positivehome.ihome.model.runtime.module;

import technology.positivehome.ihome.model.constant.ErrorEventType;

import java.time.ZonedDateTime;

public record ErrorMessageLogEntryInfo(String id, ZonedDateTime created, ErrorEventType errorEventType, String message) {
}
