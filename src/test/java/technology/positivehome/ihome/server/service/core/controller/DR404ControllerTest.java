package technology.positivehome.ihome.server.service.core.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

        for (int i=0; i<10; i++) {
            System.out.println("test: " + i);
            try {
                try (Socket clientSocket = new Socket("192.168.88.75", 8899)) {
                    clientSocket.setSoTimeout(3000);
                    try (OutputStream os = clientSocket.getOutputStream()) {
                        //read voltage
                        byte[] cmd = LiveDds238PowerMeterImpl.createReadCommand(0x01, 0x0c, 1);
                        os.write(cmd);
                        InputStream is = clientSocket.getInputStream();
                        byte[] buffer = new byte[32];
                        int read;
                        do {
                            read = is.read(buffer);
                        } while (read < 0);
                        System.out.println("Read: " + read);
                        System.out.print(Arrays.toString(buffer));
                        ByteBuffer bb = ByteBuffer.allocate(buffer.length).put(buffer);
                        bb.position(4);
                        System.out.println("Voltage:" + ByteBuffer.wrap(buffer, 3, 2).getShort()/10.0);

                        //read current
                        cmd = LiveDds238PowerMeterImpl.createReadCommand(0x01, 0x0d, 1);
                        os.write(cmd);
                        buffer = new byte[32];
                        do {
                            read = is.read(buffer);
                        } while (read < 0);
                        System.out.println("Read: " + read);
                        System.out.print(Arrays.toString(buffer));
                        bb = ByteBuffer.allocate(buffer.length).put(buffer);
                        bb.position(4);
                        System.out.println("Current:" + ByteBuffer.wrap(buffer, 3, 2).getShort()/100.0);

                        //read current frequency
                        cmd = LiveDds238PowerMeterImpl.createReadCommand(0x01, 0x11, 1);
                        os.write(cmd);
                        buffer = new byte[32];
                        do {
                            read = is.read(buffer);
                        } while (read < 0);
                        System.out.println("Read: " + read);
                        System.out.print(Arrays.toString(buffer));
                        bb = ByteBuffer.allocate(buffer.length).put(buffer);
                        bb.position(4);
                        System.out.println("Current:" + ByteBuffer.wrap(buffer, 3, 2).getShort()/100.0);

                        //read total energy
                        //word1
                        byte[] consumptionData = new byte[10];
                        cmd = LiveDds238PowerMeterImpl.createReadCommand(0x01, 0, 1);
                        os.write(cmd);
                        buffer = new byte[32];
                        do {
                            read = is.read(buffer);
                        } while (read < 0);
                        consumptionData[4] = buffer[3];
                        consumptionData[5] = buffer[4];

                        cmd = LiveDds238PowerMeterImpl.createReadCommand(0x01, 1, 1);
                        os.write(cmd);
                        buffer = new byte[32];
                        do {
                            read = is.read(buffer);
                        } while (read < 0);
                        consumptionData[6] = buffer[3];
                        consumptionData[7] = buffer[4];
                        consumptionData[0] = 0;
                        consumptionData[1] = 0;
                        consumptionData[2] = 0;
                        consumptionData[3] = 0;

                        System.out.println("Read: " + read);
                        System.out.print(Arrays.toString(buffer));
                        System.out.println("Total:" + ByteBuffer.wrap(consumptionData, 0, 8).getLong());
                        //word2

                    }
                }
                Thread.sleep(1000L);
            } catch (Exception ex) {
                ex.printStackTrace();
                log.error(ex);
            }
        }
    }
}
