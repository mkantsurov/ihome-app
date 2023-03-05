package technology.positivehome.ihome.domain.runtime.controller;

import technology.positivehome.ihome.domain.constant.ControllerType;

import java.util.List;

/**
 * Created by maxim on 3/4/23.
 **/
public record ControllerConfigEntry(long id, ControllerType type, String name, String ipAddr, int port, List<ControllerPortConfigEntry> portConfig) {
}
