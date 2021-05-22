package de.smarthome.command.impl;

import android.util.Log;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.smarthome.command.ResponseReactor;
import de.smarthome.app.utility.ToastUtility;

public class ResponseReactorGiraCallbackServer implements ResponseReactor {
    private static final String TAG = "ResponseReactorGiraCallbackServer";

    private ToastUtility toastUtility;

    public ResponseReactorGiraCallbackServer() {
        this.toastUtility = ToastUtility.getInstance();
    }

    @Override
    public void react(ResponseEntity responseEntity) {
        try {
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                Log.d(TAG, "response received " + TAG);
                Log.d(TAG, responseEntity.getBody().toString());

                Log.d(TAG, "No Problems when registering CallbackServer at Gira.\nStatus: " + responseEntity.getStatusCode());

            } else {
                Log.d(TAG, "error occurred");
                Log.d(TAG, responseEntity.getStatusCode().toString());

                Log.d(TAG, "Problem when registering CallbackServer at Gira.\nStatus: " + responseEntity.getStatusCode());
                toastUtility.prepareToast("Unable to reach Gira Server");
            }
        }catch(Exception e){
            Log.d(TAG, "Exerption: " + e.toString());

            toastUtility.prepareToast("Exception: Unable to register CallbackServer at Gira!");
        }
    }
}
