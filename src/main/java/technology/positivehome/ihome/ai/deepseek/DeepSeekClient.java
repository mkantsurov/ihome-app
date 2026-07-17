package technology.positivehome.ihome.ai.deepseek;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import technology.positivehome.ihome.ai.deepseek.model.ChatRequest;
import technology.positivehome.ihome.ai.deepseek.model.ChatResponse;

/**
 * HTTP client for the DeepSeek chat completions API.
 * Uses Spring WebClient (non-blocking) for API calls.
 */
@Component
public class DeepSeekClient {

    private static final Logger log = LoggerFactory.getLogger(DeepSeekClient.class);

    private final WebClient webClient;
    private final DeepSeekConfig config;
    private final ObjectMapper objectMapper;

    public DeepSeekClient(DeepSeekConfig config, ObjectMapper objectMapper) {
        this.config = config;
        this.objectMapper = objectMapper;
        this.webClient = WebClient.builder()
                .baseUrl(config.baseUrl())
                .defaultHeader("Authorization", "Bearer " + config.apiKey())
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * Sends a chat completion request to the DeepSeek API.
     *
     * @param request the chat request with messages and optional tools
     * @return the API response
     * @throws DeepSeekApiException if the API returns an error
     */
    public ChatResponse sendChat(ChatRequest request) {
        log.debug("Sending chat request to DeepSeek: model={}, messages={}, tools={}",
                request.model(), request.messages().size(),
                request.tools() != null ? request.tools().size() : 0);

        try {
            ChatResponse response = webClient.post()
                    .uri("/chat/completions")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(ChatResponse.class)
                    .block();

            if (response == null) {
                throw new DeepSeekApiException("Received null response from DeepSeek API");
            }

            log.debug("Received response from DeepSeek: choices={}, tokens={}",
                    response.choices() != null ? response.choices().size() : 0,
                    response.usage() != null ? response.usage().totalTokens() : 0);

            return response;
        } catch (Exception e) {
            log.error("Failed to call DeepSeek API: {}", e.getMessage(), e);
            throw new DeepSeekApiException("DeepSeek API call failed: " + e.getMessage(), e);
        }
    }

    /**
     * Returns the configured model name.
     */
    public String getModel() {
        return config.model();
    }

    /**
     * Returns the configured max tokens.
     */
    public int getMaxTokens() {
        return config.maxTokens();
    }
}
