package technology.positivehome.ihome.server.service.core.controller;

import technology.positivehome.ihome.domain.runtime.controller.ControllerPortConfigEntry;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.domain.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.server.model.command.IHomeCommand;
import technology.positivehome.ihome.server.model.command.IHomePorts;
import technology.positivehome.ihome.server.service.core.controller.input.Dds238PowerMeter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractDr404Controller implements DR404Controller {

    private final Map<Long, Dds238PowerMeter> dds238Ports = new ConcurrentHashMap<>();
    @Override
    public void addPort(ControllerPortConfigEntry configEntry) {
        switch (configEntry.type()) {
            case DDS238_POWER_METER -> dds238Ports.put(configEntry.id(), createDds238PowerMeter(configEntry.portAddress()));
        }
    }

    @Override
    public <R> R runCommand(IHomeCommand<R> iHomeCommand) throws MegadApiMallformedResponseException, PortNotSupporttedFunctionException, IOException, MegadApiMallformedUrlException, InterruptedException {
        return iHomeCommand.dispatch(IHomePorts.of(dds238Ports));
    }

    @Override
    public void onEvent(ControllerEventInfo eventInfo) {}

    protected abstract Dds238PowerMeter createDds238PowerMeter(int portAddress);
}
