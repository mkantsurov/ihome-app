package technology.positivehome.ihome.security.permissionproc.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import technology.positivehome.ihome.security.model.permissionproc.AuthorizableObj;
import technology.positivehome.ihome.security.model.permissionproc.EntityAccessPermission;
import technology.positivehome.ihome.security.model.user.User;
import technology.positivehome.ihome.security.service.PermissionService;
import technology.positivehome.ihome.security.service.UserService;

import java.util.Collection;

public class IHomeSecurityWebPermissionEvaluator {


    private static final Logger log = LogManager.getLogger(IHomeSecurityWebPermissionEvaluator.class);
    private final WebAuthProcessorChain authChain;

    public IHomeSecurityWebPermissionEvaluator(UserService userService, PermissionService permissionService) {
        authChain = WebAuthChainBuilder.getInstance()
                .next(new ModuleWebAuthProcessor(userService, permissionService))
                .build();
    }

    public boolean hasPermission(User user, Object targetDomainObject, Object permission) {
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
            log.warn("Unable authorize request with " + targetDomainObject + " object.");
            return false;
        }
    }

}
