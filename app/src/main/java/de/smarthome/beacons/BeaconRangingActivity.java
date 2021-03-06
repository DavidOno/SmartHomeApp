package de.smarthome.beacons;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.EditText;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import de.smarthome.R;

public class BeaconRangingActivity extends Activity implements BeaconConsumer {
    protected static final String TAG = "RangingActivity";
    private final BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
    private BeaconLocationManager beaconLocationManager = new BeaconLocationManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranging);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        beaconManager.unbind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        beaconManager.bind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        RangeNotifier rangeNotifier = (beacons, region) -> {
            if (!beacons.isEmpty()) {
                Log.d(TAG, "didRangeBeaconsInRegion called with beacon count:  " + beacons.size());
                Log.d("RANGING>> ", Arrays.toString(beacons.toArray(new Beacon[]{})));

                Beacon[] beaconsArray = beacons.toArray(new Beacon[]{});
                Map<BeaconID, Integer> beaconsOverview = new HashMap<>();

                for (Beacon b : beaconsArray) {
                    logToDisplay( b.toString() + " has freq. " + b.getRssi());
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

    private void logToDisplay(final String line) {
        runOnUiThread(() -> {
            EditText editText = BeaconRangingActivity.this.findViewById(R.id.rangingText);
            editText.append(line+"\n");
        });
    }
}
