package technology.positivehome.ihome.security.permissionproc.web;

import org.springframework.stereotype.Component;
import technology.positivehome.ihome.security.model.permissionproc.ModuleId;

@Component
class ObjectIdFactory {
    public ModuleId moduleIdUpdateReq(Long moduleId) {
        return new ModuleId(moduleId);
    }
}
