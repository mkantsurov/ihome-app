package technology.positivehome.ihome.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import technology.positivehome.ihome.security.service.DenyAllIotSecurityPermissionEvaluator;
import technology.positivehome.ihome.security.service.IHomeSecurityPermissionEvaluator;
import technology.positivehome.ihome.security.service.IHomeSecurityPermissionEvaluatorImpl;

/**
 * Created by maxim on 1/9/19.
 **/
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    private final IHomeSecurityPermissionEvaluator[] permissionEvaluators;

    public MethodSecurityConfig(IHomeSecurityPermissionEvaluatorImpl iHomeSecurityPermissionEvaluator) {
        permissionEvaluators = new IHomeSecurityPermissionEvaluator[]{
                new DenyAllIotSecurityPermissionEvaluator(),
                iHomeSecurityPermissionEvaluator
        };
    }

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        return new IHomeMethodSecurityExpressionHandler(permissionEvaluators);
    }

}
