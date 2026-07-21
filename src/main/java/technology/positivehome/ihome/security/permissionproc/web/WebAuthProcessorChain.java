package technology.positivehome.ihome.security.permissionproc.web;

import technology.positivehome.ihome.security.auth.JwtAuthenticationToken;
import technology.positivehome.ihome.security.model.permissionproc.AuthorizableObj;
import technology.positivehome.ihome.security.model.permissionproc.EntityAccessPermission;

public interface WebAuthProcessorChain {
    boolean isAuthorized(JwtAuthenticationToken authentication, AuthorizableObj requestDetail, EntityAccessPermission permission);
}
