package technology.positivehome.ihome.server.service.core.controller;

import technology.positivehome.ihome.model.runtime.controller.ControllerPortConfigEntry;
import technology.positivehome.ihome.model.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.model.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.model.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.server.model.command.IHomeCommand;

import java.io.IOException;

/**
 * Created by maxim on 3/4/23.
 **/
public interface IHomeController {
    void addPort(ControllerPortConfigEntry configEntry);

    <R> R runCommand(IHomeCommand<R> iHomeCommand) throws MegadApiMallformedResponseException, PortNotSupporttedFunctionException, IOException, MegadApiMallformedUrlException, InterruptedException;

    void onEvent(ControllerEventInfo eventInfo);
}
