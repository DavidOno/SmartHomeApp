package de.smarthome.command;

import de.smarthome.beacons.BeaconLocations;
import de.smarthome.app.model.configs.BoundariesConfig;
import de.smarthome.app.model.configs.ChannelConfig;

public enum AdditionalConfigs {

    LOCATION("location_config", BeaconLocations.class),
    CHANNEL("channel_config", ChannelConfig.class),
    BOUNDARIES("boundaries_config", BoundariesConfig.class);

    private final String resource;
    private final Class correspondingPOJO;

    AdditionalConfigs(String resource, Class correspondingPOJO){
        this.resource = resource;
        this.correspondingPOJO = correspondingPOJO;
    }

    public String getResource() {
        return resource;
    }

    public Class getCorrespondingPOJO() {
        return correspondingPOJO;
    }
}
