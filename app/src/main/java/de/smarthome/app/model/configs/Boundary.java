package de.smarthome.app.model.configs;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Boundary {
    private final String boundaryName;
    private final List<BoundaryDataPoint> datapoints;

    public Boundary(@JsonProperty("boundary") String boundaryName, @JsonProperty("datapoints") List<BoundaryDataPoint> datapoints) {
        this.boundaryName = boundaryName;
        this.datapoints = datapoints;
    }

    public String getName() {
        return boundaryName;
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
