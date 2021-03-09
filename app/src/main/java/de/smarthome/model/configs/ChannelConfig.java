package de.smarthome.model.configs;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ChannelConfig {
    private final List<Channel> channels;

    public ChannelConfig(@JsonProperty("channels") List<Channel> channels) {
        this.channels = channels;
    }

    public List<Channel> getChannels() {
        return channels;
    }
}
