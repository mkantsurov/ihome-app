package technology.positivehome.ihome.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import technology.positivehome.ihome.domain.runtime.*;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.domain.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.domain.runtime.module.ModuleStateData;
import technology.positivehome.ihome.domain.runtime.module.ModuleSummary;
import technology.positivehome.ihome.server.processor.StatisticProcessor;
import technology.positivehome.ihome.server.processor.SystemProcessor;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;


@RestController
@RequestMapping("/api/v1/system")
public class SystemController {

    private final SystemProcessor systemProcessor;
    private final StatisticProcessor statisticProcessor;

    @Autowired
    public SystemController(SystemProcessor systemProcessor, StatisticProcessor statisticProcessor) {
        this.systemProcessor = systemProcessor;
        this.statisticProcessor = statisticProcessor;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(path = "/summary")
    public SystemSummaryInfo getSummary() throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException {
        return systemProcessor.getSummaryInfo();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(path = "/tempstat")
    public TempStat getTempStat() {
        return statisticProcessor.getTempStat();
    }

    @GetMapping(path = "/pressure-stat")
    public PressureStat getPressureStat() {
        return statisticProcessor.getPressureStat();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(path = "/luminositystat")
    public LuminosityStat getLuminosityStat() {
        return statisticProcessor.getLuminosityStat();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(path = "/systemstat")
    public SystemStat getSystemStat() {
        return statisticProcessor.getSystemStat();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(path = "/lastat")
    public LaStat getLaStat() {
        return statisticProcessor.getLaStat();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(path = "/boiler-temp-stat")
    public BoilerTempStat getBoilerTempStatStat() {
        return statisticProcessor.getBoilerTempStat();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(path = "/modulelist/{group}")
    public List<ModuleSummary> getModuleList(@PathVariable long group) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException {
        return systemProcessor.getModuleListByGroup(group);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(path = "/moduledata/{moduleId}")
    public ModuleStateData getModuleData(@PathVariable long moduleId) throws MegadApiMallformedUrlException, URISyntaxException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException {
        return systemProcessor.getModuleData(moduleId);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping(path = "/modulemode/{moduleId}")
    public ModuleSummary updateModuleMode(@PathVariable long moduleId, @RequestBody int moduleMode) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, URISyntaxException, InterruptedException {
        return systemProcessor.updateModuleMode(moduleId, moduleMode);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping(path = "/moduleoutput/{moduleId}")
    public ModuleSummary updateModuleOutputState(@PathVariable long moduleId, @RequestBody int outputStatus) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, URISyntaxException, InterruptedException {
        return systemProcessor.updateModuleOutputState(moduleId, outputStatus);
    }


}
