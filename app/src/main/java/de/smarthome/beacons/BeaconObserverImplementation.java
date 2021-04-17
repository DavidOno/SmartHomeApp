package de.smarthome.beacons;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.util.Optional;

import de.smarthome.app.model.impl.Location;
import de.smarthome.app.model.impl.UIConfig;

public class BeaconObserverImplementation implements BeaconObserver {
    private BeaconObserverSubscriber subscriber;
    private Location currentLocation = null;
    private BeaconMonitoring beaconMonitoring;

    public BeaconObserverImplementation(Application application, Context context, UIConfig newUIConfig, BeaconLocations newBeaconConfig) {
        BeaconApplication beaconApplication = new BeaconApplication(application);
        beaconApplication.onCreate();

        beaconMonitoring = new BeaconMonitoring(context, beaconApplication, newUIConfig, newBeaconConfig);
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
        if(newLocation.isPresent()){
            if(!newLocation.get().equals(currentLocation)){
                currentLocation = newLocation.get();
                notifySubscriber();
            }
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
