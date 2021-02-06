package technology.positivehome.ihome;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import technology.positivehome.ihome.configuration.ServicesConfiguration;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

@SpringBootApplication
@Configuration
@Import({ServicesConfiguration.class})
public class ServerApplication {

    private static final Log log = LogFactory.getLog(ServerApplication.class);

    public static void main(String[] args) {
//        System.setProperty("spring.config.name", "application");
        try {
            Thread.sleep(5000);
            SpringApplication.run(ServerApplication.class, args);
        } catch (Exception ex) {
            log.error(ex);
        }
    }

    @Bean
    public ObjectMapper mapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    @Bean
    public HttpFirewall defaultHttpFirewall() {
        return new DefaultHttpFirewall();
    }
}
