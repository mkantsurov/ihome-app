package technology.positivehome.ihome.server.service.core.controller.input;

import technology.positivehome.ihome.domain.constant.BinaryPortStatus;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.domain.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.mgr.processor.megad.controller.input.BinarySensor;

import java.io.IOException;

/**
 * Created by maxim on 8/29/17.
 **/
public class EmulatedBinarySensor implements BinarySensor {

    private int portAddress;

    public EmulatedBinarySensor(int portAddress) {
        this.portAddress = portAddress;
    }

    @Override
    public BinaryPortStatus getStatus() throws PortNotSupporttedFunctionException, IOException, MegadApiMallformedResponseException, MegadApiMallformedUrlException {
        String result = "ON/" + portAddress;
        return ResultMapper.binarySensorData(result);
    }
}
