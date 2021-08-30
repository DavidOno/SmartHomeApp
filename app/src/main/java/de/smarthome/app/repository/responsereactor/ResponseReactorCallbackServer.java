package de.smarthome.app.repository.responsereactor;

import android.util.Log;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.smarthome.app.repository.Repository;
import de.smarthome.command.ResponseReactor;

/**
 * Commandchainreactor to handle requests to connect to the callbackserver send via a commandchain.
 */
public class ResponseReactorCallbackServer implements ResponseReactor {
    private static final String TAG = "ResponseReactorCallbackServer";
    private final Repository repository;

    public ResponseReactorCallbackServer() {
        repository = Repository.getInstance();
    }

    /**
     * Informs the repository about the status of the connection to the callbackserver.
     * @param responseEntity ResponseEntity send by the server
     */
    @Override
    public void react(ResponseEntity responseEntity) {
        try {
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                Log.d(TAG, "response received " + TAG);
                Log.d(TAG, "Registered CallbackServer.\nStatus: " + responseEntity.getStatusCode());
                repository.setServerConnectionEvent(ServerConnectionEvent.CALLBACK_CONNECTION_SUCCESS);
            } else {
                Log.d(TAG, "error occurred");
                Log.d(TAG, responseEntity.getStatusCode().toString());
                Log.d(TAG, "Problem when trying to register CallbackServer.\nStatus: " + responseEntity.getStatusCode());
                repository.setServerConnectionEvent(ServerConnectionEvent.CALLBACK_CONNECTION_FAIL);
            }
        }catch(Exception e){
            Log.d(TAG, "Exception: " + e.toString());
            repository.setServerConnectionEvent(ServerConnectionEvent.CALLBACK_CONNECTION_FAIL);
            e.printStackTrace();
        }
    }
}