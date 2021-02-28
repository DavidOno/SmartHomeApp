package de.smarthome.beacons;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.EditText;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import de.smarthome.R;

public class BeaconRangingActivity implements BeaconConsumer {
    protected static final String TAG = "RangingActivity";
    private final BeaconManager beaconManager;
    private BeaconLocationManager beaconLocationManager;
    private Context context;

    BeaconRangingActivity(Context context) {
        this.context = context;

        beaconManager = BeaconManager.getInstanceForApplication(context);

        ObjectMapper m = new ObjectMapper();
        String jsonString = "{\n" +
                "\t\"locations\": [\n" +
                "\t\t{\n" +
                "\t\t\t\"roomUID\": \"aaabc\",\n" +
                "\t\t\t\"beaconId\": \"abc\"\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"roomUID\": \"aacads\",\n" +
                "\t\t\t\"beaconId\": \"xyz\"\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"roomUID\": \"fdasdf\",\n" +
                "\t\t\t\"beaconId\": \"qwert\"\n" +
                "\t\t}\n" +
                "\t]\n" +
                "}";
        try {
            Locations locations = m.readValue(jsonString, new TypeReference<Locations>() {});
            beaconLocationManager = new BeaconLocationManager(locations);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void onPause() {
        beaconManager.unbind(this);
    }

    protected void onResume() {
        beaconManager.bind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        RangeNotifier rangeNotifier = (beacons, region) -> {
            System.out.println(">>>> SERVICE CONNECT");
            if (!beacons.isEmpty()) {
                Log.d(TAG, "didRangeBeaconsInRegion called with beacon count:  " + beacons.size());
//                Log.d("RANGING>> ", Arrays.toString(beacons.toArray(new Beacon[]{})));

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

