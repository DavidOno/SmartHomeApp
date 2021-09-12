package de.smarthome.app.repository.responsereactor;

import android.util.Log;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.smarthome.app.repository.Repository;
import de.smarthome.command.ResponseReactor;
import de.smarthome.app.utility.ToastUtility;

/**
 * Commandchainreactor to handle requests to register the client at gira send via a commandchain.
 */
public class ResponseReactorClient implements ResponseReactor {
    private static final String TAG = "ResponseReactorClient";
    private final Repository repository;
    private final ToastUtility toastUtility;

    public ResponseReactorClient() {
        this.repository = Repository.getInstance();
        this.toastUtility = ToastUtility.getInstance();
    }

    /**
     * Sets the login status depending on the response of the gira server.
     * @param responseEntity ResponseEntity send by the server
     */
    @Override
    public void react(ResponseEntity responseEntity) {
        try {
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                Log.d(TAG, "response received " + TAG);
                Log.d(TAG, responseEntity.getBody().toString());
                Log.d(TAG, "No Problems when registering Client at Gira.\nStatus: " + responseEntity.getStatusCode());
                repository.setServerConnectionEvent(ServerConnectionEvent.GIRA_CONNECTION_SUCCESS);
                repository.setLoginStatus(true);
            } else {
                Log.d(TAG, "error occurred");
                Log.d(TAG, responseEntity.getStatusCode().toString());
                Log.d(TAG, "Problem when registering Client at Gira.\nStatus: " + responseEntity.getStatusCode());
                toastUtility.prepareToast("Unable to register User at Gira!");
                repository.setServerConnectionEvent(ServerConnectionEvent.GIRA_CONNECTION_FAIL);
                repository.setLoginStatus(false);
            }
        }catch(Exception e){
            Log.d(TAG, "Exception: " + e.toString());
            toastUtility.prepareToast("Unable to register User at Gira!");
            repository.setLoginStatus(true);
            repository.setServerConnectionEvent(ServerConnectionEvent.GIRA_CONNECTION_FAIL);
            e.printStackTrace();
        }
    }
}