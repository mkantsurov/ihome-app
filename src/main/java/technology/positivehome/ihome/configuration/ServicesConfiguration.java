package technology.positivehome.ihome.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * The accounts Spring configuration.
 */
@Configuration
@EnableAsync
@Import({PersistenceConfiguration.class})
@ComponentScan(value = {
        "technology.positivehome.ihome.configuration",
        "technology.positivehome.ihome.security",
        "technology.positivehome.ihome.server"
})
public class ServicesConfiguration {

    public ServicesConfiguration() {
        super();
    }

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

}
