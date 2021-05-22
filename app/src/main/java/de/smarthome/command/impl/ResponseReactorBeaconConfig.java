package de.smarthome.command.impl;

import android.util.Log;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.smarthome.beacons.BeaconLocations;
import de.smarthome.command.ResponseReactor;
import de.smarthome.app.repository.Repository;

public class ResponseReactorBeaconConfig implements ResponseReactor {
    private static final String TAG = "ResponseReactorBeaconConfig";
    private BeaconLocations responseBeaconConfig;
    private Repository parentRepository;

    public ResponseReactorBeaconConfig(Repository parentRepository) {
        this.parentRepository = parentRepository;
    }

    @Override
    public void react(ResponseEntity responseEntity) {
        try {
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                Log.d(TAG, "response received BeaconConfig");
                Log.d(TAG, responseEntity.getBody().toString());

                responseBeaconConfig = (BeaconLocations) responseEntity.getBody();
                sendBeaconLocationsToRepo(responseBeaconConfig);

                Log.d(TAG, "Communication with Server possible.\nStatus: " + responseEntity.getStatusCode());
            } else {
                Log.d(TAG, "error occurred");
                Log.d(TAG, responseEntity.getStatusCode().toString());

                Log.d(TAG, "Problem when trying to reach Server.\nStatus: " + responseEntity.getStatusCode());
            }
        }catch(Exception e){
            Log.d(TAG, "Exerption: " + e.toString());
        }
    }

    public BeaconLocations getResponseBeaconLocations() {
        return responseBeaconConfig;
    }

    public void sendBeaconLocationsToRepo(BeaconLocations newBeaconConfig){
        parentRepository.setBeaconConfig(newBeaconConfig);
    }
}
