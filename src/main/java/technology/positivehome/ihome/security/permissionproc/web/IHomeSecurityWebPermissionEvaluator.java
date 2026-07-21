package technology.positivehome.ihome.security.permissionproc.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import technology.positivehome.ihome.security.auth.JwtAuthenticationToken;
import technology.positivehome.ihome.security.model.permissionproc.AuthorizableObj;
import technology.positivehome.ihome.security.model.permissionproc.EntityAccessPermission;
import technology.positivehome.ihome.security.service.PermissionService;
import technology.positivehome.ihome.security.service.UserService;

import java.util.Collection;

@Service
public class IHomeSecurityWebPermissionEvaluator {


    private static final Logger log = LogManager.getLogger(IHomeSecurityWebPermissionEvaluator.class);
    private final WebAuthProcessorChain authChain;

    public IHomeSecurityWebPermissionEvaluator(UserService userService, PermissionService permissionService) {
        authChain = WebAuthChainBuilder.getInstance()
                .next(new ModuleWebAuthProcessor(userService, permissionService))
                .build();
    }

    public boolean hasPermission(JwtAuthenticationToken user, Object targetDomainObject, Object permission) {
        EntityAccessPermission perm = EntityAccessPermission.of(permission);
        if (targetDomainObject instanceof Collection collection) {
            for (Object obj : collection) {
                if (!hasPermission(user, obj, permission)) {
                    return false;
                }
            }
            return true;
        } else if (targetDomainObject instanceof Object[]) {
            for (Object obj : ((Object[]) targetDomainObject)) {
                if (!hasPermission(user, obj, permission)) {
                    return false;
                }
            }
            return true;
        } else if (targetDomainObject instanceof AuthorizableObj authorizable) {
            return authChain.isAuthorized(user, authorizable, perm);
        } else {
            log.warn("Unable authorize request with %s object.".formatted(targetDomainObject));
            return false;
        }
    }

}
