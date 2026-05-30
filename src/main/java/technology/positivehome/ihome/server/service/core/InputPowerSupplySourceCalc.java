package technology.positivehome.ihome.server.service.core;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import technology.positivehome.ihome.domain.constant.PreferredPowerSupplyMode;
import technology.positivehome.ihome.domain.runtime.sensor.ADCConnectedSensorData;
import technology.positivehome.ihome.server.processor.SystemProcessor;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class InputPowerSupplySourceCalc {

    private static Logger logger = LoggerFactory.getLogger(InputPowerSupplySourceCalc.class);

    private final Cache<Long, Double> luminosityCache = CacheBuilder.newBuilder().concurrencyLevel(4)
            .maximumSize(20)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build();

    private final AtomicReference<PreferredPowerSupplyMode> prevValue = new AtomicReference<>(PreferredPowerSupplyMode.CONVERTER);

    private final Map<Integer, DayTime> dayTimePerMonth = new HashMap<>();

    public InputPowerSupplySourceCalc() {
        dayTimePerMonth.put(Calendar.JANUARY, new DayTime(7, 40, 16, 20));
        dayTimePerMonth.put(Calendar.FEBRUARY, new DayTime(7, 21, 16, 59));
        dayTimePerMonth.put(Calendar.MARCH, new DayTime(6, 35, 17, 43));
        dayTimePerMonth.put(Calendar.APRIL, new DayTime(6, 35, 19, 26));
        dayTimePerMonth.put(Calendar.MAY, new DayTime(5, 41, 20, 6));
        dayTimePerMonth.put(Calendar.JUNE, new DayTime(5, 7, 20, 42));
        dayTimePerMonth.put(Calendar.JULY, new DayTime(5, 8, 20, 53));
        dayTimePerMonth.put(Calendar.AUGUST, new DayTime(5, 38, 20, 28));
        dayTimePerMonth.put(Calendar.SEPTEMBER, new DayTime(6, 17, 19, 36));
        dayTimePerMonth.put(Calendar.OCTOBER, new DayTime(6, 56, 18, 37));
        dayTimePerMonth.put(Calendar.NOVEMBER, new DayTime(6, 39, 16, 42));
        dayTimePerMonth.put(Calendar.DECEMBER, new DayTime(7, 20, 16, 11));
    }

    public double getAvgValue(long periodInMills) {
        Map<Long, Double> data = luminosityCache.asMap();
        int count = 0;
        double result = 0;
        long startTime = System.currentTimeMillis() - periodInMills;
        for (Map.Entry<Long, Double> measurement : data.entrySet()) {
            if (startTime < measurement.getKey()) {
                result += measurement.getValue();
                count++;
            }
        }
        if (count > 0) {
            return result / count;
        }
        return 0;
    }

    public PreferredPowerSupplyMode getPreferredPowerSupplyMode() {
        double luminosity = getAvgValue(TimeUnit.MINUTES.toMillis(10));
        PreferredPowerSupplyMode result = prevValue.get();
        if (isDay()) {
            if (PreferredPowerSupplyMode.ONLY_LED.equals(prevValue.get())) {
                if (luminosity >= 530) {
                    result = PreferredPowerSupplyMode.ONLY_LED;
                } else {
                    result = PreferredPowerSupplyMode.CONVERTER;
                }
            } else {
                if (luminosity >= 590) {
                    result = PreferredPowerSupplyMode.ONLY_LED;
                } else {
                    result = PreferredPowerSupplyMode.CONVERTER;
                }
            }
        } else {
            result = PreferredPowerSupplyMode.CONVERTER;
        }
        prevValue.set(result);
        return result;
    }
    public void dataUpdate(ADCConnectedSensorData adcConnectedSensorData) {
        luminosityCache.put(System.currentTimeMillis(), adcConnectedSensorData.getData());
    }

    public boolean isDay() {
        Calendar cal = Calendar.getInstance();
        return dayTimePerMonth.get(cal.get(Calendar.MONTH)).isDay();
    }

    private class DayTime {

        int startHr;
        int startMinute;
        int endHr;
        int endMinute;

        public DayTime(int startHr, int startMinute, int endHr, int endMinute) {
            this.startHr = startHr;
            this.startMinute = startMinute;
            this.endHr = endHr;
            this.endMinute = endMinute;
        }

        public boolean isDay() {
            Calendar cal = Calendar.getInstance();
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);
            int hourToStopConverter = endHr - 3;
            if (hour > startHr && hour < hourToStopConverter) {
                return true;
            } else if (startHr == hour) {
                return startMinute < minute;
            } else if (hourToStopConverter == hour) {
                return endMinute > minute;
            }
            return false;
        }
    }
}
