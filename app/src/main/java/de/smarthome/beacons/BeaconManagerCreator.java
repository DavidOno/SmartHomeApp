package de.smarthome.beacons;

import android.content.Context;

import org.altbeacon.beacon.BeaconManager;

/**
 * Specifies how to create a beacon manager.
 */
public interface BeaconManagerCreator {
    /**
     * Creates beacon manager
     * @param context Android context
     * @return Beacon manager
     */
    BeaconManager create(Context context);
}
