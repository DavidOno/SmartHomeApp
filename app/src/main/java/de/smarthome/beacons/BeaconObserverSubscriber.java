package de.smarthome.beacons;

import de.smarthome.app.model.Location;

public interface BeaconObserverSubscriber {
    void update(Location newLocation);
}
