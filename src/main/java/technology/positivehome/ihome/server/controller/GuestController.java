package technology.positivehome.ihome.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import technology.positivehome.ihome.domain.runtime.OutDoorTempStat;
import technology.positivehome.ihome.domain.runtime.PowerStat;
import technology.positivehome.ihome.domain.runtime.PressureStat;
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
    public OutDoorTempStat getOutdoorTempStat() {
        return statisticProcessor.getTemperatureStat();
    }

    @GetMapping(path = "/pressure-stat")
    public PressureStat getPressureStat() {
        return statisticProcessor.getPressureStat();
    }

    @GetMapping(path = "/power-stat")
    public PowerStat getPowerStat() {
        return statisticProcessor.getPowerStat();
    }

}
