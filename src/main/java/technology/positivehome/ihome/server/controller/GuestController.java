package technology.positivehome.ihome.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import technology.positivehome.ihome.domain.constant.BinaryPortStatus;
import technology.positivehome.ihome.domain.runtime.ExternalPowerSummaryInfo;
import technology.positivehome.ihome.domain.runtime.PowerSummaryInfo;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.domain.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.domain.shared.OutDoorTempStatInfo;
import technology.positivehome.ihome.domain.shared.PowerVoltageExtStatInfo;
import technology.positivehome.ihome.domain.shared.PressureStatInfo;
import technology.positivehome.ihome.server.model.command.IHomeCommandFactory;
import technology.positivehome.ihome.server.processor.StatisticProcessor;
import technology.positivehome.ihome.server.processor.SystemProcessor;

import java.io.IOException;

import static technology.positivehome.ihome.server.service.core.module.GenericInputPowerDependentRelayPowerControlModule.POWER_METER_PORT_ID;


@RestController
@RequestMapping("/guest-api/v1/stats")
public class GuestController {

    private final StatisticProcessor statisticProcessor;
    private final SystemProcessor systemProcessor;

    @Autowired
    public GuestController(StatisticProcessor statisticProcessor, SystemProcessor systemProcessor) {
        this.statisticProcessor = statisticProcessor;
        this.systemProcessor = systemProcessor;
    }

    @GetMapping(path = "/outdoor-temp-stat")
    public OutDoorTempStatInfo getOutdoorTempStat() {
        return statisticProcessor.getTemperatureStat();
    }

    @GetMapping(path = "/pressure-stat")
    public PressureStatInfo getPressureStat() {
        return statisticProcessor.getPressureStat();
    }

    @GetMapping(path = "/power-stat")
    public PowerVoltageExtStatInfo getPowerStat() {
        return statisticProcessor.getPowerVoltageExtStat();
    }

    @GetMapping(path = "/power-summary")
    public ExternalPowerSummaryInfo getPowerSummaryInfo() throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException {
        return systemProcessor.getExtPowerSummaryInfo();
   }

}
