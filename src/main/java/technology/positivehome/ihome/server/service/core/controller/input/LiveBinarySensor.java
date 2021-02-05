package technology.positivehome.ihome.server.service.core.controller.input;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import technology.positivehome.ihome.domain.constant.BinaryPortStatus;
import technology.positivehome.ihome.domain.constant.MegadCommand;
import technology.positivehome.ihome.domain.constant.MegadRequestParam;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.domain.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.server.service.core.controller.MegadPort;
import technology.positivehome.mgr.processor.megad.controller.input.BinarySensor;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by maxim on 8/29/17.
 **/
public class LiveBinarySensor extends MegadPort implements BinarySensor {

    private final String moduleUrl;
    private static final long DATA_TTL = TimeUnit.SECONDS.toMillis(60);
    private AtomicLong lastRequestTs = new AtomicLong(0);
    private AtomicReference<BinaryPortStatus> stateCache = new AtomicReference<>(BinaryPortStatus.UNDEFINED);

    public LiveBinarySensor(String moduleUrl, int address) {
        super(address);
        this.moduleUrl = moduleUrl;
    }

    @Override
    public BinaryPortStatus getStatus() throws PortNotSupporttedFunctionException, IOException, MegadApiMallformedResponseException, MegadApiMallformedUrlException {

        if (lastRequestTs.get() + DATA_TTL > System.currentTimeMillis()) {
            return stateCache.get();
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

        stateCache.set(ResultMapper.binarySensorData(response));
        lastRequestTs.set(System.currentTimeMillis());
        return stateCache.get();
    }
}
