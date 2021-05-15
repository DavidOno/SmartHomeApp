package de.smarthome.app.repository.responsereactor;


import android.util.Log;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.smarthome.app.repository.ConfigContainer;
import de.smarthome.command.ResponseReactor;
import de.smarthome.app.model.configs.ChannelConfig;
import de.smarthome.app.utility.ToastUtility;

public class ResponseReactorChannelConfig implements ResponseReactor {
    private static final String TAG = "ResponseReactorChannelConfig";
    private ChannelConfig responseChannelConfig;
    private ConfigContainer configContainer;

    private ToastUtility toastUtility;

    public ResponseReactorChannelConfig() {
        configContainer = ConfigContainer.getInstance();
        toastUtility = ToastUtility.getInstance();
    }

    @Override
    public void react(ResponseEntity responseEntity) {
        try {
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                System.out.println("response received ChannelConfig");
                System.out.println(responseEntity.getBody());

                responseChannelConfig = (ChannelConfig) responseEntity.getBody();
                sendChannelConfigToRepo(responseChannelConfig);

                Log.d(TAG, "ChannelConfig retrieved successfully.\nStatus: " + responseEntity.getStatusCode());

            } else {
                System.out.println("error occurred");
                System.out.println(responseEntity.getStatusCode());

                Log.d(TAG, "Problem when trying to retrieve ChannelConfig.\nStatus: " + responseEntity.getStatusCode());

            }
        }catch(Exception e){
            Log.d(TAG, "Exception: " + e.toString());
        }
    }

    public ChannelConfig getResponseChannelConfig() {
        return responseChannelConfig;
    }

    public void sendChannelConfigToRepo(ChannelConfig newChannelConfig){
        configContainer.setChannelConfig(newChannelConfig);
    }
}