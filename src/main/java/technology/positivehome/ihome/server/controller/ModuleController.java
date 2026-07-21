package technology.positivehome.ihome.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import technology.positivehome.ihome.model.runtime.exception.MegadApiMallformedResponseException;
import technology.positivehome.ihome.model.runtime.exception.MegadApiMallformedUrlException;
import technology.positivehome.ihome.model.runtime.exception.PortNotSupporttedFunctionException;
import technology.positivehome.ihome.model.runtime.module.ModuleEntry;
import technology.positivehome.ihome.model.runtime.module.ModuleSummary;
import technology.positivehome.ihome.model.runtime.module.ModuleUpdateRequest;
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

    @PreAuthorize("hasPermission(@ObjectIdFactory.rootListObjReq('module_list'), 'read')")
    @GetMapping
    public ModuleSummary[] moduleList(@RequestParam(required = false) Integer assignment, @RequestParam(required = false) Long group) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException {
        return systemProcessor.getModuleList(assignment, group);
    }

    @PreAuthorize("hasPermission(@ObjectIdFactory.moduleIdReq(#moduleId), 'read')")
    @GetMapping(path = "/{moduleId}")
    public ModuleEntry getModuleData(@PathVariable long moduleId) throws MegadApiMallformedUrlException, URISyntaxException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, InterruptedException {
        return systemProcessor.getModuleData(moduleId);
    }

    @PreAuthorize("hasPermission(@ObjectIdFactory.moduleIdReq(#moduleId), 'write')")
    @PutMapping(path = "/{moduleId}")
    public ResponseEntity<Void> updateModule(@PathVariable long moduleId, @RequestBody ModuleUpdateRequest moduleUpdateRequest) throws MegadApiMallformedUrlException, PortNotSupporttedFunctionException, MegadApiMallformedResponseException, IOException, URISyntaxException, InterruptedException {
        systemProcessor.updateModuleProps(moduleId, moduleUpdateRequest);
        return ResponseEntity.noContent().build();
    }

}
