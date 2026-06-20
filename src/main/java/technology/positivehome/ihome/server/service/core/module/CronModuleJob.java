package technology.positivehome.ihome.server.service.core.module;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEventPublisher;
import technology.positivehome.ihome.model.constant.ErrorEventType;
import technology.positivehome.ihome.model.runtime.event.IHomeErrorEvent;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by maxim on 7/1/19.
 **/
public abstract class CronModuleJob {
    private static final Log log = LogFactory.getLog(CronModuleJob.class);
    private final long periodInMills;
    private final ApplicationEventPublisher eventPublisher;
    private AtomicLong lastExecuted = new AtomicLong(0);

    public CronModuleJob(long periodInMills) {
        this.periodInMills = periodInMills;
        this.eventPublisher = null;
    }

    public CronModuleJob(long periodInMills, ApplicationEventPublisher eventPublisher) {
        this.periodInMills = periodInMills;
        this.eventPublisher = eventPublisher;
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
            if (eventPublisher != null) {
                eventPublisher.publishEvent(new IHomeErrorEvent(this, ErrorEventType.MODULE_JOB_TASK_EXECUTION, "Problem running " + this.getClass().getName() + " JOB task: " + ex.getMessage()));
            }
        } finally {
            lastExecuted.set(System.currentTimeMillis());
        }
    }
}
