package technology.positivehome.ihome.server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import technology.positivehome.ihome.server.processor.AdminProcessor;


@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);

    private final AdminProcessor adminProcessor;
    private PasswordEncoder encoder;

    @Autowired
    public AdminController(AdminProcessor adminProcessor,
                           PasswordEncoder encoder) {
        this.adminProcessor = adminProcessor;
        this.encoder = encoder;
    }

//
//    @PreAuthorize("isAuthenticated()")
//    @PostMapping()
//    public AdminEntryContainer getSuperAdminList(
//            @RequestBody AdminFilter filter) {
//        return adminProcessor.getSuperAdminList(filter);
//    }


}
