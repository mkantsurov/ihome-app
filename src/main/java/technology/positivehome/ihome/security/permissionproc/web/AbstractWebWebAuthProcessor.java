package technology.positivehome.ihome.security.permissionproc.web;

import technology.positivehome.ihome.security.service.PermissionService;
import technology.positivehome.ihome.security.service.UserService;

public abstract class AbstractWebWebAuthProcessor implements WebWebAuthProcessor {

    private WebAuthProcessorChain next;
    protected final PermissionService permissionService;

    public AbstractWebWebAuthProcessor(UserService userService, PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Override
    public final void addNext(WebAuthProcessorChain next) {
        this.next = next;
    }

    @Override
    public final WebAuthProcessorChain next() {
        return next;
    }

}
