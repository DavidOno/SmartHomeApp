package de.smarthome.model.impl;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import de.smarthome.model.Datapoint;
import de.smarthome.model.Function;
import de.smarthome.model.MetaInformation;

public class FunctionImpl implements Function, MetaInformation {

    private final String name;
    private final String ID;
    private final String channelType;
    private final String functionType;

    public FunctionImpl(@JsonProperty("displayName") String name,
                        @JsonProperty("uid") String ID,
                        @JsonProperty("channelType") String channelType,
                        @JsonProperty("functionType") String functionType) {
        this.name = name;
        this.ID = ID;
        this.channelType = channelType;
        this.functionType = functionType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public List<Datapoint> getDataPoints() {
        return null;
    }

    @Override
    public String getChannelType() {
        return channelType;
    }

    @Override
    public String getFunctionType() {
        return functionType;
    }
}
