package technology.positivehome.ihome.domain.runtime.exception;

import technology.positivehome.ihome.domain.constant.IHomePortType;

/**
 * Created by maxim on 6/30/19.
 **/
public class PortNotSupporttedFunctionException extends AbstractIHomeException {
    public PortNotSupporttedFunctionException(String finctionName, int address, IHomePortType type) {
        super("Function \"" + finctionName + "\" isn't supported by port: " + address + " with type " + type.name());
    }
}
