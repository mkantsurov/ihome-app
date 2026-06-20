package technology.positivehome.mgr.processor.megad.controller.input;

import technology.positivehome.ihome.model.constant.BinaryPortStatus;
import technology.positivehome.ihome.model.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.model.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.model.runtime.exception.PortNotSupporttedFunctionException;

import java.io.IOException;

/**
 * Created by maxim on 8/28/17.
 **/
public interface BinarySensor {

    BinaryPortStatus getStatus() throws PortNotSupporttedFunctionException, IOException, MegadApiMallformedResponseException, MegadApiMallformedUrlException;

}
