package technology.positivehome.ihome.server.persistence.model;

import technology.positivehome.ihome.domain.constant.IHomePortType;

/**
 * Created by maxim on 6/27/19.
 **/
public record ControllerPortConfigEntity(long id, long controllerId, IHomePortType type, int portAdress, String description) {}
