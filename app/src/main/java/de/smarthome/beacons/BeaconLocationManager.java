package de.smarthome.beacons;

import android.content.Context;

import java.io.File;
import java.io.FileWriter;
import java.util.Map;
import java.util.Optional;

import de.smarthome.app.model.Location;
import de.smarthome.app.model.UIConfig;
import de.smarthome.beacons.nearest.RetrievingStrategy;

public class BeaconLocationManager {
    private static final String TAG = "BeaconLocationManager";
    private final UIConfig uiConfig;
    private final BeaconLocations locationConfig;
    private BeaconObserver beaconObserver;
    private final RetrievingStrategy retrievingStrategy;

    public BeaconLocationManager(UIConfig newUIConfig, BeaconLocations newBeaconConfig, RetrievingStrategy retrievingStrategy) {
        this.uiConfig = newUIConfig;
        this.locationConfig = newBeaconConfig;
        this.retrievingStrategy = retrievingStrategy;
    }

    /**
     *
     * @param updatedBeaconSignals
     */
    void updateCurrentLocation(Map<BeaconID, Integer> updatedBeaconSignals) {
        BeaconID nearestBeacon = retrievingStrategy.getNearest(updatedBeaconSignals);

        if(nearestBeacon != null) {
            Optional<Location> currentLocation = getLocation(nearestBeacon);
            beaconObserver.updateLocation(currentLocation);
        }
    }

    public Optional<Location> getLocation(BeaconID nearestBeacon) {
        return uiConfig.getLocation(locationConfig.getRoomUID(nearestBeacon));
    }

    public void setObserver(BeaconObserverImplementation beaconObserverImplementation) {
        this.beaconObserver = beaconObserverImplementation;
    }
}
