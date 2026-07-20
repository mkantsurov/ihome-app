package technology.positivehome.ihome.security.permissionproc.web;

import technology.positivehome.ihome.security.auth.JwtAuthenticationToken;
import technology.positivehome.ihome.security.model.permissionproc.AuthorizableObj;
import technology.positivehome.ihome.security.model.permissionproc.EntityAccessPermission;

public class WebAuthChainBuilder {

    private WebWebAuthProcessor chain;
    private WebWebAuthProcessor currentItem;

    public static WebAuthChainBuilder getInstance() {
        return new WebAuthChainBuilder();
    }

    public WebAuthChainBuilder next(WebWebAuthProcessor next) {
        if (chain == null) {
            chain = next;
            currentItem = next;
        } else {
            currentItem.addNext(next);
            currentItem = next;
        }
        return this;
    }

    public WebAuthProcessorChain build() {
        currentItem.addNext((JwtAuthenticationToken authentication, AuthorizableObj requestDetail, EntityAccessPermission permission) -> false);
        return chain;
    }
}
