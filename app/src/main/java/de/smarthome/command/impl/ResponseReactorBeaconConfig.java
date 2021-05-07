package de.smarthome.command.impl;

import android.util.Log;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.smarthome.app.repository.ConfigContainer;
import de.smarthome.beacons.BeaconLocations;
import de.smarthome.command.ResponseReactor;

public class ResponseReactorBeaconConfig implements ResponseReactor {
    private final String TAG = "ResponseReactorBeaconConfig";
    private BeaconLocations responseBeaconConfig;
    private ConfigContainer configContainer;

    public ResponseReactorBeaconConfig() {
        this.configContainer = ConfigContainer.getInstance();
    }

    @Override
    public void react(ResponseEntity responseEntity) {
        try {
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                System.out.println("response received BeaconConfig");
                System.out.println(responseEntity.getBody());

                responseBeaconConfig = (BeaconLocations) responseEntity.getBody();
                sendBeaconLocationsToRepo(responseBeaconConfig);

                Log.d(TAG, "Communication with Server possible.\nStatus: " + responseEntity.getStatusCode());
            } else {
                System.out.println("error occurred");
                System.out.println(responseEntity.getStatusCode());

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
        configContainer.setBeaconConfig(newBeaconConfig);
    }
}
