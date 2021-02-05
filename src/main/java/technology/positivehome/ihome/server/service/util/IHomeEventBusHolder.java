package technology.positivehome.ihome.server.service.util;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import technology.positivehome.ihome.domain.runtime.event.IHomeEvent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by maxim on 6/24/19.
 **/
@Service
public class IHomeEventBusHolder implements IHomeEventBus, InitializingBean {
    private static final Log log = LogFactory.getLog(IHomeEventBusHolder.class);
    public static final int TIMEOUT_WAITING_ON_EVENTBUS_READY = 120;
    private EventBus eventBus = null;

    private final CountDownLatch sync = new CountDownLatch(1);

    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    @Override
    public void afterPropertiesSet() throws Exception {
        eventBus = new AsyncEventBus(executorService);
        sync.countDown();
    }

    @Override
    public void register(Object object) {
        try {
            if (sync.await(TIMEOUT_WAITING_ON_EVENTBUS_READY, TimeUnit.SECONDS)) {
                eventBus.register(object);
            } else {
                throw new IllegalStateException("Timeout waiting onEvent bus initialization");
            }
        } catch (InterruptedException e) {
            log.error("Event bus register event interrupted");
        }
    }

    @Override
    public void unregister(Object object) {
        try {
            if (sync.await(TIMEOUT_WAITING_ON_EVENTBUS_READY, TimeUnit.SECONDS)) {
                eventBus.unregister(object);
            } else {
                throw new IllegalStateException("Timeout waiting onEvent bus initialization");
            }
        } catch (InterruptedException e) {
            log.error("Event bus unregister event interrupted");
        }
    }

    @Override
    public void post(IHomeEvent event) {
        try {
            if (sync.await(TIMEOUT_WAITING_ON_EVENTBUS_READY, TimeUnit.SECONDS)) {
                eventBus.post(event);
            } else {
                throw new IllegalStateException("Timeout waiting onEvent bus initialization");
            }
        } catch (InterruptedException e) {
            log.error("Event bus post method interrupted");
        }
    }
}
