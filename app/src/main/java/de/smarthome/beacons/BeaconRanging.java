package de.smarthome.beacons;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.service.RunningAverageRssiFilter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.smarthome.app.model.UIConfig;
import de.smarthome.beacons.nearest.HistoryBestStrategy;

/**
 * This class enables the app to detect movement in-and-out of regions.
 */
public class BeaconRanging implements BeaconConsumer {
    private final BeaconManager beaconManager;
    private final BeaconLocationManager beaconLocationManager;
    private final Context context;
    private final BeaconLocations beaconConfig;

    public BeaconRanging(Context context, UIConfig newUIConfig, BeaconLocations newBeaconConfig,
                         BeaconManagerCreator beaconManagerCreator) {
        this.context = context;
        BeaconManager.setRssiFilterImplClass(RunningAverageRssiFilter.class);
        RunningAverageRssiFilter.setSampleExpirationMilliseconds(5000);
        beaconManager = beaconManagerCreator.create(context);
        beaconLocationManager = new BeaconLocationManager(newUIConfig, newBeaconConfig, new HistoryBestStrategy());
        this.beaconConfig = newBeaconConfig;
    }

    /**
     * Binds beacon manager
     */
    public void bind() {
        beaconManager.bind(this);
    }

    /**
     * Unbinds beacon manager
     */
    public void unbind() {
        beaconManager.unbind(this);
    }

    /**
     * Specifies range notifier which sends updates to the BeaconLocationManager
     */
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
                filterUnregisteredBeacons(beaconsOverview);
                beaconLocationManager.updateCurrentLocation(beaconsOverview);
            }
        };

        beaconManager.startRangingBeacons(new Region("myRangingUniqueId", null, null, null));
        beaconManager.addRangeNotifier(rangeNotifier);
        beaconManager.startRangingBeacons(new Region("myRangingUniqueId", null, null, null));
        beaconManager.addRangeNotifier(rangeNotifier);
    }

    private void filterUnregisteredBeacons(Map<BeaconID, Integer> beaconsOverview) {
        Iterator<BeaconID> iter = beaconsOverview.keySet().iterator();
        while (iter.hasNext()) {
            BeaconID beaconID = iter.next();
            if(!beaconConfig.isRegistered(beaconID)){
                iter.remove();
            }
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public Context getApplicationContext() {
        return context;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void unbindService(ServiceConnection serviceConnection) {
        context.unbindService(serviceConnection);
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return context.bindService(intent, serviceConnection, i);
    }

    public void setObserver(BeaconObserverImplementation beaconObserverImplementation) {
        beaconLocationManager.setObserver(beaconObserverImplementation);
    }
}
