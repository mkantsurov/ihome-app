package technology.positivehome.ihome.security.permissionproc.web;

import technology.positivehome.ihome.security.auth.JwtAuthenticationToken;
import technology.positivehome.ihome.security.model.permissionproc.*;
import technology.positivehome.ihome.security.model.user.Role;
import technology.positivehome.ihome.security.service.PermissionService;
import technology.positivehome.ihome.security.service.UserService;
import technology.positivehome.ihome.security.util.IHomeApiTargetAccessType;

import java.util.Set;

import static technology.positivehome.ihome.security.model.user.Role.*;

public class ModuleWebAuthProcessor extends AbstractWebWebAuthProcessor {
    public ModuleWebAuthProcessor(UserService userService, PermissionService permissionService) {
        super(userService, permissionService);
    }

    @Override
    public boolean isAuthorized(JwtAuthenticationToken authenticationToken, AuthorizableObj requestDetail, EntityAccessPermission permission) {
        Set<Role> roles = permissionService.extractRoles(authenticationToken.getAuthorities());
        if (requestDetail instanceof RootListObj(TargetType requestedTargetType)) {
            if (TargetType.MODULE_LIST.equals(requestedTargetType)) {
                if (roles.contains(ADMIN)
                        || roles.contains(SUPERVISOR)
                        || roles.contains(CHILDREN_ROOM1_MANAGER)
                        || roles.contains(CHILDREN_ROOM2_MANAGER)) {
                    return true;
                } else {
                    return false;
                }
            }
        } else if (requestDetail instanceof ModuleId(Long id)) {
            if (roles.contains(ADMIN) || (roles.contains(SUPERVISOR) && EntityAccessPermission.READ.equals(permission))) {
                return true;
            } else {
                return permissionService.hasModulePermission(authenticationToken, id, IHomeApiTargetAccessType.WRITE);
            }
        } else if (requestDetail instanceof MooduleUpdateRequest req) {
            if (roles.contains(ADMIN)) {
                return true;
            } else {
                return permissionService.hasModulePermission(authenticationToken, req);
            }
        }
        return next().isAuthorized(authenticationToken, requestDetail, permission);
    }
}
