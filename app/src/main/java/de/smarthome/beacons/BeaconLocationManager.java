package de.smarthome.beacons;

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
        System.out.println("UPDATE:"+updatedBeaconSignals);
        BeaconID nearestBeacon = retrievingStrategy.getNearest(updatedBeaconSignals);
        if(nearestBeacon != null) {
            System.out.println("NEARESTBEACON::: " + nearestBeacon.toString());
            Optional<Location> currentLocation = getLocation(nearestBeacon);
            beaconObserver.updateLocation(currentLocation);
        }else{
            System.out.println("NEARESTBEACON::: no nearest beacon found");
        }
    }

    public Optional<Location> getLocation(BeaconID nearestBeacon) {
        return uiConfig.getLocation(locationConfig.getRoomUID(nearestBeacon));
    }

    public void setObserver(BeaconObserverImplementation beaconObserverImplementation) {
        this.beaconObserver = beaconObserverImplementation;
    }

}
