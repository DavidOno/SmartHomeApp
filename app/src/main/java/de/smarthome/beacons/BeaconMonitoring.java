package de.smarthome.beacons;

import android.annotation.SuppressLint;
import android.content.Context;

import org.altbeacon.beacon.BeaconManager;

public class BeaconMonitoring {
    private Context context;
    private BeaconRanging ranging;
    private BeaconApplication application;

    public BeaconMonitoring(Context context, BeaconApplication application) {
        this.context = context;
        this.ranging = new BeaconRanging(context);
        this.application = application;
    }

    public void startMonitoring() {
        ranging.onResume();
    }

    @SuppressLint("SetTextI18n")
    public void stopMonitoring() {
        if (BeaconManager.getInstanceForApplication(context).getMonitoredRegions().size() > 0) {
            application.disableMonitoring();
            ranging.onPause();
        }
        else {
            application.enableMonitoring();
        }
    }
}
