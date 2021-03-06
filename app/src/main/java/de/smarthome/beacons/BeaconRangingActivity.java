package de.smarthome.beacons;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.RemoteException;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BeaconRangingActivity implements BeaconConsumer {
    private final BeaconManager beaconManager;
    private BeaconLocationManager beaconLocationManager = new BeaconLocationManager();
    private Context context;

    public BeaconRangingActivity(Context context) {
        this.context = context;
        beaconManager = BeaconManager.getInstanceForApplication(context);
    }

    public void onPause() {
        beaconManager.unbind(this);
    }

    public void onResume() {
        beaconManager.bind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        RangeNotifier rangeNotifier = (beacons, region) -> {
            if (!beacons.isEmpty()) {
                System.out.println("didRangeBeaconsInRegion called with beacon count:  " + beacons.size());
                System.out.println(">>>> RANGING " + Arrays.toString(beacons.toArray(new Beacon[]{})));

                Beacon[] beaconsArray = beacons.toArray(new Beacon[]{});
                Map<BeaconID, Integer> beaconsOverview = new HashMap<>();

                for (Beacon b : beaconsArray) {
                    beaconsOverview.put(new BeaconID(b.getId1(), b.getId2(), b.getId3()), b.getRssi());
                }
                beaconLocationManager.addNewBeaconStatus(beaconsOverview);

                //TODO: Get Location
            }
        };

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
            beaconManager.addRangeNotifier(rangeNotifier);
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
            beaconManager.addRangeNotifier(rangeNotifier);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Context getApplicationContext() {
        return context;
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {
        context.unbindService(serviceConnection);
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return context.bindService(intent, serviceConnection, i);
    }
}
