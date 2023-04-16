package technology.positivehome.ihome.server.service.core.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.nio.ByteBuffer;

import static technology.positivehome.ihome.server.service.core.controller.input.LiveDds238PowerMeterImpl.calcCRC;

public class DR404CRCTest {
    private static final Log log = LogFactory.getLog(DR404CRCTest.class);
    public static void main(String[] argv) {
        //[1, 3, 2, 8, 63, -1, -108, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
//        byte[] data = {0x8, (byte)0x98};
//        System.out.println("Parse result: " + ByteBuffer.wrap(data).getShort());
        printCrc(new byte[]{0x01, 0x03, 0, 0x0c, 0, 0x01});
        printCrc(new byte[]{0x01, 0x03, 0, 0x0d, 0, 0x01});
        printCrc(new byte[]{0x01, 0x10, 0, 0x15, 0x02, 0x01});
    }

    private static void printCrc(byte[] cmd) {
        int crc = calcCRC(cmd, 6);
        System.out.println("CRC: " + Integer.toHexString(crc >> 8) + " " + Integer.toHexString(crc & 0xff));
    }
}
