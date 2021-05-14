package de.smarthome.command.impl;

import android.util.Log;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.smarthome.app.model.configs.BoundariesConfig;
import de.smarthome.app.repository.ConfigContainer;
import de.smarthome.command.ResponseReactor;

public class ResponseReactorBoundariesConfig implements ResponseReactor {
    private final String TAG = "ResponseReactorBoundariesConfig";
    private BoundariesConfig responseBoundariesConfig;
    private ConfigContainer configContainer;

    public ResponseReactorBoundariesConfig() {
        this.configContainer = ConfigContainer.getInstance();
    }
    @Override
    public void react(ResponseEntity responseEntity) {
        try {
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                System.out.println("response received UIConfig");
                System.out.println(responseEntity.getBody());

                responseBoundariesConfig = (BoundariesConfig) responseEntity.getBody();
                sendBoundariesConfigToRepo(responseBoundariesConfig);

                Log.d(TAG, "BoundaryConfig successfully retrieved.\nStatus: " + responseEntity.getStatusCode());
            } else {
                System.out.println("error occurred");
                System.out.println(responseEntity.getStatusCode());
                Log.d(TAG, "Problem when trying get BoundaryConfig.\nStatus: " + responseEntity.getStatusCode());
            }
        }catch(Exception e){
            Log.d(TAG, "Exception: " + e.toString());
        }
    }

    public BoundariesConfig getResponseBoundariesConfig() {
        return responseBoundariesConfig;
    }

    public void sendBoundariesConfigToRepo(BoundariesConfig newBoundariesConfig){
        configContainer.setBoundaryConfig(newBoundariesConfig);
    }
}