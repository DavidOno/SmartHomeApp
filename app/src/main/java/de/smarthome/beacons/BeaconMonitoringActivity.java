package de.smarthome.beacons;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import org.altbeacon.beacon.BeaconManager;

public class BeaconMonitoringActivity {
    private Context context;
    private BeaconRangingActivity ranging;
    private Application application;

    public BeaconMonitoringActivity(Context context, Application application) {
        this.context = context;
        this.ranging = new BeaconRangingActivity(context);
        this.application = application;
    }

    public void startMonitoring() {
        ranging.onResume();
    }

    @SuppressLint("SetTextI18n")
    public void stopMonitoring() {
        if (BeaconManager.getInstanceForApplication(context).getMonitoredRegions().size() > 0) {
            ((BeaconApplication)application).disableMonitoring();
            ranging.onPause();
        }
        else {
            ((BeaconApplication)application).enableMonitoring();
        }
    }

    public void onResume() {
        System.out.println(">>>> RESUME MONITORING");

        ((BeaconApplication)application).setMonitoringActivity(this);
    }
}
