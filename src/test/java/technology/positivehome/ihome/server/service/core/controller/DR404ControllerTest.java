package technology.positivehome.ihome.server.service.core.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class DR404ControllerTest {
    private static final Log log = LogFactory.getLog(DR404ControllerTest.class);
    public static void main(String[] argv) {

        for (int i=0; i<10; i++) {
            System.out.println("test: " + i);
            try {
                try (Socket clientSocket = new Socket("192.168.88.75", 80)) {
                    try (OutputStream os = clientSocket.getOutputStream()) {
                        byte[] cmd = new byte[]{0x01, 0x03, 0, 0x0c, 0, 0x01, 0x44, 0x09};
                        os.write(cmd);
//                    os.flush();
                        InputStream is = clientSocket.getInputStream();
                        byte[] buffer = new byte[1024];
                        int read;
                        while ((read = is.read(buffer)) != -1) {
                            String output = new String(buffer, 0, read);
                            System.out.print(output);
                            System.out.flush();
                        }
                        ;
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
