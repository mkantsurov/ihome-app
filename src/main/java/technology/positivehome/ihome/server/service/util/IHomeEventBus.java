package technology.positivehome.ihome.server.service.util;

import technology.positivehome.ihome.domain.runtime.event.IHomeEvent;

/**
 * Created by maxim on 6/24/19.
 **/
public interface IHomeEventBus {

    void register(Object object);

    void unregister(Object object);

    void post(IHomeEvent event);

}
