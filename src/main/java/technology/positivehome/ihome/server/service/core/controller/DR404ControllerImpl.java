package technology.positivehome.ihome.server.service.core.controller;

import org.springframework.context.ApplicationEventPublisher;
import technology.positivehome.ihome.domain.runtime.controller.ControllerConfigEntry;
import technology.positivehome.ihome.server.service.core.controller.input.Dds238PowerMeter;
import technology.positivehome.ihome.server.service.core.controller.input.LiveDds238PowerMeterImpl;

public class DR404ControllerImpl extends AbstractDr404Controller {

    public DR404ControllerImpl(ApplicationEventPublisher eventPublisher, ControllerConfigEntry entry) {
        super(eventPublisher, entry);
    }

    @Override
    protected Dds238PowerMeter createDds238PowerMeter(DR404RequestExecutor dr404RequestExecutor, int portAddress) {
        return new LiveDds238PowerMeterImpl(dr404RequestExecutor, portAddress);
    }
}
