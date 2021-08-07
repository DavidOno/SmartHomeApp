package de.smarthome.beacons;

import java.util.Map;
import java.util.Optional;

import de.smarthome.app.model.Location;
import de.smarthome.app.model.UIConfig;
import de.smarthome.beacons.nearest.NearestBeaconStrategy;

public class BeaconLocationManager {
    private static final String TAG = "BeaconLocationManager";
    private final UIConfig uiConfig;
    private final BeaconLocations locationConfig;
    private BeaconObserver beaconObserver;
    private final NearestBeaconStrategy nearestBeaconStrategy;

    public BeaconLocationManager(UIConfig newUIConfig, BeaconLocations newBeaconConfig, NearestBeaconStrategy nearestBeaconStrategy) {
        this.uiConfig = newUIConfig;
        this.locationConfig = newBeaconConfig;
        this.nearestBeaconStrategy = nearestBeaconStrategy;
    }

    /**
     *
     * @param updatedBeaconSignals
     */
    void updateCurrentLocation(Map<BeaconID, Integer> updatedBeaconSignals) {
        BeaconID nearestBeacon = nearestBeaconStrategy.getNearest(updatedBeaconSignals);

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
