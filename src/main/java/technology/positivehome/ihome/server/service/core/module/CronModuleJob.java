package technology.positivehome.ihome.server.service.core.module;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by maxim on 7/1/19.
 **/
public abstract class CronModuleJob {
    private static final Log log = LogFactory.getLog(CronModuleJob.class);
    private final long periodInMills;
    private AtomicLong lastExecuted = new AtomicLong(0);

    public CronModuleJob(long periodInMills) {
        this.periodInMills = periodInMills;
    }

    protected abstract void execute() throws Exception;

    public boolean isNeedsToBeExecuted() {
        return System.currentTimeMillis() - lastExecuted.get() > periodInMills;
    }

    public void run() {
        try {
            execute();
        } catch (Exception ex) {
            log.error("Problem running " + this.getClass().getName() + " JOB task", ex);
        } finally {
            lastExecuted.set(System.currentTimeMillis());
        }

    }
}
