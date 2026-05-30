package technology.positivehome.ihome.server.persistence.repository;

import technology.positivehome.ihome.server.persistence.model.ControllerPortConfigEntity;

import java.util.List;

/**
 * Created by maxim on 2/26/23.
 **/
public interface ControllerPortConfigRepository extends GenericIHomeRepository<ControllerPortConfigEntity, Long> {
    List<ControllerPortConfigEntity> findByControllerId(long id);
}
