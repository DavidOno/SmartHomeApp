package de.smarthome.app.model.configs;

import de.smarthome.beacons.BeaconLocations;
import de.smarthome.app.model.configs.BoundariesConfig;
import de.smarthome.app.model.configs.ChannelConfig;

/**
 * This enum lists all possible additional configs (beside ui-config).
 * Normally all additional configs reside on the callbackserver.
 */
public enum AdditionalConfig {

    LOCATION("location_config", BeaconLocations.class),
    CHANNEL("channel_config", ChannelConfig.class),
    BOUNDARIES("boundaries_config", BoundariesConfig.class);

    private final String resource;
    private final Class correspondingPOJO;

    AdditionalConfig(String resource, Class correspondingPOJO){
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
