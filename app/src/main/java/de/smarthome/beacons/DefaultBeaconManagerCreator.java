package de.smarthome.beacons;

import android.content.Context;

import org.altbeacon.beacon.BeaconManager;

public class DefaultBeaconManagerCreator implements BeaconManagerCreator {
    @Override
    public BeaconManager create(Context context) {
        return BeaconManager.getInstanceForApplication(context);
    }
}
