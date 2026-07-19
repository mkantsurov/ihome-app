package technology.positivehome.ihome.server.persistence;

import technology.positivehome.ihome.model.constant.ModuleOperationMode;
import technology.positivehome.ihome.model.constant.ModuleStartupMode;
import technology.positivehome.ihome.model.runtime.module.ModuleConfigElementEntry;
import technology.positivehome.ihome.model.runtime.module.ModuleConfigEntry;
import technology.positivehome.ihome.model.runtime.module.ModuleSettings;

import java.util.List;


/**
 * Created by maxim on 6/25/19.
 **/
public interface ModuleConfigRepository {

    List<ModuleConfigEntry> loadModuleConfig();

    ModuleConfigEntry updateModuleMode(long moduleId, ModuleOperationMode newMode);

    ModuleConfigEntry updateModuleStartupMode(long moduleId, ModuleStartupMode moduleStartupMode);

    List<ModuleConfigEntry> addNewModule(ModuleSettings moduleSettings);

    ModuleConfigEntry updateModuleProperties(ModuleSettings moduleConfigProperties);

    ModuleConfigElementEntry updateModuleConfigElement(ModuleConfigElementEntry value);

    ModuleConfigEntry getModuleConfigEntry(long moduleId);
}
