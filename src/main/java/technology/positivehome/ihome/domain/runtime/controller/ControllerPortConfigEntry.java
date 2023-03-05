package technology.positivehome.ihome.domain.runtime.controller;

import technology.positivehome.ihome.domain.constant.IHomePortType;

/**
 * Created by maxim on 6/27/19.
 **/
public record ControllerPortConfigEntry(long id, IHomePortType type, int portAddress, String description) {}
