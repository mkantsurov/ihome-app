package technology.positivehome.ihome.server.persistence.repository;

import technology.positivehome.ihome.server.persistence.model.ControllerConfigEntity;

import java.util.List;

/**
 * Created by maxim on 2/26/23.
 **/
public interface ControllerConfigRepository extends GenericIHomeRepository<ControllerConfigEntity, Long> {
    List<ControllerConfigEntity> findAll();
}
