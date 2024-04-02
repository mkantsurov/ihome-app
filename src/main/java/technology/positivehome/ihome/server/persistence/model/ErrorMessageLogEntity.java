package technology.positivehome.ihome.server.persistence.model;

import technology.positivehome.ihome.domain.constant.ErrorEventType;

import java.time.LocalDateTime;
import java.util.UUID;

public record ErrorMessageLogEntity(UUID id, LocalDateTime created, ErrorEventType type, String message) {
}
