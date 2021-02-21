package technology.positivehome.ihome.server.service.core.module;

import technology.positivehome.ihome.domain.constant.ModuleAssignment;
import technology.positivehome.ihome.domain.constant.ModuleOperationMode;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.domain.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.domain.runtime.module.ModuleConfigElementEntry;
import technology.positivehome.ihome.domain.runtime.module.OutputPortStatus;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by maxim on 7/4/19.
 **/
public interface IHomeModuleSummary {

    ModuleAssignment getAssignment();

    ModuleOperationMode getMode();

    long getModuleId();

    long getGroupId();

    String getName();

    List<ModuleConfigElementEntry> getInputPorts();

    OutputPortStatus getOutputPortStatus() throws URISyntaxException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, MegadApiMallformedUrlException, InterruptedException;

}
