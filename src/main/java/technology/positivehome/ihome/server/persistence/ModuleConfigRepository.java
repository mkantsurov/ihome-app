package technology.positivehome.ihome.server.persistence;

import technology.positivehome.ihome.domain.constant.ModuleOperationMode;
import technology.positivehome.ihome.domain.runtime.module.ModuleConfigElementEntry;
import technology.positivehome.ihome.domain.runtime.module.ModuleConfigEntry;
import technology.positivehome.ihome.domain.runtime.module.ModuleSettings;

import java.util.List;


/**
 * Created by maxim on 6/25/19.
 **/
public interface ModuleConfigRepository {

    List<ModuleConfigEntry> loadModuleConfig();

    ModuleConfigEntry updateModuleMode(long moduleId, ModuleOperationMode newMode);

    List<ModuleConfigEntry> addNewModule(ModuleSettings moduleSettings);

    ModuleConfigEntry updateModuleProperties(ModuleSettings moduleConfigProperties);

    ModuleConfigElementEntry updateModuleConfigElement(ModuleConfigElementEntry value);

}
