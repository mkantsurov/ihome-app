package technology.positivehome.ihome.model.runtime.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Incoming chat request from the UI.
 * The UI sends the full conversation history, including the latest user message
 * as the last entry. The server prepends a fresh system prompt before processing.
 *
 * <pre>
 * {
 *   "messages": [
 *     {"role": "user", "text": "What's the indoor temperature?"},
 *     {"role": "ai",   "text": "It's currently 21.5\u00b0C"},
 *     {"role": "user", "text": "Can you increase it to 23\u00b0?"}
 *   ]
 * }
 * </pre>
 */
public record IHomeChatRequest(
        @NotEmpty(message = "Messages must not be empty")
        List<ChatMessage> messages
) {

    /**
     * A single message in the conversation history.
     *
     * @param role "user" or "ai" (mapped to "assistant" internally)
     * @param text the message content
     */
    public record ChatMessage(
            @NotBlank(message = "Role must not be blank")
            String role,
            @NotBlank(message = "Text must not be blank")
            String text
    ) {}
}
