package de.smarthome.beacons;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.sql.SQLOutput;

public class BeaconHandler implements BootstrapNotifier {
    private static final String TAG = "BeaconReferenceApp";
    private RegionBootstrap regionBootstrap;
    private BackgroundPowerSaver backgroundPowerSaver;
    private BeaconMonitoringActivity monitoringActivity = null;
    private Context context;

    public BeaconHandler(Context context) {
        this.context = context;

        BeaconManager beaconManager = BeaconManager.getInstanceForApplication(context);

        System.out.println(">>>> APPLICATION CREATED");

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
        System.out.println(">>>> DISABLE MONITORING");

        if (regionBootstrap != null) {
            regionBootstrap.disable();
            regionBootstrap = null;
        }
    }
    public void enableMonitoring() {
        System.out.println(">>>> ENABLE MONITORING");

        Region region = new Region("backgroundRegion",
                null, null, null);
        regionBootstrap = new RegionBootstrap(this, region);
    }

    @Override
    public void didEnterRegion(Region arg0) { }

    @Override
    public void didExitRegion(Region region) { }

    @Override
    public void didDetermineStateForRegion(int state, Region region) { }


    public void setMonitoringActivity(BeaconMonitoringActivity activity) {
        System.out.println(">>>> SET MONITORING");

        this.monitoringActivity = activity;
    }

    @Override
    public Context getApplicationContext() {
        return context;
    }

    public void start() {
        BeaconMonitoringActivity monitoring = new BeaconMonitoringActivity(context, this);
        monitoring.onResume();
        monitoring.startRanging();
    }
}
