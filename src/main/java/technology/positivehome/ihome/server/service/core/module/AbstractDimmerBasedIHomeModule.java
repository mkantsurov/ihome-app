package technology.positivehome.ihome.server.service.core.module;

import technology.positivehome.ihome.domain.constant.DimmerPortStatus;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.domain.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.domain.runtime.module.ModuleConfigElementEntry;
import technology.positivehome.ihome.domain.runtime.module.ModuleConfigEntry;
import technology.positivehome.ihome.domain.runtime.module.OutputPortStatus;
import technology.positivehome.ihome.server.model.command.IHomeCommandFactory;
import technology.positivehome.ihome.server.service.core.SystemManager;

import java.io.IOException;
import java.util.*;

/**
 * Created by maxim on 7/2/19.
 **/
public abstract class AbstractDimmerBasedIHomeModule extends AbstractIHomeModule {

    private final Map<Long, ModuleConfigElementEntry> dimmerPorts = new HashMap<>();

    public AbstractDimmerBasedIHomeModule(SystemManager mgr, ModuleConfigEntry configEntry) {

        super(mgr, configEntry);

        for (ModuleConfigElementEntry ent : configEntry.getControls()) {
            switch (ent.getType()) {
                case DIMMER:
                    dimmerPorts.put(ent.getId(), ent);
                    break;
            }
        }
    }

    @Override
    protected OutputPortStatus getOutputPortStatus(ModuleConfigElementEntry port) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException {
        DimmerPortStatus status = getMgr().runCommand(IHomeCommandFactory.cmdGetDimmerStatus(port.getPort()));
        return OutputPortStatus.of(status);
    }

    @Override
    protected OutputPortStatus updateOutputPortState(ModuleConfigElementEntry port, OutputPortStatus status) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, InterruptedException, IOException {
        DimmerPortStatus newStatus = getMgr().runCommand(IHomeCommandFactory.cmdSetDimmerStatus(port.getPort(), DimmerPortStatus.of(status.value())));
        return OutputPortStatus.of(newStatus);
    }

    @Override
    protected List<ModuleConfigElementEntry> getOutputPorts() {
        List<ModuleConfigElementEntry> result = new ArrayList<>(dimmerPorts.values());
        result.sort(Comparator.comparingLong(ModuleConfigElementEntry::getId));
        return result;
    }
}
