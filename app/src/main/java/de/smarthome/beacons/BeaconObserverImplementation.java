package de.smarthome.beacons;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.util.Optional;

import de.smarthome.app.model.Location;
import de.smarthome.app.model.UIConfig;

public class BeaconObserverImplementation implements BeaconObserver {
    private BeaconObserverSubscriber subscriber;
    private Location currentLocation = null;
    private final BeaconMonitoring beaconMonitoring;

    public BeaconObserverImplementation(Application application, Context context,
                                        UIConfig newUIConfig, BeaconLocations newBeaconConfig,
                                        BeaconManagerCreator beaconManagerCreator) {
        BeaconApplication beaconApplication = new BeaconApplication(application, beaconManagerCreator);
        beaconApplication.onCreate();

        beaconMonitoring = new BeaconMonitoring(context, beaconApplication, newUIConfig,
                newBeaconConfig, beaconManagerCreator);
    }

    public void init() {
        beaconMonitoring.setObserver(this);
    }

    @Override
    public Location getCurrentLocation() {
        return currentLocation;
    }

    @Override
    public void updateLocation(Optional<Location> newLocation) {
        if(newLocation.isPresent() && !newLocation.get().equals(currentLocation)){
            currentLocation = newLocation.get();
            notifySubscriber();
        }

    }

    public void subscribe(BeaconObserverSubscriber subscriber) {
        this.subscriber = subscriber;
        Log.d("Observer", "subscription");
        beaconMonitoring.startMonitoring();
    }

    public void unsubscribe() {
        this.subscriber = null;
        Log.d("Observer", "unsubscribe");
        beaconMonitoring.stopMonitoring();
    }

    private void notifySubscriber() {
        subscriber.update(currentLocation);
    }
}
