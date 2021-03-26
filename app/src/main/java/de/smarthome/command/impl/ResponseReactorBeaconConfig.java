package de.smarthome.command.impl;

import android.util.Log;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.smarthome.model.impl.UIConfig;
import de.smarthome.model.repository.Repository;

public class ResponseReactorBeaconConfig implements ResponseReactor{
    private final String TAG = "ResponseReactorBeaconConfig";
    private UIConfig responseUIConfig;
    private Repository parentRepository;

    public ResponseReactorBeaconConfig(Repository parentRepository) {
        this.parentRepository = parentRepository;
    }

    @Override
    public void react(ResponseEntity responseEntity) {
        try {
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                System.out.println("response received UIConfig");
                System.out.println(responseEntity.getBody());

                responseUIConfig = (UIConfig) responseEntity.getBody();
                sendUIConfigToRepo(responseUIConfig);

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

    public UIConfig getResponseUIConfig() {
        return responseUIConfig;
    }

    public void sendUIConfigToRepo(UIConfig newUIConfig){
        parentRepository.setUIConfig(newUIConfig);
    }
}
