package de.smarthome.beacons;


import android.content.Context;
import org.altbeacon.beacon.BeaconManager;

import de.smarthome.app.model.UIConfig;

/**
 * This class allows the app to scan the region for bluetooth beacons.
 */
public class BeaconMonitoring {
    private final Context context;
    private final BeaconRanging ranging;
    private final BeaconApplication application;

    /**
     *
     * @param context
     * @param application
     * @param newUIConfig
     * @param newBeaconConfig
     * @param beaconManagerCreator
     */
    public BeaconMonitoring(Context context, BeaconApplication application, UIConfig newUIConfig,
                            BeaconLocations newBeaconConfig, BeaconManagerCreator beaconManagerCreator) {
        this.context = context;
        this.ranging = new BeaconRanging(context, newUIConfig, newBeaconConfig, beaconManagerCreator);
        this.application = application;
    }

    /**
     * Enables the app to scan the region for beacons
     */
    public void startMonitoring() {
        ranging.onResume();
    }

    /**
     * Disables the ability to scan the region for beacons.
     * Calling the method up again cancels the deactivation.
     */
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
