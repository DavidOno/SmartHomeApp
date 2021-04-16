package de.smarthome.beacons;

import de.smarthome.model.impl.Location;

public interface BeaconObserverSubscriber {
    void update(Location newLocation);
}
