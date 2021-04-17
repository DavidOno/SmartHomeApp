package de.smarthome.beacons;

import de.smarthome.app.model.impl.Location;

public interface BeaconObserverSubscriber {
    void update(Location newLocation);
}
