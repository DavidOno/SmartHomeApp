package de.smarthome.model;

import java.util.List;

public interface Function {

    String getName();
    String getID();
    List<Datapoint> getDataPoints();
}
