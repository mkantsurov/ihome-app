package technology.positivehome.ihome.server.service.core.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class DR404Port {
    private static Log log = LogFactory.getLog(DR404Port.class);
    protected final  DR404RequestExecutor requestExecutor;
    protected final int port;

    protected DR404Port(DR404RequestExecutor requestExecutor, int port) {
        this.requestExecutor = requestExecutor;
        this.port = port;
    }

    public DR404RequestExecutor getRequestExecutor() {
        return requestExecutor;
    }
}
