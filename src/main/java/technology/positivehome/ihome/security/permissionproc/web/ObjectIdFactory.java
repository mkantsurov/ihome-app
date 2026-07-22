package technology.positivehome.ihome.security.permissionproc.web;

import org.springframework.stereotype.Component;
import technology.positivehome.ihome.model.runtime.module.ModuleUpdateRequest;
import technology.positivehome.ihome.security.model.permissionproc.ModuleId;
import technology.positivehome.ihome.security.model.permissionproc.MooduleUpdateRequest;
import technology.positivehome.ihome.security.model.permissionproc.RootListObj;
import technology.positivehome.ihome.security.model.permissionproc.TargetType;

@Component("ObjectIdFactory")
class ObjectIdFactory {

    public RootListObj rootListObjReq(String targetType) {
        return new RootListObj(TargetType.of(targetType));
    }

    public ModuleId moduleIdReq(Long moduleId) {
        return new ModuleId(moduleId);
    }

    public MooduleUpdateRequest moduleUpdateReq(Long moduleId, ModuleUpdateRequest req) {
        return new MooduleUpdateRequest(moduleId, req.enabledOnStartup(), req.moduleActive(), req.outputValue());
    }

}
