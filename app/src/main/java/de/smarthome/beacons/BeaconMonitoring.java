package de.smarthome.beacons;

import android.annotation.SuppressLint;
import android.content.Context;
import org.altbeacon.beacon.BeaconManager;

import de.smarthome.app.model.UIConfig;

public class BeaconMonitoring {
    private Context context;
    private BeaconRanging ranging;
    private BeaconApplication application;

    public BeaconMonitoring(Context context, BeaconApplication application, UIConfig newUIConfig, BeaconLocations newBeaconConfig) {
        this.context = context;
        this.ranging = new BeaconRanging(context, newUIConfig, newBeaconConfig);
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

    public void setObserver(BeaconObserverImplementation beaconObserverImplementation) {
        ranging.setObserver(beaconObserverImplementation);
    }
}
