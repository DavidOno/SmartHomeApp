package de.smarthome.beacons;

import android.annotation.SuppressLint;
import android.content.Context;
import org.altbeacon.beacon.BeaconManager;

import de.smarthome.app.model.UIConfig;

public class BeaconMonitoring {
    private final Context context;
    private final BeaconRanging ranging;
    private final BeaconApplication application;

    public BeaconMonitoring(Context context, BeaconApplication application, UIConfig newUIConfig,
                            BeaconLocations newBeaconConfig, BeaconManagerCreator beaconManagerCreator) {
        this.context = context;
        this.ranging = new BeaconRanging(context, newUIConfig, newBeaconConfig, beaconManagerCreator);
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
