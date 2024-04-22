package technology.positivehome.ihome.server.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import technology.positivehome.ihome.domain.constant.ErrorMessageLogSortRule;
import technology.positivehome.ihome.domain.runtime.module.ErrorMessageLogEntryInfo;
import technology.positivehome.ihome.server.processor.AdminProcessor;
import technology.positivehome.ihome.server.processor.ErrorMessageLogService;

import java.util.List;


@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final ErrorMessageLogService errorMessageLogService;


    @Autowired
    public AdminController(AdminProcessor adminProcessor,
                           PasswordEncoder encoder, ErrorMessageLogService errorMessageLogService) {
        this.errorMessageLogService = errorMessageLogService;
    }
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/errors", method = {RequestMethod.HEAD})
    public ResponseEntity<Void> errorCount(@RequestParam(required = false) List<String> filter) {
        HttpHeaders headers = new HttpHeaders();

        long count = errorMessageLogService.countMessages(FilterMapper.fromErrorMessageFilter(filter));
        headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "X-Total-Count, X-Offset");
        headers.add("x-total-count", String.valueOf(count));
        return ResponseEntity.ok()
                .headers(headers)
                .body(null);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/errors")
    public List<ErrorMessageLogEntryInfo> searchErrors(@RequestParam(required = false) List<String> filter,
                                                       @RequestParam(required = false, defaultValue = "0") @Min(0) Integer page,
                                                       @RequestParam(required = false, defaultValue = "100") @Min(1) @Max(100) Integer size,
                                                       @RequestParam(required = false) List<ErrorMessageLogSortRule> sort) {
        return errorMessageLogService.searchMessages(FilterMapper.fromErrorMessageFilter(filter), page, size, sort);
    }

}
