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
import java.nio.ByteBuffer;

public class EmulatedDds238PowerMeterImpl extends DR404Port implements Dds238PowerMeter {

    public EmulatedDds238PowerMeterImpl(DR404RequestExecutor requestExecutor, int port) {
        super(requestExecutor, port);
    }

    @Override
    public Dds238PowerMeterData getData() throws PortNotSupporttedFunctionException, IOException, MegadApiMallformedResponseException, MegadApiMallformedUrlException, InterruptedException {
        return getRequestExecutor().performRequest(socket -> {
            try (OutputStream os = socket.getOutputStream()) {
                InputStream is = socket.getInputStream();
                Dds238PowerMeterData.Builder result = Dds238PowerMeterData.builder();
                LiveDds238PowerMeterImpl.requestData(os, is, port, Dds238Command.READ_VOLTAGE, bytes -> {
                    result.voltage(ByteBuffer.wrap(bytes, 3, 2).getShort() / 10.0);
                });
                LiveDds238PowerMeterImpl.requestData(os, is, port, Dds238Command.READ_CURRENT, bytes -> {
                    result.current(ByteBuffer.wrap(bytes, 3, 2).getShort() / 100.0);
                });
                LiveDds238PowerMeterImpl.requestData(os, is, port, Dds238Command.READ_FREQUENCY, bytes -> {
                    result.freq(ByteBuffer.wrap(bytes, 3, 2).getShort() / 100.0);
                });
                LiveDds238PowerMeterImpl.requestData(os, is, port, Dds238Command.READ_TOTAL_ENERGY, bytes -> {
                    byte[] data = new byte[]{0, 0, 0, 0, bytes[3], bytes[4], bytes[5], bytes[6]};
                    result.total(ByteBuffer.wrap(data, 0, 8).getLong() / 100.0);
                });
                return result.build();
            }
        });
    }
}
