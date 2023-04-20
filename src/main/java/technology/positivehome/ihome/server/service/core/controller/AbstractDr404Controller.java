package technology.positivehome.ihome.server.service.core.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEventPublisher;
import technology.positivehome.ihome.domain.runtime.controller.ControllerConfigEntry;
import technology.positivehome.ihome.domain.runtime.controller.ControllerPortConfigEntry;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.domain.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.server.model.PortInfo;
import technology.positivehome.ihome.server.model.command.IHomeCommand;
import technology.positivehome.ihome.server.model.command.IHomePorts;
import technology.positivehome.ihome.server.service.core.controller.input.Dds238PowerMeter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public abstract class AbstractDr404Controller implements DR404Controller, DR404RequestExecutor {

    private static Log log = LogFactory.getLog(MegadPort.class);
    private final ApplicationEventPublisher eventPublisher;
    private String socketIpAddress;
    private int socketPortAddress;
    private final Map<Integer, PortInfo> portInfoByAddress = new ConcurrentHashMap<>();
    private final Map<Long, Dds238PowerMeter> dds238Ports = new ConcurrentHashMap<>();

    public AbstractDr404Controller(ApplicationEventPublisher eventPublisher, ControllerConfigEntry entry) {
        this.eventPublisher = eventPublisher;
        this.socketIpAddress = entry.ipAddr();
        this.socketPortAddress = entry.port();
        for (ControllerPortConfigEntry configEntry : entry.portConfig()) {
            portInfoByAddress.put(configEntry.portAddress(), new PortInfo(configEntry.id(), configEntry.type()));
            addPort(configEntry);
        }
    }

    @Override
    public void addPort(ControllerPortConfigEntry configEntry) {
        switch (configEntry.type()) {
            case DDS238_POWER_METER -> dds238Ports.put(configEntry.id(), createDds238PowerMeter(this, configEntry.portAddress()));
        }
    }

    @Override
    public <R> R performRequest(SocketExecutor<R> executor) throws IOException {
        try (Socket clientSocket = new Socket(socketIpAddress, socketPortAddress)) {
            clientSocket.setSoTimeout(3000);
            return executor.run(clientSocket);
        }
    }

    @Override
    public <R> R runCommand(IHomeCommand<R> iHomeCommand) throws MegadApiMallformedResponseException, PortNotSupporttedFunctionException, IOException, MegadApiMallformedUrlException, InterruptedException {
        return iHomeCommand.dispatch(IHomePorts.of(dds238Ports));
    }

    @Override
    public void onEvent(ControllerEventInfo eventInfo) {}

    protected abstract Dds238PowerMeter createDds238PowerMeter(DR404RequestExecutor dr404RequestExecutor, int portAddress);
}
