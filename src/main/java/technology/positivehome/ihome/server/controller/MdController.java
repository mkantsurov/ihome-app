package technology.positivehome.ihome.server.controller;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import technology.positivehome.ihome.server.service.core.ControllerEventListener;
import technology.positivehome.ihome.server.service.core.controller.ControllerEventInfo;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by maxim on 8/1/19.
 **/
@RestController
@RequestMapping("/wsmd/listener")
/**
 * Request params:
 *     undefined,
 *     cmd,
 *     pt,
 *     m,
 *     cnt,
 *     click,
 *     pwm
 */
public class MdController {

    private final ControllerEventListener controllerEventListener;

    @Autowired
    public MdController(ControllerEventListener controllerEventListener) {
        this.controllerEventListener = controllerEventListener;
    }

    @GetMapping()
    public void onMdRequest(HttpServletRequest request,
                            @RequestParam(required = false) String pt,
                            @RequestParam(required = false) String m,
                            @RequestParam(required = false) String cnt,
                            @RequestParam(required = false) String click) {

        controllerEventListener.getControllerIdByAddress(request.getRemoteAddr()).ifPresent(controllerId -> {
            if (controllerEventListener.isControllerExists(controllerId)) {
                if (!Strings.isNullOrEmpty(pt)) {
                    controllerEventListener.onControllerEvent(ControllerEventInfo.builder()
                            .sourceId(controllerId)
                            .port(pt)
                            .mode(m)
                            .count(cnt)
                            .click(click)
                            .build());
                }
            }
        });
    }
}
