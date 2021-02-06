package technology.positivehome.ihome.configuration;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import technology.positivehome.ihome.security.service.DenyAllIotSecurityPermissionEvaluator;
import technology.positivehome.ihome.security.service.IHomeSecurityPermissionEvaluator;

import java.util.Optional;

/**
 * Created by maxim on 2/6/21.
 **/
public class IHomeMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {
    private final AuthenticationTrustResolver trustResolver;

    private final IHomeSecurityPermissionEvaluator[] permissionEvaluators;

    public IHomeMethodSecurityExpressionHandler(IHomeSecurityPermissionEvaluator[] permissionEvaluators) {
        this.trustResolver = new AuthenticationTrustResolverImpl();
        this.permissionEvaluators = permissionEvaluators;
    }

    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(
            Authentication authentication, MethodInvocation invocation) {
        IHomeMethodSecurityExpressionRoot root =
                new IHomeMethodSecurityExpressionRoot(authentication, invocation);

        String packageName = invocation.getMethod().getDeclaringClass().getPackageName();

        //Can't find standard permission evaluator usable, We will not initialize it. System will use DenyAllPermissionEvaluator as result.
        //Our permission evaluator IOTPermissionEvaluator has different interface and we will set it to our  IOTMethodSecurityExpressionRoot instead of standard one.
        //root.setPermissionEvaluator(getPermissionEvaluator());
        IHomeSecurityPermissionEvaluator permissionEvaluator = null;

        for (int i = 0; i < permissionEvaluators.length && permissionEvaluator == null; i++) {
            permissionEvaluator = permissionEvaluators[i];
        }
        root.setIHomeSecurityPermissionEvaluator(Optional.ofNullable(permissionEvaluator).orElse(new DenyAllIotSecurityPermissionEvaluator()));
        root.setTrustResolver(this.trustResolver);
        root.setRoleHierarchy(getRoleHierarchy());
        return root;
    }
}
