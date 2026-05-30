package technology.positivehome.ihome.server.service.core.controller.input;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import technology.positivehome.ihome.domain.constant.MegadCommand;
import technology.positivehome.ihome.domain.constant.MegadRequestParam;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.domain.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.domain.runtime.sensor.ADCConnectedSensorData;
import technology.positivehome.ihome.server.service.core.controller.MegadPort;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by maxim on 8/2/21.
 **/
public class LiveADCConnectedSensor extends MegadPort implements ADCConnectedSensor {

    private static final long DATA_TTL = TimeUnit.MINUTES.toMillis(1);
    private final String url;
    private AtomicReference<ADCConnectedSensorData> adcCache = new AtomicReference<>();
    private AtomicLong lastRequestTs = new AtomicLong(0);

    public LiveADCConnectedSensor(String address, int addr) {
        super(addr);
        this.url = address;
    }

    @Override
    public ADCConnectedSensorData getData() throws PortNotSupporttedFunctionException, IOException, MegadApiMallformedResponseException, MegadApiMallformedUrlException {

        HttpUriRequest megadRequest;

        if (lastRequestTs.get() + DATA_TTL > System.currentTimeMillis()) {
            return adcCache.get();
        }
        try {
            megadRequest = RequestBuilder.get().setUri(new URI(url))
                    .addParameter(MegadRequestParam.pt.name(), Integer.toString(getAddress()))
                    .addParameter(MegadRequestParam.cmd.name(), MegadCommand.get.name())
                    .build();

        } catch (URISyntaxException e) {
            throw new MegadApiMallformedUrlException(e.getMessage());
        }

        String response = makeRequest("", megadRequest);
        ADCConnectedSensorData result = ResultMapper.adcSensorData(response);

        lastRequestTs.set(System.currentTimeMillis());
        adcCache.set(ResultMapper.adcSensorData(response));

        return result;
    }
}
