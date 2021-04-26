package de.smarthome.app.model.configs;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class BoundariesConfig {
    private final List<Boundary> boundaries;

    public BoundariesConfig(@JsonProperty("boundaries") List<Boundary> boundaries) {
        this.boundaries = boundaries;
    }

    public List<Boundary> getBoundaries() {
        return boundaries;
    }

    @Override
    public String toString() {
        return "BoundariesConfig{" +
                "boundaries=" + boundaries +
                '}';
    }
}
