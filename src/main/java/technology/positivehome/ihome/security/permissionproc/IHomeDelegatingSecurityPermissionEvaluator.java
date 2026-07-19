package technology.positivehome.ihome.security.permissionproc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import technology.positivehome.ihome.security.auth.JwtAuthenticationToken;
import technology.positivehome.ihome.security.model.UserContext;
import technology.positivehome.ihome.security.service.UserService;

import java.io.Serializable;

/**
 * Created by maxim on 1/9/19.
 **/
@Component
public class IHomeDelegatingSecurityPermissionEvaluator implements PermissionEvaluator {

    private static final Logger log = LogManager.getLogger(IHomeDelegatingSecurityPermissionEvaluator.class);

    @Autowired
    private UserService userService;
    @Autowired
    private IHomeSecurityWebPermissionEvaluator iHomeSecurityWebPermissionEvaluator;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication instanceof JwtAuthenticationToken token) {
            return userService.findById(((UserContext) token.getPrincipal()).userId())
                    .map(userEntry -> iHomeSecurityWebPermissionEvaluator.hasPermission(userEntry, targetDomainObject, permission))
                    .orElse(false);
        }
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        log.warn("hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) is not implemented yet.");
        //implementation based on request attributes encapsulation is more flexible, so that method is not used
        //and was replaced with the corresponding method with 2 arguments
        return false;
    }

}

