package de.smarthome.app.repository.responsereactor;

import android.util.Log;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.smarthome.app.repository.Repository;
import de.smarthome.command.ResponseReactor;
import de.smarthome.app.model.configs.ChannelConfig;

/**
 * Commandchainreactor to handle requests for the channelconfig send via a commandchain
 */
public class ResponseReactorChannelConfig implements ResponseReactor {
    private static final String TAG = "ResponseReactorChannelConfig";
    private final Repository repository;

    public ResponseReactorChannelConfig() {
        repository = Repository.getInstance();
    }

    @Override
    public void react(ResponseEntity responseEntity) {
        try {
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                Log.d(TAG, "response received ChannelConfig");
                Log.d(TAG, responseEntity.getBody().toString());

                ChannelConfig responseChannelConfig = (ChannelConfig) responseEntity.getBody();
                sendChannelConfigToRepo(responseChannelConfig);

                repository.serverConnectionEvent(ServerConnectionEvent.CALLBACK_CONNECTION_SUCCESS);
                Log.d(TAG, "ChannelConfig retrieved successfully.\nStatus: " + responseEntity.getStatusCode());
            } else {
                Log.d(TAG, "error occurred");
                Log.d(TAG, responseEntity.getStatusCode().toString());
                repository.serverConnectionEvent(ServerConnectionEvent.CALLBACK_CONNECTION_FAIL);
                Log.d(TAG, "Problem when trying to retrieve ChannelConfig.\nStatus: " + responseEntity.getStatusCode());
            }
        }catch(Exception e){
            Log.d(TAG, "Exception: " + e.toString());
            repository.serverConnectionEvent(ServerConnectionEvent.CALLBACK_CONNECTION_FAIL);
            e.printStackTrace();
        }
    }

    private void sendChannelConfigToRepo(ChannelConfig newChannelConfig){
        repository.setChannelConfig(newChannelConfig);
    }
}