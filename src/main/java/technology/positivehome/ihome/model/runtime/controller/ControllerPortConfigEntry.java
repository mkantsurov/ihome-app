package technology.positivehome.ihome.model.runtime.controller;

import technology.positivehome.ihome.model.constant.IHomePortType;

/**
 * Created by maxim on 6/27/19.
 **/
public record ControllerPortConfigEntry(long id, IHomePortType type, int portAddress, String description) {}
