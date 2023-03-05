package technology.positivehome.ihome.server.service.core.module;

import technology.positivehome.ihome.domain.constant.BinaryPortStatus;
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
public abstract class AbstractRelayBasedIHomeModule extends AbstractIHomeModule {

    private final Map<Long, ModuleConfigElementEntry> relayPorts = new HashMap<>();

    public AbstractRelayBasedIHomeModule(SystemManager mgr, ModuleConfigEntry configEntry) {

        super(mgr, configEntry);

        for (ModuleConfigElementEntry ent : configEntry.getControls()) {
            switch (ent.getType()) {
                case RELAY:
                    relayPorts.put(ent.getId(), ent);
            }
        }
    }

    @Override
    protected OutputPortStatus getOutputPortStatus(long port) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException {
        return OutputPortStatus.of(getMgr().runCommand(IHomeCommandFactory.cmdGetRelayStatus(port)));
    }

    @Override
    protected OutputPortStatus updateOutputPortState(long port, OutputPortStatus status) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, InterruptedException, IOException {
        return OutputPortStatus.of(getMgr().runCommand(IHomeCommandFactory.cmdSetRelayStatus(port, status.isEnabled() ? BinaryPortStatus.ENABLED : BinaryPortStatus.DISABLED)));
    }

    @Override
    protected List<ModuleConfigElementEntry> getOutputPorts() {
        List<ModuleConfigElementEntry> result = new ArrayList<>(relayPorts.values());
        result.sort(Comparator.comparingLong(ModuleConfigElementEntry::getId));
        return result;
    }
}
