package de.smarthome.beacons;

import android.content.Context;
import android.util.Log;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

public class BeaconApplication implements BootstrapNotifier {
    private static final String TAG = "BeaconApplication";
    private RegionBootstrap regionBootstrap;
    private BackgroundPowerSaver backgroundPowerSaver;
    private Context context;

    public BeaconApplication(Context context) {
        this.context = context;
    }

    public void onCreate() {
        BeaconManager beaconManager = BeaconManager.getInstanceForApplication(context);

        beaconManager.getBeaconParsers().clear();
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

        BeaconManager.setDebug(true);

        Log.d(TAG, "setting up background monitoring for beacons and power saving");
        Region region = new Region("backgroundRegion",
                null, null, null);
        regionBootstrap = new RegionBootstrap(this, region);
        backgroundPowerSaver = new BackgroundPowerSaver(context);
    }

    public void disableMonitoring() {
        Log.d(TAG, "disabling monitoring");
        if (regionBootstrap != null) {
            regionBootstrap.disable();
            regionBootstrap = null;
        }
    }
    public void enableMonitoring() {
        Log.d(TAG, "enabling monitoring");
        Region region = new Region("backgroundRegion",
                null, null, null);
        regionBootstrap = new RegionBootstrap(this, region);
    }

    @Override
    public void didEnterRegion(Region arg0) {
        Log.d(TAG, "enter beacon region");
    }

    @Override
    public void didExitRegion(Region region) {
        Log.d(TAG, "exit beacon region");
    }

    @Override
    public void didDetermineStateForRegion(int state, Region region) {}

    @Override
    public Context getApplicationContext() {
        return context;
    }
}