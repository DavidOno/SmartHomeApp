package de.smarthome.app.repository.responsereactor;

import android.util.Log;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.smarthome.app.repository.ConfigContainer;
import de.smarthome.app.repository.Repository;
import de.smarthome.beacons.BeaconLocations;
import de.smarthome.command.ResponseReactor;

public class ResponseReactorBeaconConfig implements ResponseReactor {
    private static final String TAG = "ResponseReactorBeaconConfig";
    private ConfigContainer configContainer;
    private Repository repository;

    public ResponseReactorBeaconConfig() {
        configContainer = ConfigContainer.getInstance();
        repository = Repository.getInstance(null);
    }

    @Override
    public void react(ResponseEntity responseEntity) {
        try {
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                Log.d(TAG, "response received BeaconConfig");
                Log.d(TAG, responseEntity.getBody().toString());

                BeaconLocations responseBeaconConfig = (BeaconLocations) responseEntity.getBody();
                sendBeaconLocationsToRepo(responseBeaconConfig);

                Log.d(TAG, "BeaconConfig successfully retrieved.\nStatus: " + responseEntity.getStatusCode());
            } else {
                Log.d(TAG, "error occurred");
                Log.d(TAG, responseEntity.getStatusCode().toString());

                Log.d(TAG, "Problem when trying retrieve BeaconConfig.\nStatus: " + responseEntity.getStatusCode());
            }
        }catch(Exception e){
            Log.d(TAG, "Exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public void sendBeaconLocationsToRepo(BeaconLocations newBeaconConfig){
        configContainer.setBeaconLocations(newBeaconConfig);
        repository.initBeaconObserver();
    }
}