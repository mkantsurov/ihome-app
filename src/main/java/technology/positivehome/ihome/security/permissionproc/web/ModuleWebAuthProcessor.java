package technology.positivehome.ihome.security.permissionproc.web;

import technology.positivehome.ihome.security.auth.JwtAuthenticationToken;
import technology.positivehome.ihome.security.model.permissionproc.*;
import technology.positivehome.ihome.security.model.user.Role;
import technology.positivehome.ihome.security.service.PermissionService;
import technology.positivehome.ihome.security.service.UserService;

import java.util.Set;

import static technology.positivehome.ihome.security.model.user.Role.ADMIN;
import static technology.positivehome.ihome.security.model.user.Role.SUPERVISOR;

public class ModuleWebAuthProcessor extends AbstractWebWebAuthProcessor {
    public ModuleWebAuthProcessor(UserService userService, PermissionService permissionService) {
        super(userService, permissionService);
    }

    @Override
    public boolean isAuthorized(JwtAuthenticationToken authenticationToken, AuthorizableObj requestDetail, EntityAccessPermission permission) {
        Set<Role> roles = permissionService.extractRoles(authenticationToken.getAuthorities());
        if (requestDetail instanceof RootListObj searchReq) {
            if (TargetType.MODULE_LIST.equals(searchReq.requestedTargetType())) {
                if (roles.contains(ADMIN) || roles.contains(SUPERVISOR)) {
                    // please note, it is expected that the corresponding controller method will modify filter
                    // and exclude restricted queues for user
                    return true;
                }
            }
        } else if (requestDetail instanceof ModuleId) {
            if (roles.contains(ADMIN) || (roles.contains(SUPERVISOR) && EntityAccessPermission.READ.equals(permission))) {
                return true;
            }
        }
        return next().isAuthorized(authenticationToken, requestDetail, permission);
    }
}
