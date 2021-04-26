package de.smarthome.app.model.configs;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BoundaryDataPoint {
    private final String name;
    private final String min;
    private final String max;


    public BoundaryDataPoint(@JsonProperty("name") String name, @JsonProperty("min") String min, @JsonProperty("max") String max) {
        this.name = name;
        this.min = min;
        this.max = max;
    }

    public String getName() {
        return name;
    }

    public String getMin() {
        return min;
    }

    public String getMax() {
        return max;
    }

    @Override
    public String toString() {
        return "BoundaryDataPoint{" +
                "name='" + name + '\'' +
                ", min='" + min + '\'' +
                ", max='" + max + '\'' +
                '}';
    }
}
