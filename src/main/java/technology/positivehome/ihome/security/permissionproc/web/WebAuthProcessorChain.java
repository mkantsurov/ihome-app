package technology.positivehome.ihome.security.permissionproc.web;

import technology.positivehome.ihome.security.model.permissionproc.AuthorizableObj;
import technology.positivehome.ihome.security.model.user.User;

public interface WebAuthProcessorChain {
    boolean isAuthorized(User authentication, AuthorizableObj requestDetail, EntityAccessPermission permission);
}
