package technology.positivehome.ihome.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        String index = "/index.html";

//        registry.addViewController("/main/batches").setViewName(index);
//        registry.addViewController("/main/profiles").setViewName(index);
//        registry.addViewController("/main/authorities").setViewName(index);
//        registry.addViewController("/main").setViewName(index);
//
//        registry.addViewController("/ecosystem-admin/ecosystems").setViewName(index);
//        registry.addViewController("/ecosystem-admin/admins").setViewName(index);
//        registry.addViewController("/ecosystem-admin/organizations").setViewName(index);
//        registry.addViewController("/ecosystem-admin/users").setViewName(index);
//        registry.addViewController("/ecosystem-admin/profiles").setViewName(index);
//        registry.addViewController("/ecosystem-admin").setViewName(index);

        registry.addViewController("/cert_validation").setViewName(index);
        registry.addViewController("/ssl_auth/**").setViewName(index);

        registry.addViewController("/reset/**").setViewName(index);
        registry.addViewController("/").setViewName(index);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/saapi/**")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
                .exposedHeaders("Access-Control-Expose-Headers", "Authorization")
                .allowedHeaders("Origin",
                        "Accept",
                        "X-Requested-With",
                        "Content-Type",
                        "Access-Control-Request-Method",
                        "Access-Control-Request-Headers",
                        "Access-Control-Allow-Methods",
                        "Access-Control-Allow-Origin")
                .allowedOrigins("*")
//            .allowCredentials(true)
                .maxAge(3600);
    }

}
