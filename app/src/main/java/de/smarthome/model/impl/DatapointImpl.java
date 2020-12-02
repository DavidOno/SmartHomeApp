package de.smarthome.model.impl;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.smarthome.model.Datapoint;

public class DatapointImpl implements Datapoint {

    private final String ID;
    private final String name;

    public DatapointImpl(@JsonProperty("uid") String ID,
                         @JsonProperty("name") String name) {
        this.ID = ID;
        this.name = name;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getID() {
        return null;
    }
}
