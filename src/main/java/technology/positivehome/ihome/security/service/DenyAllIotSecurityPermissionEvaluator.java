package technology.positivehome.ihome.security.service;

import org.springframework.security.core.Authentication;
import technology.positivehome.ihome.security.util.IHomeApiTargetAccessType;
import technology.positivehome.ihome.security.util.IHomeApiTargetType;

import java.io.Serializable;

/**
 * Created by maxim on 2/6/21.
 **/
public class DenyAllIotSecurityPermissionEvaluator implements IHomeSecurityPermissionEvaluator {

    @Override
    public boolean hasPermission(Authentication authentication, IHomeApiTargetType targetType, IHomeApiTargetAccessType targetAccessType, Serializable[] ids) {
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, IHomeApiTargetAccessType targetAccessType) {
        return false;
    }
}
