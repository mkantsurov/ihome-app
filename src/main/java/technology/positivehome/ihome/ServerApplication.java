package technology.positivehome.ihome;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.web.client.RestTemplate;
import technology.positivehome.ihome.configuration.ServicesConfiguration;

import java.util.ArrayList;
import java.util.List;

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
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new StringHttpMessageConverter());
        converters.add(new MappingJackson2HttpMessageConverter());
        converters.add(new ByteArrayHttpMessageConverter());

        restTemplate.setMessageConverters(converters);
        return restTemplate;
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
