package technology.positivehome.ihome.server.service.core.controller.output;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import technology.positivehome.ihome.domain.constant.DimmerPortStatus;
import technology.positivehome.ihome.domain.constant.MegadCommand;
import technology.positivehome.ihome.domain.constant.MegadRequestParam;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.domain.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.server.service.core.controller.MegadPort;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by maxim on 12/3/17.
 **/
public class LiveDimmerOutput extends MegadPort implements DimmerOutput {

    private static final long DATA_TTL = TimeUnit.SECONDS.toMillis(1);

    private final String moduleUrl;
    private final AtomicReference<DimmerPortStatus> cache = new AtomicReference<>(DimmerPortStatus.OFF);
    private final AtomicLong lastRequestTs = new AtomicLong(0);

    public LiveDimmerOutput(String moduleUrl, int address) {
        super(address);
        this.moduleUrl = moduleUrl;
    }

    @Override
    public DimmerPortStatus getStatus() throws PortNotSupporttedFunctionException, IOException, MegadApiMallformedResponseException, MegadApiMallformedUrlException {
        if (lastRequestTs.get() + DATA_TTL > System.currentTimeMillis()) {
            return cache.get();
        }
        HttpUriRequest megadRequest = null;
        try {
            megadRequest = RequestBuilder.get().setUri(new URI(moduleUrl))
                    .addParameter(MegadRequestParam.pt.name(), Integer.toString(getAddress()))
                    .addParameter(MegadRequestParam.cmd.name(), MegadCommand.get.name())
                    .build();
        } catch (URISyntaxException e) {
            throw new MegadApiMallformedUrlException(e.getMessage());
        }
        String response = makeRequest("", megadRequest);
        return DimmerPortStatus.of(Integer.parseInt(response.trim()));
    }

    @Override
    public DimmerPortStatus setState(DimmerPortStatus value) throws PortNotSupporttedFunctionException, IOException, MegadApiMallformedResponseException, MegadApiMallformedUrlException, InterruptedException {
        try {
            //?pt=27&pwm=100
            StringBuilder urlWithQuery = new StringBuilder(moduleUrl).append("?")
                    .append(MegadRequestParam.pt.name()).append("=").append(Integer.toString(getAddress())).append("&")
                    .append(MegadRequestParam.pwm.name()).append("=").append(value);

            HttpUriRequest megadRequest = RequestBuilder.get().setUri(new URI(urlWithQuery.toString())).build();
            String response = makeRequest("", megadRequest);
            Thread.sleep(100L);
            lastRequestTs.set(0);
            return getStatus();
        } catch (URISyntaxException e) {
            throw new MegadApiMallformedUrlException(e.getMessage());
        }
    }
}
