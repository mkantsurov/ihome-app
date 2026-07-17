package technology.positivehome.ihome.ai.deepseek;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the DeepSeek API integration.
 * Values are bound from the {@code ihome.ai.deepseek} prefix in application.yml.
 */
@ConfigurationProperties(prefix = "ihome.ai.deepseek")
public record DeepSeekConfig(
        String apiKey,
        String baseUrl,
        String model,
        int maxTokens
) {}
