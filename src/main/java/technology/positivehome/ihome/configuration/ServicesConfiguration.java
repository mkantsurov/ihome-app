package technology.positivehome.ihome.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Executor;

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

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(300);
        executor.setThreadNamePrefix("AsyncTask-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "moduleJobTaskExecutor")
    public Executor moduleJobTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(300);
        executor.setThreadNamePrefix("ModuleJobTask-");
        executor.initialize();
        return executor;
    }

}
