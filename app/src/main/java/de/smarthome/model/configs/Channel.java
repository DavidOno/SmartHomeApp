package de.smarthome.model.configs;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


public class Channel {
    private final String channelID;
    private final List<ChannelDatapoint> datapoints;

    public Channel(@JsonProperty("channel") String channelID, @JsonProperty("datapoints") List<ChannelDatapoint> datapoints) {
        this.channelID = channelID;
        this.datapoints = datapoints;
    }

    public String getChannelID() {
        return channelID;
    }

    public List<ChannelDatapoint> getDatapoints() {
        return datapoints;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "channelID='" + channelID + '\'' +
                ", datapoints=" + datapoints +
                '}';
    }
}
