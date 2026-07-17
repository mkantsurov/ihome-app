package technology.positivehome.ihome.server.service.core.module;

import technology.positivehome.ihome.model.constant.ModuleAssignment;
import technology.positivehome.ihome.model.constant.ModuleOperationMode;
import technology.positivehome.ihome.model.constant.ModuleProperty;
import technology.positivehome.ihome.model.constant.ModuleStartupMode;
import technology.positivehome.ihome.model.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.model.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.model.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.model.runtime.module.ModuleConfigElementEntry;
import technology.positivehome.ihome.model.runtime.module.ModulePropertyValue;
import technology.positivehome.ihome.model.runtime.module.OutputPortStatus;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * Created by maxim on 7/4/19.
 **/
public interface IHomeModuleSummary {

    ModuleAssignment getAssignment();

    ModuleOperationMode getMode();

    ModuleStartupMode getStartupMode();

    void  updateStartupMode(ModuleStartupMode moduleStartupMode);

    long getModuleId();

    long getGroupId();

    String getName();

    default boolean dimmableOutput() {
        return false;
    }

    List<ModuleConfigElementEntry> getInputPorts();

    OutputPortStatus getOutputPortStatus() throws URISyntaxException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, MegadApiMallformedUrlException, InterruptedException;

    Optional<ModulePropertyValue> getProperty(ModuleProperty key);

}
