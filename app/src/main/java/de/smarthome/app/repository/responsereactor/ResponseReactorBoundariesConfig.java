package de.smarthome.app.repository.responsereactor;

import android.util.Log;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.smarthome.app.model.configs.BoundariesConfig;
import de.smarthome.app.repository.Repository;
import de.smarthome.command.ResponseReactor;

/**
 * Commandchainreactor to handle requests for the boundaryconfig send via a commandchain
 */
public class ResponseReactorBoundariesConfig implements ResponseReactor {
    private static final String TAG = "ResponseReactorBoundariesConfig";
    private final Repository repository;

    public ResponseReactorBoundariesConfig() {
        this.repository = Repository.getInstance();
    }
    @Override
    public void react(ResponseEntity responseEntity) {
        try {
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                Log.d(TAG, "response received UIConfig");
                Log.d(TAG, responseEntity.getBody().toString());

                BoundariesConfig responseBoundariesConfig = (BoundariesConfig) responseEntity.getBody();
                sendBoundariesConfigToRepo(responseBoundariesConfig);

                repository.setServerConnectionEvent(ServerConnectionEvent.CALLBACK_CONNECTION_SUCCESS);
                Log.d(TAG, "BoundaryConfig successfully retrieved.\nStatus: " + responseEntity.getStatusCode());
            } else {
                Log.d(TAG, "error occurred");
                Log.d(TAG, responseEntity.getStatusCode().toString());
                repository.setServerConnectionEvent(ServerConnectionEvent.CALLBACK_CONNECTION_FAIL);
                Log.d(TAG, "Problem when trying get BoundaryConfig.\nStatus: " + responseEntity.getStatusCode());
            }
        }catch(Exception e){
            Log.d(TAG, "Exception: " + e.toString());
            repository.setServerConnectionEvent(ServerConnectionEvent.CALLBACK_CONNECTION_FAIL);
            e.printStackTrace();
        }
    }

    private void sendBoundariesConfigToRepo(BoundariesConfig newBoundariesConfig){
        repository.setBoundaryConfig(newBoundariesConfig);
    }
}