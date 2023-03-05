package technology.positivehome.ihome.server.service.core;

import technology.positivehome.ihome.domain.runtime.controller.ControllerConfigEntry;
import technology.positivehome.ihome.domain.runtime.controller.ControllerPortConfigEntry;
import technology.positivehome.ihome.server.persistence.model.ControllerConfigEntity;
import technology.positivehome.ihome.server.persistence.model.ControllerPortConfigEntity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by maxim on 3/4/23.
 **/
public class ControllerConfigMapper {
    public static ControllerConfigEntry from(ControllerConfigEntity configEntity, List<ControllerPortConfigEntity> portConfigEntities) {
        return new ControllerConfigEntry(
                configEntity.id(),
                configEntity.type(),
                configEntity.name(),
                configEntity.ipAddr(),
                configEntity.port(), portConfigEntities.stream().map(portConfigEntity -> new ControllerPortConfigEntry(
                        portConfigEntity.id(),
                        portConfigEntity.type(),
                        portConfigEntity.portAdress(),
                        portConfigEntity.description()))
                .collect(Collectors.toList())
        );
    }
}
