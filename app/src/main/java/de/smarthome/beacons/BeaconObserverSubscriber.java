package de.smarthome.beacons;

import de.smarthome.app.model.Location;

/**
 * This interface allows to share the information received from the BeaconApplication
 * with the user subscribed to the GIRA-server.
 */
public interface BeaconObserverSubscriber {

    /**
     * Specifies how to use the information from the given input, containing changed values.
     * @param newLocation Input from to updated location.
     */
    void update(Location newLocation);
}
