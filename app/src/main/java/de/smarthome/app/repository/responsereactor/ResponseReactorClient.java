package de.smarthome.app.repository.responsereactor;

import android.util.Log;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.smarthome.app.repository.ServerCommunicator;
import de.smarthome.command.ResponseReactor;
import de.smarthome.app.utility.ToastUtility;

public class ResponseReactorClient implements ResponseReactor {
    private static final String TAG = "ResponseReactorClient";
    private ServerCommunicator serverCommunicator;

    private ToastUtility toastUtility;

    public ResponseReactorClient() {
        this.serverCommunicator = ServerCommunicator.getInstance(null);
        this.toastUtility = ToastUtility.getInstance();
    }

    @Override
    public void react(ResponseEntity responseEntity) {
        try {
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                Log.d(TAG, "response received " + TAG);
                Log.d(TAG, responseEntity.getBody().toString());

                Log.d(TAG, "No Problems when registering Client at Gira.\nStatus: " + responseEntity.getStatusCode());
                serverCommunicator.updateLoginDataStatus(true);

            } else {
                Log.d(TAG, "error occurred");
                Log.d(TAG, responseEntity.getStatusCode().toString());

                Log.d(TAG, "Problem when registering Client at Gira.\nStatus: " + responseEntity.getStatusCode());
                serverCommunicator.updateLoginDataStatus(false);
            }
        }catch(Exception e){
            Log.d(TAG, "Exception: " + e.toString());
            toastUtility.prepareToast("Exception: Unable to register User at Gira!");
            e.printStackTrace();
        }
    }
}