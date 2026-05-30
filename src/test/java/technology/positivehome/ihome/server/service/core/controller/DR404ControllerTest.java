package technology.positivehome.ihome.server.service.core.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

public class DR404ControllerTest {
    private static final Log log = LogFactory.getLog(DR404ControllerTest.class);
    public static void main(String[] argv) {

        for (int i=0; i<10; i++) {
            System.out.println("test: " + i);
            try {
                try (Socket clientSocket = new Socket("192.168.88.75", 8899)) {
                    try (OutputStream os = clientSocket.getOutputStream()) {
                        byte[] cmd = new byte[]{0x01, 0x03, 0, 0x0c, 0, 0x01, 0x44, 0x09};
                        os.write(cmd);
//                    os.flush();
                        InputStream is = clientSocket.getInputStream();
                        byte[] buffer = new byte[32];
                        int read;
                        do {
                            read = is.read(buffer);
                            System.out.println("Read: " + read);
                            System.out.print(Arrays.toString(buffer));
                        } while (read < 0);
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
