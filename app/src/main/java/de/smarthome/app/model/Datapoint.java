package de.smarthome.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

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


    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Datapoint)) return false;
        Datapoint datapoint = (Datapoint) o;
        boolean result = this.ID.equals(datapoint.ID)
                && this.name.equals(datapoint.name);
        if(value != null && datapoint.value != null) {
            result = result && this.value.equals(datapoint.value);
        }
        return result;
    }
}
