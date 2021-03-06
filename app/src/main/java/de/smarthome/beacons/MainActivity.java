package de.smarthome.beacons;

import android.app.Application;
import android.util.Log;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

public class MainActivity extends Application implements BootstrapNotifier {
    private static final String TAG = "BeaconReferenceApp";
    private RegionBootstrap regionBootstrap;
    private BackgroundPowerSaver backgroundPowerSaver;
    private BeaconMonitoringActivity monitoringActivity = null;
    private String cumulativeLog = "";

    @Override
    public void onCreate() {
        super.onCreate();
        BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);

        System.out.println(">>>> APPLICATION CREATED");

        beaconManager.getBeaconParsers().clear();
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

        BeaconManager.setDebug(true);

        Log.d(TAG, "setting up background monitoring for beacons and power saving");
        Region region = new Region("backgroundRegion",
                null, null, null);
        regionBootstrap = new RegionBootstrap(this, region);
        backgroundPowerSaver = new BackgroundPowerSaver(this);
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
    public void didEnterRegion(Region arg0) {
        // TODO: To comment
        System.out.println(">>>> DID ENTER REGION");
//        Log.d(TAG, "did enter region.");
//        Log.d(TAG, "Sending notification.");
        //sendNotification();
        if (monitoringActivity != null) {
            logToDisplay("I see a beacon again" );
        }
    }

    @Override
    public void didExitRegion(Region region) {
        // TODO: To comment
        logToDisplay("Beacon left Region");
    }

    @Override
    public void didDetermineStateForRegion(int state, Region region) {
        // TODO: To comment
        logToDisplay("Current region state is: " + (state == 1 ? "INSIDE" : "OUTSIDE ("+state+")"));
    }

//    private void sendNotification() {
//        NotificationManager notificationManager =
//                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
//        Notification.Builder builder;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel("Beacon Reference Notifications",
//                    "Beacon Reference Notifications", NotificationManager.IMPORTANCE_HIGH);
//            channel.enableLights(true);
//            channel.enableVibration(true);
//            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
//            notificationManager.createNotificationChannel(channel);
//            builder = new Notification.Builder(this, channel.getId());
//        }
//        else {
//            builder = new Notification.Builder(this);
//            builder.setPriority(Notification.PRIORITY_HIGH);
//        }
//
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//        stackBuilder.addNextIntent(new Intent(this, MonitoringActivity.class));
//        PendingIntent resultPendingIntent =
//                stackBuilder.getPendingIntent(
//                        0,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                );
//
//        // Show message on log screen
//        builder.setSmallIcon(R.drawable.ic_launcher);
//        builder.setContentTitle("I detect a beacon");
//        builder.setContentText("Tap here to see details in the reference app");
//        builder.setContentIntent(resultPendingIntent);
//        notificationManager.notify(1, builder.build());
//    }

    public void setMonitoringActivity(BeaconMonitoringActivity activity) {
        System.out.println(">>>> SET MONITORING");

        this.monitoringActivity = activity;
    }

    private void logToDisplay(String line) {
        // TODO: To comment
        cumulativeLog += (line + "\n");
        if (this.monitoringActivity != null) {
            this.monitoringActivity.updateLog(cumulativeLog);
        }
    }

    public String getLog() {
        return cumulativeLog;
    }
}