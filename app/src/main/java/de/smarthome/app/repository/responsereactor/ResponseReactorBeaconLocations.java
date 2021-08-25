package de.smarthome.app.repository.responsereactor;

import android.util.Log;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.smarthome.app.repository.Repository;
import de.smarthome.beacons.BeaconLocations;
import de.smarthome.command.ResponseReactor;

/**
 * Commandchainreactor to handle requests for the beaconconfig send via a commandchain
 */
public class ResponseReactorBeaconLocations implements ResponseReactor {
    private static final String TAG = "ResponseReactorBeaconLocations";
    private final Repository repository;

    public ResponseReactorBeaconLocations() {
        repository = Repository.getInstance();
    }

    @Override
    public void react(ResponseEntity responseEntity) {
        try {
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                Log.d(TAG, "response received BeaconConfig");
                Log.d(TAG, responseEntity.getBody().toString());

                BeaconLocations responseBeaconLocations = (BeaconLocations) responseEntity.getBody();
                sendBeaconLocationsToRepo(responseBeaconLocations);

                repository.setServerConnectionEvent(ServerConnectionEvent.CALLBACK_CONNECTION_SUCCESS);
                Log.d(TAG, "BeaconConfig successfully retrieved.\nStatus: " + responseEntity.getStatusCode());
            } else {
                Log.d(TAG, "error occurred");
                Log.d(TAG, responseEntity.getStatusCode().toString());
                repository.setServerConnectionEvent(ServerConnectionEvent.CALLBACK_CONNECTION_FAIL);
                Log.d(TAG, "Problem when trying retrieve BeaconConfig.\nStatus: " + responseEntity.getStatusCode());
            }
        }catch(Exception e){
            Log.d(TAG, "Exception: " + e.toString());
            repository.setServerConnectionEvent(ServerConnectionEvent.CALLBACK_CONNECTION_FAIL);
            e.printStackTrace();
        }
    }

    private void sendBeaconLocationsToRepo(BeaconLocations beaconLocations){
        repository.initBeaconObserverWithBeaconLocations(beaconLocations);
    }
}