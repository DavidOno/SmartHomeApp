package de.smarthome.beacons;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;

import org.altbeacon.beacon.BeaconManager;

public class BeaconMonitoring {
    private Context context;
    private BeaconRanging ranging;
    private BeaconApplication application;

    public BeaconMonitoring(Context context, BeaconApplication application) {
        this.context = context;
        this.ranging = new BeaconRanging(context);
        this.application = application;

//        if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                if (context.checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    if (!context.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
//                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                        builder.setTitle("This app needs background location access");
//                        builder.setMessage("Please grant location access so this app can detect beacons in the background.");
//                        builder.setPositiveButton(android.R.string.ok, null);
//                        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
//
//                            @TargetApi(23)
//                            @Override
//                            public void onDismiss(DialogInterface dialog) {
//                                requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
//                                        PERMISSION_REQUEST_BACKGROUND_LOCATION);
//                            }
//
//                        });
//                        builder.show();
//                    } else {
//                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                        builder.setTitle("Functionality limited");
//                        builder.setMessage("Since background location access has not been granted, this app will not be able to discover beacons in the background.  Please go to Settings -> Applications -> Permissions and grant background location access to this app.");
//                        builder.setPositiveButton(android.R.string.ok, null);
//                        builder.setOnDismissListener(dialog -> {
//                        });
//                        builder.show();
//                    }
//                }
//            }
//        } else {
//            if (!context.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
//                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
//                                Manifest.permission.ACCESS_BACKGROUND_LOCATION},
//                        PERMISSION_REQUEST_FINE_LOCATION);
//            } else {
//                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                builder.setTitle("Functionality limited");
//                builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons.  Please go to Settings -> Applications -> Permissions and grant location access to this app.");
//                builder.setPositiveButton(android.R.string.ok, null);
//                builder.setOnDismissListener(dialog -> {
//                });
//                builder.show();
//            }
//        }
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
