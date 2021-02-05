package technology.positivehome.ihome.server.service.util;

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
}
