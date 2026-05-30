package technology.positivehome.ihome.server.service.core.controller.input;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import technology.positivehome.ihome.domain.constant.MegadCommand;
import technology.positivehome.ihome.domain.constant.MegadRequestParam;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.domain.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.domain.runtime.sensor.Bme280TempHumidityPressureSensorData;
import technology.positivehome.ihome.server.service.core.controller.MegadPort;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by maxim on 12/29/17.
 **/
public class LiveBme280TempHumidityPressureSensor extends MegadPort implements Bme280TempHumidityPressureSensor {
    private static final Log log = LogFactory.getLog(LiveDht21TempHumiditySensor.class);
    private static final long DATA_TTL = TimeUnit.MINUTES.toMillis(1);

    private final String url;
    private AtomicReference<Bme280TempHumidityPressureSensorData> cache = new AtomicReference<>();
    private AtomicLong lastRequestTs = new AtomicLong(0);

    public LiveBme280TempHumidityPressureSensor(String address, int addr) {
        super(addr);
        this.url = address;
    }

    @Override
    public Bme280TempHumidityPressureSensorData getData() throws PortNotSupporttedFunctionException, IOException, MegadApiMallformedResponseException, MegadApiMallformedUrlException {
        if (lastRequestTs.get() + DATA_TTL > System.currentTimeMillis()) {
            return cache.get();
        }
        HttpUriRequest megadRequest = null;
        try {
            megadRequest = RequestBuilder.get().setUri(new URI(url))
                    .addParameter(MegadRequestParam.pt.name(), Integer.toString(getAddress()))
                    .addParameter(MegadRequestParam.cmd.name(), MegadCommand.get.name())
                    .build();
        } catch (URISyntaxException e) {
            throw new MegadApiMallformedUrlException(e.getMessage());
        }
        String response = makeRequest("", megadRequest);
        lastRequestTs.set(System.currentTimeMillis());
        cache.set(ResultMapper.bme280TempHumidityPressureData(response));
        return cache.get();
    }
}
