package de.smarthome.beacons;

import android.annotation.SuppressLint;
import android.content.Context;

public class BeaconMonitoringActivity {
    protected static final String TAG = "MonitoringActivity";
    private static final int PERMISSION_REQUEST_FINE_LOCATION = 1;
    private static final int PERMISSION_REQUEST_BACKGROUND_LOCATION = 2;
    private Context context;
    private BeaconHandler application;
    private BeaconRangingActivity rangingActivity;

    public BeaconMonitoringActivity(Context context, BeaconHandler application) {
        System.out.println(">>>> MONITORING CREATED");

        this.context = context;
        this.application = application;
    }

    public void startRanging() {
        rangingActivity = new BeaconRangingActivity(context);
        rangingActivity.onResume();

        System.out.println(">>>> RANGING STARTED");
    }

    public void stopRanging() {
        rangingActivity.onPause();
    }

    @SuppressLint("SetTextI18n")
    public void onEnableClicked() {
//        MainApplication application = (MainApplication) context;
//        if (BeaconManager.getInstanceForApplication(context).getMonitoredRegions().size() > 0) {
//            application.disableMonitoring();
//        }
//        else {
            application.enableMonitoring();
//        }
    }

    public void onResume() {
        System.out.println(">>>> RESUME MONITORING");
//        MainApplication application = (MainApplication) context;
        application.setMonitoringActivity(this);
    }

    public void onPause() {
        application.setMonitoringActivity(null);
    }
}

