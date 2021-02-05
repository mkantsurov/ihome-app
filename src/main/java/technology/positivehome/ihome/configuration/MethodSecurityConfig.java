package technology.positivehome.ihome.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import technology.positivehome.ihome.security.service.SecurityPermissionEvaluator;

/**
 * Created by maxim on 1/9/19.
 **/
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    @Bean
    public SecurityPermissionEvaluator securityPermissionEvaluator() {
        return new SecurityPermissionEvaluator();
    }

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler =
                new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(securityPermissionEvaluator());
        return expressionHandler;
    }

}
