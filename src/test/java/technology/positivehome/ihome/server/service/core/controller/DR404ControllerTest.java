package technology.positivehome.ihome.server.service.core.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import technology.positivehome.ihome.domain.runtime.sensor.Dds238PowerMeterData;
import technology.positivehome.ihome.server.service.core.controller.input.Dds238Command;
import technology.positivehome.ihome.server.service.core.controller.input.LiveDds238PowerMeterImpl;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class DR404ControllerTest {
    private static final Log log = LogFactory.getLog(DR404ControllerTest.class);
    public static void main(String[] argv) {

        for (int i=0; i<3; i++) {
            System.out.println("test: " + i);
            queryDs238(1, "Input sensor: ");

            queryDs238(2, "Output sensor: ");
        }
    }

    private static void queryDs238(int port, String description) {
        try {
            try (Socket clientSocket = new Socket("192.168.88.75", 8899)) {
                clientSocket.setSoTimeout(3000);
                try (OutputStream os = clientSocket.getOutputStream()) {
                    InputStream is = clientSocket.getInputStream();
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
                    System.out.println(description + result.build().toString());
                }
            }
            Thread.sleep(1000L);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(ex);
        }
    }
}
