package de.smarthome.beacons;

import android.content.Context;

import org.altbeacon.beacon.BeaconManager;

public interface BeaconManagerCreator {
    BeaconManager create(Context context);
}
