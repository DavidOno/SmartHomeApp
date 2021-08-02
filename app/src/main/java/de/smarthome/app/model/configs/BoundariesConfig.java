package de.smarthome.app.model.configs;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * This class holds all boundaries from the callbackserver.
 * Boundaries specify some limits for specific datapoints in certain locations.
 * Most importantly they affect the range of options presented in the ui.
 */
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
