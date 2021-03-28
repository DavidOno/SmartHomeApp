package de.smarthome.beacons;

import java.util.Optional;

import de.smarthome.model.impl.Location;

public interface BeaconObserver {
    Location getCurrentLocation();

    void updateLocation(Optional<Location> currentLocation);
}
