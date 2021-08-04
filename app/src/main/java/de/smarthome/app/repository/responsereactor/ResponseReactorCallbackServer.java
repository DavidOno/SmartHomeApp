package de.smarthome.app.repository.responsereactor;

import android.util.Log;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.smarthome.app.repository.Repository;
import de.smarthome.command.ResponseReactor;
import de.smarthome.app.utility.ToastUtility;

public class ResponseReactorCallbackServer implements ResponseReactor {
    private static final String TAG = "ResponseReactorCallbackServer";
    private ToastUtility toastUtility;
    private Repository repository;

    public ResponseReactorCallbackServer() {
        repository = Repository.getInstance();
        toastUtility = ToastUtility.getInstance();
    }

    @Override
    public void react(ResponseEntity responseEntity) {
        try {
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                Log.d(TAG, "response received " + TAG);
                Log.d(TAG, "Registered CallbackServer.\nStatus: " + responseEntity.getStatusCode());
                repository.serverConnectionEvent(ServerConnectionEvent.CALLBACK_CONNECTION_SUCCESS);
            } else {
                Log.d(TAG, "error occurred");
                Log.d(TAG, responseEntity.getStatusCode().toString());
                Log.d(TAG, "Problem when trying to register CallbackServer.\nStatus: " + responseEntity.getStatusCode());
                toastUtility.prepareToast("Unable to reach CallbackServer");
                repository.serverConnectionEvent(ServerConnectionEvent.CALLBACK_CONNECTION_FAIL);
            }
        }catch(Exception e){
            Log.d(TAG, "Exception: " + e.toString());
            toastUtility.prepareToast("Unable to register CallbackServer!");
            repository.serverConnectionEvent(ServerConnectionEvent.CALLBACK_CONNECTION_FAIL);
            e.printStackTrace();
        }
    }
}