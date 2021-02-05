package technology.positivehome.ihome.server.persistence;

import technology.positivehome.ihome.domain.runtime.controller.*;

import java.util.List;


/**
 * Created by maxim on 6/25/19.
 **/
public interface ControllerConfigRepository {

    List<ControllerConfigEntry> loadControllerConfig();

    ControllerConfigEntry updateControllerProps(ControllerProperties value);

    ControllerPortConfigEntry updateControllerPort(ControllerPortConfigEntry value);

    List<ControllerPortConfigEntry> addNewControllerPort(AppendControllerPortConfigEntryRequest value);

    List<ControllerPortConfigEntry> removeControllerPort(RemoveControllerPortRequest value);

}
