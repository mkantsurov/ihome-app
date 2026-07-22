package technology.positivehome.ihome.security.model.permissionproc;

public record MooduleUpdateRequest(Long moduleId, boolean enableOnStartup, boolean moduleActive, int outputValue) implements AuthorizableObj {
}
