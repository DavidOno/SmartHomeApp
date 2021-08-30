package de.smarthome.app.repository.responsereactor;

import android.util.Log;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.smarthome.app.repository.Repository;
import de.smarthome.command.ResponseReactor;
import de.smarthome.app.model.UIConfig;

/**
 * Commandchainreactor to handle requests for the uiconfigconfig send via a commandchain.
 */
public class ResponseReactorUIConfig implements ResponseReactor {
    private static final String TAG = "ResponseReactorUIConfig";
    private final Repository repository;

    public ResponseReactorUIConfig() {
        this.repository = Repository.getInstance();
    }

    /**
     * Extracts the uiconfigconfig from the body of the responseentity and saves it in the repository.
     * @param responseEntity ResponseEntity send by the server
     */
    @Override
    public void react(ResponseEntity responseEntity) {
        try {
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                Log.d(TAG, "response received UIConfig");
                Log.d(TAG, responseEntity.getBody().toString());

                UIConfig responseUIConfig = (UIConfig) responseEntity.getBody();
                sendUIConfigToRepo(responseUIConfig);

                repository.setServerConnectionEvent(ServerConnectionEvent.GIRA_CONNECTION_SUCCESS);
                Log.d(TAG, "UIConfig successfully retrieved.\nStatus: " + responseEntity.getStatusCode());
            } else {
                Log.d(TAG, "error occurred");
                Log.d(TAG, responseEntity.getStatusCode().toString());
                repository.setServerConnectionEvent(ServerConnectionEvent.GIRA_CONNECTION_FAIL);
                Log.d(TAG, "Problem when trying to retrieve UIConfig.\nStatus: " + responseEntity.getStatusCode());
            }
        }catch(Exception e){
            Log.d(TAG, "Exception: " + e.toString());
            repository.setServerConnectionEvent(ServerConnectionEvent.GIRA_CONNECTION_FAIL);
            e.printStackTrace();
        }
    }

    private void sendUIConfigToRepo(UIConfig newUIConfig){
        repository.initNewUIConfig(newUIConfig);
    }
}