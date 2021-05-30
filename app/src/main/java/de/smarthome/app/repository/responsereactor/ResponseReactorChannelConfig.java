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

    public ResponseReactorChannelConfig() {
        configContainer = ConfigContainer.getInstance();
    }

    @Override
    public void react(ResponseEntity responseEntity) {
        try {
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                Log.d(TAG, "response received ChannelConfig");
                Log.d(TAG, responseEntity.getBody().toString());

                responseChannelConfig = (ChannelConfig) responseEntity.getBody();
                sendChannelConfigToRepo(responseChannelConfig);

                Log.d(TAG, "ChannelConfig retrieved successfully.\nStatus: " + responseEntity.getStatusCode());

            } else {
                Log.d(TAG, "error occurred");
                Log.d(TAG, responseEntity.getStatusCode().toString());

                Log.d(TAG, "Problem when trying to retrieve ChannelConfig.\nStatus: " + responseEntity.getStatusCode());

            }
        }catch(Exception e){
            Log.d(TAG, "Exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public ChannelConfig getResponseChannelConfig() {
        return responseChannelConfig;
    }

    public void sendChannelConfigToRepo(ChannelConfig newChannelConfig){
        configContainer.setChannelConfig(newChannelConfig);
    }
}