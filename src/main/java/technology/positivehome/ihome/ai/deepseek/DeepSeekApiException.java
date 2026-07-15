package technology.positivehome.ihome.ai.deepseek;

/**
 * Exception thrown when the DeepSeek API call fails.
 */
public class DeepSeekApiException extends RuntimeException {

    public DeepSeekApiException(String message) {
        super(message);
    }

    public DeepSeekApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
