package technology.positivehome.ihome.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

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

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //Springboot/Angular2. This added to handle HTML5 urls (fallback url).
        registry.addResourceHandler("/**/*")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource requestedResource = location.createRelative(resourcePath);
                        return requestedResource.exists() && requestedResource.isReadable() ? requestedResource
                                : new ClassPathResource("/static/index.html");
                    }
                });
    }

}
