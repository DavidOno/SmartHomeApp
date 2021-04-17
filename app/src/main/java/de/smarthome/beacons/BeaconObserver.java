package de.smarthome.beacons;

import java.util.Optional;

import de.smarthome.app.model.Location;

public interface BeaconObserver {
    Location getCurrentLocation();

    void updateLocation(Optional<Location> currentLocation);
}
