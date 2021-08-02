package de.smarthome.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;
/**
 * Model class for the datapoints, stored inside functions, inside ui-config.
 * These represent current data, like temperature, light-intensity in the users home.
 */
public class Datapoint {

    private final String ID;
    private final String name;
    private Object value;

    public Datapoint(@JsonProperty("uid") String ID,
                     @JsonProperty("name") String name) {
        this.ID = ID;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getID() {
        return ID;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Datapoint{" +
                "\nID='" + ID + '\'' +
                "\n, name='" + name + '\'' +
                "\n}";
    }
}
