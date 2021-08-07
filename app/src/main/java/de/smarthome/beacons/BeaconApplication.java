package de.smarthome.beacons;

import android.content.Context;
import android.util.Log;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

/**
 * Sets up region and scan patterns for beacon monitoring.
 */
public class BeaconApplication implements BootstrapNotifier {
    private static final String TAG = "BeaconApplication";
    private final BeaconManagerCreator beaconManagerCreator;
    private RegionBootstrap regionBootstrap;
    private final Context context;

    public BeaconApplication(Context context, BeaconManagerCreator beaconManagerCreator) {
        this.context = context;
        this.beaconManagerCreator = beaconManagerCreator;
    }

    /**
     * Sets up the signal layout for parsing incoming beacon signals.
     * Initializing the scanned region.
     */
    public void setUpScan() {
        BeaconManager beaconManager = beaconManagerCreator.create(context);

        beaconManager.getBeaconParsers().clear();
        // BeaconLayout for iBeacons
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

        beaconManager.setEnableScheduledScanJobs(false);
        beaconManager.setBackgroundBetweenScanPeriod(0);
        beaconManager.setBackgroundScanPeriod(1100);

        BeaconManager.setDebug(true);

        Log.d(TAG, "setting up background monitoring for beacons and power saving");
        Region region = new Region("backgroundRegion",
                null, null, null);
        regionBootstrap = new RegionBootstrap(this, region);
        BackgroundPowerSaver backgroundPowerSaver = new BackgroundPowerSaver(context);
    }

    /**
     * When beacon monitoring should be disabled and regionsBootstrap isn't null
     * the regionsBootstrap will be disabled and regionsBootstrap is set null.
     */
    public void disableMonitoring() {
        Log.d(TAG, "disabling monitoring");
        if (regionBootstrap != null) {
            regionBootstrap.disable();
            regionBootstrap = null;
        }
    }

    /**
     * When beacon monitoring should be enabled new region is created and regionsBootstrap declared.
     */
    public void enableMonitoring() {
        Log.d(TAG, "enabling monitoring");
        Region region = new Region("backgroundRegion",
                null, null, null);
        regionBootstrap = new RegionBootstrap(this, region);
    }

    /**
     * @inheritDoc
     * Because of the observer pattern didEnterRegion isn't used.
     */
    @Override
    public void didEnterRegion(Region arg0) {
        Log.d(TAG, "enter beacon region");
    }

    /**
     * @inheritDoc
     * Because of the observer pattern didExitRegion isn't used.
     */
    @Override
    public void didExitRegion(Region region) {
        Log.d(TAG, "exit beacon region");
    }

    /**
     * @inheritDoc
     * Because of the observer pattern didDetermineStateForRegion isn't used.
     */
    @Override
    public void didDetermineStateForRegion(int state, Region region) {/* This method is currently not called*/}

    /**
     * @inheritDoc
     */
    @Override
    public Context getApplicationContext() {
        return context;
    }
}