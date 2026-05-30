package technology.positivehome.ihome.server.service.core.controller;

import com.google.common.base.Strings;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * Created by maxim on 6/30/19.
 **/
public class MegadPort {

    private static transient Log log = LogFactory.getLog(MegadPort.class);

    private final int address;

    public MegadPort(int address) {
        this.address = address;
    }

    protected String makeRequest(String username, HttpUriRequest req) throws IOException, MegadApiMallformedResponseException {
        StringBuilder stb = new StringBuilder("Running megad request. User: ").append(username).append(" Address: ").append(req.getURI());
        BasicCookieStore cookieStore = new BasicCookieStore();

        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .setConnectionTimeToLive(1, TimeUnit.MINUTES)
                .build();

        String body = "";

        try (CloseableHttpResponse response = httpclient.execute(req)) {
            HttpEntity entity = response.getEntity();
            InputStream is;
            if ((is = entity.getContent()) != null) {
                body = IOUtils.toString(is, "UTF-8");
                if (Strings.isNullOrEmpty(body)) {
                    throw new MegadApiMallformedResponseException("Empty response string is received while connecting to : " + req.getURI());
                }
                return body;
            } else {
                throw new MegadApiMallformedResponseException("Empty response is received while connecting to : " + req.getURI());
            }
        } finally {
            stb.append(" Response: ");
            if (body.length() > 10) {
                stb.append(body.substring(0, 10));
            } else {
                stb.append(body);
            }
            log.info(stb.toString());
        }
    }

    public int getAddress() {
        return address;
    }

}
