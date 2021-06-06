package de.smarthome.beacons;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.RemoteException;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.HashMap;
import java.util.Map;

import de.smarthome.app.model.UIConfig;

public class BeaconRanging implements BeaconConsumer {
    private final BeaconManager beaconManager;
    private BeaconLocationManager beaconLocationManager;
    private Context context;

    public BeaconRanging(Context context, UIConfig newUIConfig, BeaconLocations newBeaconConfig,
                         BeaconManagerCreator beaconManagerCreator) {
        this.context = context;
        beaconManager = beaconManagerCreator.create(context);
        beaconLocationManager = new BeaconLocationManager(newUIConfig, newBeaconConfig);
    }

    public void onResume() {
        beaconManager.bind(this);
    }

    public void onPause() {
        beaconManager.unbind(this);
    }

    public void onDestroy() {
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        RangeNotifier rangeNotifier = (beacons, region) -> {
            if (!beacons.isEmpty()) {
                Log.d("BeaconRanging","called with beacon count: " + beacons.size());

                Beacon[] beaconsArray = beacons.toArray(new Beacon[]{});
                Map<BeaconID, Integer> beaconsOverview = new HashMap<>();

                for (Beacon b : beaconsArray) {
                    beaconsOverview.put(new BeaconID(b.getId1(), b.getId2(), b.getId3()), b.getRssi());
                }
                beaconLocationManager.addNewBeaconStatus(beaconsOverview);
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

    public void setObserver(BeaconObserverImplementation beaconObserverImplementation) {
        beaconLocationManager.setObserver(beaconObserverImplementation);
    }
}
