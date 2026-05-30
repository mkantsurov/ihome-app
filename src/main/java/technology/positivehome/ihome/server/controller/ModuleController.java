package technology.positivehome.ihome.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.domain.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.domain.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.domain.runtime.module.ModuleEntry;
import technology.positivehome.ihome.domain.runtime.module.ModuleSummary;
import technology.positivehome.ihome.domain.runtime.module.ModuleUpdateRequest;
import technology.positivehome.ihome.server.processor.StatisticProcessor;
import technology.positivehome.ihome.server.processor.SystemProcessor;

import java.io.IOException;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/v1/modules")
public class ModuleController {

    private final SystemProcessor systemProcessor;
    private final StatisticProcessor statisticProcessor;

    @Autowired
    public ModuleController(SystemProcessor systemProcessor, StatisticProcessor statisticProcessor) {
        this.systemProcessor = systemProcessor;
        this.statisticProcessor = statisticProcessor;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ModuleSummary[] moduleList(@RequestParam(required = false) Integer assignment, @RequestParam(required = false) Long group) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException {
        return systemProcessor.getModuleList(assignment, group);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(path = "/{moduleId}")
    public ModuleEntry getModuleData(@PathVariable long moduleId) throws MegadApiMallformedUrlException, URISyntaxException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException {
        return systemProcessor.getModuleData(moduleId);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping(path = "/{moduleId}")
    public ResponseEntity<Void> updateModule(@PathVariable long moduleId, @RequestBody ModuleUpdateRequest moduleUpdateRequest) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, URISyntaxException, InterruptedException {
        systemProcessor.updateModuleProps(moduleId, moduleUpdateRequest);
        return ResponseEntity.noContent().build();
    }

}
