package de.smarthome.beacons;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import org.altbeacon.beacon.BeaconManager;

import de.smarthome.R;

public class BeaconMonitoringActivity extends Activity {
    private BeaconRangingActivity ranging = new BeaconRangingActivity(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println(">>>> MONITORING CREATED");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring);
    }

    public void onRangingClicked(View view) {
        ranging.onResume();
    }

    @SuppressLint("SetTextI18n")
    public void onEnableClicked(View view) {
        MainActivity application = ((MainActivity) this.getApplicationContext());
        if (BeaconManager.getInstanceForApplication(this).getMonitoredRegions().size() > 0) {
            application.disableMonitoring();
            ranging.onPause();
        }
        else {
            application.enableMonitoring();
        }
    }

    @Override
    public void onResume() {
        System.out.println(">>>> RESUME MONITORING");

        super.onResume();
        MainActivity application = ((MainActivity) this.getApplicationContext());
        application.setMonitoringActivity(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) this.getApplicationContext()).setMonitoringActivity(null);
    }

    public void updateLog(final String log) {
        runOnUiThread(() -> {
            EditText editText = (EditText) BeaconMonitoringActivity.this
                    .findViewById(R.id.monitoringText);
            editText.setText(log);
        });
    }
}
