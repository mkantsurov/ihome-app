package technology.positivehome.ihome.security.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import technology.positivehome.ihome.security.util.IHomeApiTargetAccessType;
import technology.positivehome.ihome.security.util.IHomeApiTargetType;

import java.io.Serializable;

/**
 * Created by maxim on 2/6/21.
 * <p>
 * Delegates permission checks to {@link PermissionService} — the single source of truth
 * for role-to-permission mappings.
 **/
@Component
public class IHomeSecurityPermissionEvaluatorImpl implements IHomeSecurityPermissionEvaluator {

    private final PermissionService permissionService;

    public IHomeSecurityPermissionEvaluatorImpl(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Override
    public boolean hasPermission(Authentication authentication, IHomeApiTargetType targetType, IHomeApiTargetAccessType targetAccessType, Serializable[] ids) {
        return permissionService.hasPermission(authentication, targetType, targetAccessType, ids);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, IHomeApiTargetAccessType targetAccessType) {
        return permissionService.hasPermission(authentication, targetDomainObject, targetAccessType);
    }
}
