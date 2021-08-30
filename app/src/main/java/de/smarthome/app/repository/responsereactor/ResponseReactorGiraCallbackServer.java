package de.smarthome.app.repository.responsereactor;

import android.util.Log;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.smarthome.app.repository.Repository;
import de.smarthome.command.ResponseReactor;

/**
 * Commandchainreactor to handle requests to register the callbackserver at gira send via a commandchain.
 */
public class ResponseReactorGiraCallbackServer implements ResponseReactor {
    private static final String TAG = "ResponseReactorGiraCallbackServer";
    private final Repository repository;

    public ResponseReactorGiraCallbackServer() {
        repository = Repository.getInstance();
    }

    /**
     * Informs the repository about the status of the connection to the gira server.
     * @param responseEntity ResponseEntity send by the server
     */
    @Override
    public void react(ResponseEntity responseEntity) {
        try {
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                Log.d(TAG, "response received " + TAG);
                Log.d(TAG, "No Problems when registering CallbackServer at Gira.\nStatus: " + responseEntity.getStatusCode());
                repository.setServerConnectionEvent(ServerConnectionEvent.GIRA_CONNECTION_SUCCESS);
            } else {
                Log.d(TAG, "error occurred");
                Log.d(TAG, responseEntity.getStatusCode().toString());
                Log.d(TAG, "Problem when registering CallbackServer at Gira.\nStatus: " + responseEntity.getStatusCode());
                repository.setServerConnectionEvent(ServerConnectionEvent.GIRA_CONNECTION_FAIL);
            }
        }catch(Exception e){
            Log.d(TAG, "Exception: " + e.toString());
            repository.setServerConnectionEvent(ServerConnectionEvent.GIRA_CONNECTION_FAIL);
            e.printStackTrace();
        }
    }
}