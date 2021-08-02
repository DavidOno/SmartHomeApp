package de.smarthome.app.model.configs;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * This class represents the boundaries, which are specified for some datapoints in certain locations.
 */
public class Boundary {
    private final String boundaryName;
    private final String location;
    private final List<BoundaryDataPoint> datapoints;

    public Boundary(@JsonProperty("boundary") String boundaryName, @JsonProperty("location") String location, @JsonProperty("datapoints") List<BoundaryDataPoint> datapoints) {
        this.boundaryName = boundaryName;
        this.location = location;
        this.datapoints = datapoints;
    }

    public String getName() {
        return boundaryName;
    }

    public String getLocation() {
        return location;
    }

    public List<BoundaryDataPoint> getDatapoints() {
        return datapoints;
    }

    @Override
    public String toString() {
        return "Boundary{" +
                "boundaryName='" + boundaryName + '\'' +
                ", datapoints=" + datapoints +
                '}';
    }
}