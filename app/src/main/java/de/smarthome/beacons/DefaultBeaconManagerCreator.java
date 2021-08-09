package de.smarthome.beacons;

import android.content.Context;

import org.altbeacon.beacon.BeaconManager;

/**
 * Implements BeaconManagerCreator
 */
public class DefaultBeaconManagerCreator implements BeaconManagerCreator {
    /**
     * @inheritDoc
     */
    @Override
    public BeaconManager create(Context context) {
        return BeaconManager.getInstanceForApplication(context);
    }
}
