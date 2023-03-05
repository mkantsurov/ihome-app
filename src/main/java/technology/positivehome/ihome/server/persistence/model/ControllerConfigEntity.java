package technology.positivehome.ihome.server.persistence.model;

import technology.positivehome.ihome.domain.constant.ControllerType;

/**
 * Created by maxim on 2/25/23.
 **/
public record ControllerConfigEntity(long id, ControllerType type, String name, String ipAddr, int port) {}
