package technology.positivehome.ihome.server.service.core.controller.input;

import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.domain.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.domain.runtime.sensor.Dht21TempHumiditySensorData;

import java.io.IOException;

/**
 * Created by maxim on 9/12/17.
 **/
public class EmulatedDht21TempHumiditySensor implements Dht21TempHumiditySensor {
    @Override
    public Dht21TempHumiditySensorData getData() throws PortNotSupporttedFunctionException, IOException, MegadApiMallformedResponseException, MegadApiMallformedUrlException {
        return ResultMapper.dht21TempHumiditySensorData("<a href=/sec/?cf=3>Back</a><br>P2@" +
                "<br>temp:25.90" +
                "<br>hum:81.10<form action=/sec/>" +
                "<input type=hidden name=pn value=2@>Type " +
                "<select name=pty><option value=255>NC<option value=0>In<option value=1>Out" +
                "<option value=3 selected>DSen<option value=4>I2C<option value=2>ADC</select>" +
                "<br>Sen <select name=d><option value=1>DHT11<option value=2 selected>DHT22<option value=3>1W" +
                "<option value=5>1WBUS<option value=4>iB<option value=6>W26</select>" +
                "<br><input type=submit value=Save></form>");
    }
}
