package technology.positivehome.ihome.server.service.core.controller.input;

import com.google.common.base.Strings;
import technology.positivehome.ihome.domain.constant.BinaryPortStatus;
import technology.positivehome.ihome.domain.runtime.sensor.Bme280TempHumidityPressureSensorData;
import technology.positivehome.ihome.domain.runtime.sensor.Dht21TempHumiditySensorData;
import technology.positivehome.ihome.domain.runtime.sensor.Ds18b20TempSensorData;
import technology.positivehome.ihome.domain.runtime.sensor.Tsl2591LuminositySensorData;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by maxim on 9/1/17.
 **/
public class ResultMapper {

    public static final String TEMP_PREFIX = "temp:";
    public static final String HUMIDITY_PREFIX = "hum:";
    public static final String PRESSURE_PREFIX = "press:";
    public static final String LUMINOSITY_PREFIX = "lux:";

    private static final Pattern dht21ValuesPattern = Pattern.compile("-?\\d+(,\\d+)*?\\.?\\d+?");


    public static Ds18b20TempSensorData ds18b20TempSensorData(String response) {
        Ds18b20TempSensorData result = new Ds18b20TempSensorData();

        if (!Strings.isNullOrEmpty(response) && response.startsWith(TEMP_PREFIX)) {
            result.setData(Double.parseDouble(response.substring(TEMP_PREFIX.length())));
        }
        return result;
    }

    public static BinaryPortStatus binarySensorData(String result) {
        if (!Strings.isNullOrEmpty(result)) {
            if (result.startsWith("ON")) {
                return BinaryPortStatus.ENABLED;
            } else {
                return BinaryPortStatus.DISABLED;
            }
        }
        return BinaryPortStatus.UNDEFINED;
    }

    public static Dht21TempHumiditySensorData dht21TempHumiditySensorData(String response) {
        Dht21TempHumiditySensorData result = new Dht21TempHumiditySensorData();
        if (!Strings.isNullOrEmpty(response)) {
            Matcher m = dht21ValuesPattern.matcher(response.substring(response.indexOf(TEMP_PREFIX) + TEMP_PREFIX.length()));
            for (int i = 0; i < 2 && m.find(); i++) {
                switch (i) {
                    case 0:
                        result.setTemperature(Double.parseDouble(m.group()));
                        break;
                    case 1:
                        result.setHumidity(Double.parseDouble(m.group()));
                        break;
                }
            }
        }
        return result;
    }

    public static Bme280TempHumidityPressureSensorData bme280TempHumidityPressureData(String response) {
        Bme280TempHumidityPressureSensorData result = new Bme280TempHumidityPressureSensorData();
        //"temp:24.00/press:755.26/hum:44.678"
        Map<String, String> data = new HashMap<>();
        StringTokenizer st = new StringTokenizer(response, "/");

        while (st.hasMoreTokens()) {
            String value = st.nextToken();

            if (value.startsWith(TEMP_PREFIX)) {
                result.setTemperature(Double.parseDouble(value.substring(TEMP_PREFIX.length())));
            } else if (value.startsWith(HUMIDITY_PREFIX)) {
                result.setHumidity(Double.parseDouble(value.substring(HUMIDITY_PREFIX.length())));
            } else if (value.startsWith(PRESSURE_PREFIX)) {
                result.setPressure(Double.parseDouble(value.substring(PRESSURE_PREFIX.length())));
            }
        }
        return result;
    }

    public static Tsl2591LuminositySensorData tsl2591LuminositySensorData(String response) {
        Tsl2591LuminositySensorData result = new Tsl2591LuminositySensorData();
        if (!Strings.isNullOrEmpty(response)) {
            result.setData(Double.parseDouble(response));
        }
        return result;
    }
}
