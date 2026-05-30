package technology.positivehome.ihome.server.service.core.controller.input;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import technology.positivehome.ihome.domain.constant.MegadCommand;
import technology.positivehome.ihome.domain.constant.MegadRequestParam;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.domain.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.domain.runtime.sensor.Tsl2591LuminositySensorData;
import technology.positivehome.ihome.server.service.core.controller.MegadPort;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by maxim on 2/23/20.
 **/
public class LiveTsl2591LuminositySensor extends MegadPort implements Tsl2591LuminositySensor {

    private static final long DATA_TTL = TimeUnit.MINUTES.toMillis(3);
    private final String url;
    private AtomicReference<Tsl2591LuminositySensorData> luminosityCache = new AtomicReference<>();
    private AtomicLong lastRequestTs = new AtomicLong(0);

    public LiveTsl2591LuminositySensor(String address, int addr) {
        super(addr);
        this.url = address;
    }

    @Override
    public Tsl2591LuminositySensorData getData() throws PortNotSupporttedFunctionException, IOException, MegadApiMallformedResponseException, MegadApiMallformedUrlException {
        HttpUriRequest megadRequest;

        if (lastRequestTs.get() + DATA_TTL > System.currentTimeMillis()) {
            return luminosityCache.get();
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
        Tsl2591LuminositySensorData result = ResultMapper.tsl2591LuminositySensorData(response);

        lastRequestTs.set(System.currentTimeMillis());
        luminosityCache.set(ResultMapper.tsl2591LuminositySensorData(response));

        return result;
    }
}
