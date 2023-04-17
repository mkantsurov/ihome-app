package technology.positivehome.ihome.server.service.core.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import technology.positivehome.ihome.server.service.core.controller.input.LiveDds238PowerMeterImpl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

import static technology.positivehome.ihome.server.service.core.controller.input.LiveDds238PowerMeterImpl.*;

public class DR404ControllerChangeAddress {
    private static final Log log = LogFactory.getLog(DR404ControllerChangeAddress.class);

    public static void main(String[] argv) {
        try {
            readPortAddr((byte) 0x01);
            readPortAddr((byte) 0x02);
//            System.out.println("Write register 0x15");
//            try (Socket clientSocket = new Socket("192.168.88.75", 8899)) {
//                try (OutputStream os = clientSocket.getOutputStream()) {
//                    byte[] cmd = createWriteCommand(0x02, 0x15, 0x0201);
//                    os.write(cmd);
//                }
//            }
//            System.out.println("Done");
//            System.out.println("Check result");
//            readPortAddr((byte) 0x00);
//            Thread.sleep(1000L);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(ex);
        }
    }

    private static void readPortAddr(byte address) throws IOException {
        System.out.println("Read port "  + Integer.toHexString(address));
        try (Socket clientSocket = new Socket("192.168.88.75", 8899)) {
            try (OutputStream os = clientSocket.getOutputStream()) {
                byte[] cmd = createReadCommand(address, 0x15, 0x02);
                os.write(cmd);
                InputStream is = clientSocket.getInputStream();
                byte[] buffer = new byte[32];
                int read;
                do {
                    read = is.read(buffer);
                } while (read < 0);
                System.out.println("Read port settings: " + read);
                System.out.println(Arrays.toString(buffer));
            }
        }
    }
}
