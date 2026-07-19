package technology.positivehome.ihome.security.permissionproc;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;

/**
 * Created by mkant on 7/20/2026.
 **/
public class IHomeMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {
    @Override
    protected PermissionEvaluator getPermissionEvaluator() {
        return super.getPermissionEvaluator();
    }
}
