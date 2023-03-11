package technology.positivehome.ihome.server.service.core.controller.input;

import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.domain.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.domain.runtime.sensor.Dds238PowerMeterData;
import technology.positivehome.ihome.server.service.core.controller.DR404Port;
import technology.positivehome.ihome.server.service.core.controller.DR404RequestExecutor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class EmulatedDds238PowerMeterImpl extends DR404Port implements Dds238PowerMeter {

    public EmulatedDds238PowerMeterImpl(DR404RequestExecutor requestExecutor, int port) {
        super(requestExecutor, port);
    }

    @Override
    public Dds238PowerMeterData getData() throws PortNotSupporttedFunctionException, IOException, MegadApiMallformedResponseException, MegadApiMallformedUrlException {
        return getRequestExecutor().performRequest(socket -> {
            try (OutputStream os = socket.getOutputStream()) {
                byte[] cmd = new byte[]{(byte) port, 0x03, 0, 0x0c, 0, 0x01, 0x44, 0x09};
                os.write(cmd);
                InputStream is = socket.getInputStream();
                byte[] buffer = new byte[32];
                int read;
                do {
                    read = is.read(buffer);
                    System.out.println("Read: " + read);
                    System.out.print(Arrays.toString(buffer));
                } while (read < 0);
                return new Dds238PowerMeterData(ByteBuffer.wrap(buffer, 3, 2).getShort()/10.0);
            }
        });
    }
}
