package technology.positivehome.ihome.security.permissionproc.web;

import org.springframework.stereotype.Component;
import technology.positivehome.ihome.security.model.permissionproc.ModuleId;
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

}
