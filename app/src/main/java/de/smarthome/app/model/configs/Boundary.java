package de.smarthome.app.model.configs;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Boundary {
    private final String boundaryName;
    private final String loaction;
    private final List<BoundaryDataPoint> datapoints;

    public Boundary(@JsonProperty("boundary") String boundaryName, @JsonProperty("location") String location, @JsonProperty("datapoints") List<BoundaryDataPoint> datapoints) {
        this.boundaryName = boundaryName;
        this.loaction = location;
        this.datapoints = datapoints;
    }

    public String getName() {
        return boundaryName;
    }

    public String getLoaction() {
        return loaction;
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
