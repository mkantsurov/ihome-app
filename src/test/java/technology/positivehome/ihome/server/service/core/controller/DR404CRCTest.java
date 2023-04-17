package technology.positivehome.ihome.server.service.core.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static technology.positivehome.ihome.server.service.core.controller.input.LiveDds238PowerMeterImpl.addCRC;

public class DR404CRCTest {
    private static final Log log = LogFactory.getLog(DR404CRCTest.class);
    public static void main(String[] argv) {
        //[1, 3, 2, 8, 63, -1, -108, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
//        byte[] data = {0x8, (byte)0x98};
//        System.out.println("Parse result: " + ByteBuffer.wrap(data).getShort());
        testCrc(new byte[]{0x01, 0x03, 0, 0x0c, 0, 0x01});
        testCrc(new byte[]{0x01, 0x03, 0, 0x0d, 0, 0x01});
        testCrc(new byte[]{0x01, 0x10, 0, 0x15, 0x02, 0x01});
    }

    private static void testCrc(byte[] cmd) {
        byte[] result = addCRC(cmd);
        System.out.println("CRC: " + Integer.toHexString(result[cmd.length]) + " " + Integer.toHexString(result[cmd.length + 1]));
    }

    @ParameterizedTest
    @MethodSource("testData")
    void testCRC(byte[] data) throws Exception {
        byte[] cmd = Arrays.copyOf(data, data.length - 2);
        byte[] result = addCRC(cmd);
        int idx = data.length - 2;
        assertEquals(data[idx], result[idx]);
        assertEquals(data[idx+1], result[idx+1]);
    }

    private static Stream<Arguments> testData() {
        return Stream.of(
                Arguments.of(new byte[]  {0, 0x03, 0, 0x15, 0, 0x01, (byte) 0x94, 0x1F}),
                Arguments.of(new byte[]  {0x02, 0x03, 0, 0x15, 0, 0x01, (byte) 0x95, (byte) 0xfd}),
                Arguments.of(new byte[]  {0x01, 0x03, 0, 0, 0, 0x02, (byte) 0xc4, 0x0b}),
                Arguments.of(new byte[]  {0x01, 0x03, 0, 0x0c, 0, 0x01, (byte) 0x44, 0x09}),
                Arguments.of(new byte[]  {0x01, 0x03, 0, 0x0d, 0, 0x01, (byte) 0x15, (byte) 0xc9}),

//                Arguments.of(new byte[] {0x01, 0x10, 0, 0x15, 0x02, 0x01, 0x58, (byte) 0xae}),
                //                      [01]   [10] [00][15] [00][01]  [02] [02][01]     [64]         [35]
                Arguments.of(new byte[] {0x01, 0x10, 0, 0x15, 0, 0x01, 0x02, 0x02, 0x01, 0x64, (byte) 0x35})
        );
    }
}
