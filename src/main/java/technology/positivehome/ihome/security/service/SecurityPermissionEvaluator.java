package technology.positivehome.ihome.security.service;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Created by maxim on 1/9/19.
 **/
@Component
public class SecurityPermissionEvaluator implements PermissionEvaluator {

    @Override
    public boolean hasPermission(Authentication auth, Object targetDomainObject, Object permission) {

        if ((auth == null) || targetDomainObject == null || !(permission instanceof String)) {
            return false;
        }

        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable id, String targetType,
                                 Object permission) {
        if (targetType == null) {
            return false;
        }
        if (!(id instanceof Long)) {
            return false;
        }
        return false;
    }

}

