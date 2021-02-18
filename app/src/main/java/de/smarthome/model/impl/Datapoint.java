package de.smarthome.model.impl;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Datapoint {

    private final String ID;
    private final String name;

    public Datapoint(@JsonProperty("uid") String ID,
                     @JsonProperty("name") String name) {
        this.ID = ID;
        this.name = name;
    }

    public String getName() {
        return null;
    }

    public String getID() {
        return null;
    }

    @Override
    public String toString() {
        return "Datapoint{" +
                "\nID='" + ID + '\'' +
                "\n, name='" + name + '\'' +
                "\n}";
    }
}
