package technology.positivehome.ihome.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import technology.positivehome.ihome.domain.shared.OutDoorTempStatInfo;
import technology.positivehome.ihome.domain.shared.PowerStatInfo;
import technology.positivehome.ihome.domain.shared.PressureStatInfo;
import technology.positivehome.ihome.server.processor.StatisticProcessor;


@RestController
@RequestMapping("/guest-api/v1/stats")
public class GuestController {

    private final StatisticProcessor statisticProcessor;

    @Autowired
    public GuestController(StatisticProcessor statisticProcessor) {
        this.statisticProcessor = statisticProcessor;
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
    public PowerStatInfo getPowerStat() {
        return statisticProcessor.getPowerStat();
    }

}
