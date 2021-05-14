package de.smarthome.command.impl;

import android.util.Log;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.smarthome.app.repository.ConfigContainer;
import de.smarthome.command.ResponseReactor;
import de.smarthome.app.model.UIConfig;
import de.smarthome.app.utility.ToastUtility;

public class ResponseReactorUIConfig implements ResponseReactor {
    private final String TAG = "ResponseReactorUIConfig";
    private UIConfig responseUIConfig;
    private ConfigContainer configContainer;

    private ToastUtility toastUtility;

    public ResponseReactorUIConfig() {
        this.configContainer = ConfigContainer.getInstance();
        this.toastUtility = ToastUtility.getInstance();
    }

    @Override
    public void react(ResponseEntity responseEntity) {
        try {
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                System.out.println("response received UIConfig");
                System.out.println(responseEntity.getBody());

                responseUIConfig = (UIConfig) responseEntity.getBody();
                sendUIConfigToRepo(responseUIConfig);

                Log.d(TAG, "UIConfig successfully retrieved.\nStatus: " + responseEntity.getStatusCode());

            } else {
                System.out.println("error occurred");
                System.out.println(responseEntity.getStatusCode());

                Log.d(TAG, "Problem when trying to retrieve UIConfig.\nStatus: " + responseEntity.getStatusCode());
            }
        }catch(Exception e){
            Log.d(TAG, "Exception: " + e.toString());
        }
    }

    public UIConfig getResponseUIConfig() {
        return responseUIConfig;
    }

    public void sendUIConfigToRepo(UIConfig newUIConfig){
        configContainer.setUIConfig(newUIConfig);
    }
}
