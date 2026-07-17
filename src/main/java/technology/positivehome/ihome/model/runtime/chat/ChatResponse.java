package technology.positivehome.ihome.model.runtime.chat;

import java.util.List;

/**
 * Response returned to the user after processing their chat message.
 */
public record ChatResponse(
        String reply,
        List<String> actionsTaken
) {
    public static ChatResponse of(String reply) {
        return new ChatResponse(reply, List.of());
    }

    public static ChatResponse of(String reply, List<String> actionsTaken) {
        return new ChatResponse(reply, actionsTaken);
    }
}
