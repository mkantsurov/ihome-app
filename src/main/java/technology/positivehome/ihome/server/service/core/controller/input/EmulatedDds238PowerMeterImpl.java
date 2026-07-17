package technology.positivehome.ihome.server.service.core.controller.input;

import technology.positivehome.ihome.model.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.model.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.model.runtime.sensor.Dds238PowerMeterData;
import technology.positivehome.ihome.server.service.core.controller.DR404Port;
import technology.positivehome.ihome.server.service.core.controller.DR404RequestExecutor;

import java.io.IOException;

public class EmulatedDds238PowerMeterImpl extends DR404Port implements Dds238PowerMeter {

    private static final double EMULATED_VOLTAGE = 220.0;
    private static final double EMULATED_CURRENT = 4.0;
    private static final double EMULATED_FREQUENCY = 50.0;
    private static final double EMULATED_TOTAL = 50000.0;

    public EmulatedDds238PowerMeterImpl(DR404RequestExecutor requestExecutor, int port) {
        super(requestExecutor, port);
    }

    @Override
    public Dds238PowerMeterData getData() throws PortNotSupporttedFunctionException, IOException, MegadApiMallformedUrlException, InterruptedException {
        return Dds238PowerMeterData.builder()
                .voltage(EMULATED_VOLTAGE)
                .current(EMULATED_CURRENT)
                .freq(EMULATED_FREQUENCY)
                .total(EMULATED_TOTAL)
                .build();
    }
}
