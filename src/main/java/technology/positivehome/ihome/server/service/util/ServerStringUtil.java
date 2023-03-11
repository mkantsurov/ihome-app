package technology.positivehome.ihome.server.service.util;

import java.nio.ByteBuffer;

/**
 * Created by maxim on 8/3/19.
 **/
public class ServerStringUtil {
    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        } else {
            int length = str.length();
            if (length == 0) {
                return false;
            } else {
                int i = 0;
                if (str.charAt(0) == '-') {
                    if (length == 1) {
                        return false;
                    }

                    i = 1;
                }

                while (i < length) {
                    char c = str.charAt(i);
                    if (c <= '/' || c >= ':') {
                        return false;
                    }

                    ++i;
                }

                return true;
            }
        }
    }

    public static float parseHalfPrecisionFloat(byte[] bytes) {
        short halfPrecision = ByteBuffer.wrap(bytes).getShort();

        int mantisa = halfPrecision & 0x03ff;
        int exponent = halfPrecision & 0x7c00;

        if (exponent == 0x7c00) {
            exponent = 0x3fc00;
        } else if (exponent != 0) {
            exponent += 0x1c000;
            if (mantisa == 0 && exponent > 0x1c400) {
                return Float.intBitsToFloat(
                        (halfPrecision & 0x8000) << 16 | exponent << 13 | 0x3ff);
            }
        } else if (mantisa != 0) {
            exponent = 0x1c400;
            do {
                mantisa <<= 1;
                exponent -= 0x400;
            } while ((mantisa & 0x400) == 0);
            mantisa &= 0x3ff;
        }

        return Float.intBitsToFloat(
                (halfPrecision & 0x8000) << 16 | (exponent | mantisa) << 13);
    }
}
