package de.smarthome.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Optional;

public class Function {

    private final String name;
    private final String ID;
    private final String channelType;
    private final String functionType;
    private final List<Datapoint> datapoints;

    public Function(@JsonProperty("displayName") String name,
                    @JsonProperty("uid") String ID,
                    @JsonProperty("channelType") String channelType,
                    @JsonProperty("functionType") String functionType,
                    @JsonProperty("dataPoints") List<Datapoint> datapoints) {
        this.name = name;
        this.ID = ID;
        this.channelType = channelType;
        this.functionType = functionType;
        this.datapoints = datapoints;
    }

    public String getName() {
        return name;
    }

    public String getID() {
        return ID;
    }

    public List<Datapoint> getDataPoints() {
        return datapoints;
    }

    public String getChannelType() {
        return channelType;
    }

    public String getFunctionType() {
        return functionType;
    }

    public Optional<Datapoint> getCorrespondingDataPoint(String id){
        for(Datapoint datapoint: getDataPoints()){
            if(datapoint.getID().equals(id)) {
                return Optional.of(datapoint);
            }
        }
        return Optional.empty();
    }

    public boolean isStatusFunction() {
        return name.toLowerCase().endsWith("_status");
    }

    @Override
    public String toString() {
        return "Function{" +
                "\nname='" + name + '\'' +
                "\n, ID='" + ID + '\'' +
                "\n, channelType='" + channelType + '\'' +
                "\n, functionType='" + functionType + '\'' +
                "\n, datapoints=" + datapoints +
                "\n}";
    }
}
