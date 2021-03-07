package de.smarthome.beacons;

import android.app.Application;
import android.content.Context;

import java.util.Optional;

import de.smarthome.model.impl.Location;

public class BeaconObserverImplementation implements BeaconObserver {
    private BeaconObserverSubscriber subscriber;
    private Location currentLocation = null;
    private BeaconMonitoring beaconMonitoring;

    public BeaconObserverImplementation(Application application, Context context) {
        BeaconApplication beaconApplication = new BeaconApplication(application);
        beaconApplication.onCreate();

        beaconMonitoring = new BeaconMonitoring(context, beaconApplication);
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
        newLocation.ifPresent(location -> currentLocation = location);
        notifySubscriber();
    }

    public void subscribe(BeaconObserverSubscriber subscriber) {
        this.subscriber = subscriber;

        beaconMonitoring.startMonitoring();
    }

    public void unsubscribe() {
        this.subscriber = null;

        beaconMonitoring.stopMonitoring();
    }

    private void notifySubscriber() {
        subscriber.update(currentLocation);
    }
}
