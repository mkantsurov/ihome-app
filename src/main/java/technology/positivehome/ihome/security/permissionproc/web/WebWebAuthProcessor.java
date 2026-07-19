package technology.positivehome.ihome.security.permissionproc.web;

public interface WebWebAuthProcessor extends WebAuthProcessorChain {

    void addNext(WebAuthProcessorChain next);

    WebAuthProcessorChain next();

}
