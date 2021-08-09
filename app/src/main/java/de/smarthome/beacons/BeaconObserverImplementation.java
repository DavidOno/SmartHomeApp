package de.smarthome.beacons;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.util.Optional;

import de.smarthome.app.model.Location;
import de.smarthome.app.model.UIConfig;

/**
 * Implements the observer design pattern specified by the interface BeaconObserver
 */
public class BeaconObserverImplementation implements BeaconObserver {
    private BeaconObserverSubscriber subscriber;
    private Location currentLocation = null;
    private final BeaconMonitoring beaconMonitoring;

    /**
     * Creates object and sets up beacon scan.
     */
    public BeaconObserverImplementation(Application application, Context context,
                                        UIConfig newUIConfig, BeaconLocations newBeaconConfig,
                                        BeaconManagerCreator beaconManagerCreator) {
        BeaconApplication beaconApplication = new BeaconApplication(application, beaconManagerCreator);
        beaconApplication.setUpScan();

        beaconMonitoring = new BeaconMonitoring(context, beaconApplication, newUIConfig,
                newBeaconConfig, beaconManagerCreator);
    }

    /**
     * Additional initialization which has to be called after the constructor
     */
    public void init() {
        beaconMonitoring.setObserver(this);
    }

    /**
     * @inheritDoc
     */
    @Override
    public Location getCurrentLocation() {
        return currentLocation;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void updateLocation(Optional<Location> newLocation) {
        if(newLocation.isPresent() && !newLocation.get().equals(currentLocation)){
            currentLocation = newLocation.get();
            notifySubscriber();
        }

    }

    /**
     * Subscribes BeaconObserverSubscriber and starts monitoring
     */
    public void subscribe(BeaconObserverSubscriber subscriber) {
        this.subscriber = subscriber;
        Log.d("Observer", "subscription");
        beaconMonitoring.startMonitoring();
    }

    /**
     * Unsubscribes BeaconObserverSubscriber and stops monitoring
     */
    public void unsubscribe() {
        this.subscriber = null;
        Log.d("Observer", "unsubscribe");
        beaconMonitoring.stopMonitoring();
    }

    private void notifySubscriber() {
        subscriber.update(currentLocation);
    }
}
