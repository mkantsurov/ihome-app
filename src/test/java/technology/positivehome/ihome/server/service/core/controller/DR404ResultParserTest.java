package technology.positivehome.ihome.server.service.core.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import technology.positivehome.ihome.server.service.util.ServerStringUtil;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class DR404ResultParserTest {
    private static final Log log = LogFactory.getLog(DR404ResultParserTest.class);
    public static void main(String[] argv) {
        //[1, 3, 2, 8, 63, -1, -108, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
        byte[] data = {0x8, (byte)0x98};
        System.out.println("Parse result: " + ByteBuffer.wrap(data).getShort());

    }
}
