package technology.positivehome.ihome.security.permissionproc.web;

import technology.positivehome.ihome.security.model.UserContext;
import technology.positivehome.ihome.security.model.user.User;
import technology.positivehome.ihome.security.service.UserService;

import java.util.Optional;

public abstract class AbstractWebWebAuthProcessor implements WebWebAuthProcessor {

    private WebAuthProcessorChain next;
    private final UserService userService;

    protected AbstractWebWebAuthProcessor(UserDataFactory userService) {
        this.userService = userService;
    }

    protected Optional<User> getRequester(UserContext context) {
        return userService.findById(context.userId());
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
