package de.smarthome.beacons;

import java.util.Optional;

import de.smarthome.app.model.Location;

/**
 * This interface allows to share the information received from the BeaconApplication.
 */
public interface BeaconObserver {
    /**
     * Specifies how to get the current location to verify the new information.
     * @return The current location which is saved in the application.
     */
    Location getCurrentLocation();

    /**
     * Specifies how to use the information from the given input, containing changed values.
     * @param currentLocation Input from the current location defined by the BeaconLocationManager.
     *                        Datatype is optional because the value may be null.
     */
    void updateLocation(Optional<Location> currentLocation);
}
