package technology.positivehome.ihome.model.runtime.chat;

import jakarta.validation.constraints.NotBlank;

/**
 * Incoming chat message from the user.
 */
public record ChatRequest(
        @NotBlank(message = "Message must not be blank")
        String message
) {}
