package technology.positivehome.ihome.server.service.core.controller;

import org.springframework.context.ApplicationEventPublisher;
import technology.positivehome.ihome.domain.runtime.controller.ControllerConfigEntry;
import technology.positivehome.ihome.server.service.core.controller.input.Dds238PowerMeter;
import technology.positivehome.ihome.server.service.core.controller.input.EmulatedDds238PowerMeterImpl;

/**
 * Created by maxim on 3/4/23.
 **/
public class DR404EmulatedControllerImpl extends AbstractDr404Controller {

    public DR404EmulatedControllerImpl(ApplicationEventPublisher eventPublisher, ControllerConfigEntry entry) {
        super(eventPublisher, entry);
    }

    @Override
    protected Dds238PowerMeter createDds238PowerMeter(DR404RequestExecutor requestExecutor, int portAddress) {
        return new EmulatedDds238PowerMeterImpl(requestExecutor, portAddress);
    }
}
