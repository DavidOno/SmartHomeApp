package de.smarthome.command.impl;

import android.util.Log;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.smarthome.command.ResponseReactor;
import de.smarthome.app.utility.ToastUtility;

public class ResponseReactorCheckAvailability implements ResponseReactor {
    private static final String TAG = "ResponseReactorCheckAvailability";

    private ToastUtility toastUtility;

    public ResponseReactorCheckAvailability() {
        this.toastUtility = ToastUtility.getInstance();
    }

    @Override
    public void react(ResponseEntity responseEntity) {
        try {
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                Log.d(TAG, "response received " + TAG);
                Log.d(TAG, responseEntity.getBody().toString());

                Log.d(TAG, "Communication with Server possible.\nStatus: " + responseEntity.getStatusCode());

            } else {
                Log.d(TAG, "error occurred");
                Log.d(TAG, responseEntity.getStatusCode().toString());

                Log.d(TAG, "Problem when trying to reach Server.\nStatus: " + responseEntity.getStatusCode());

            }
        }catch(Exception e){
            Log.d(TAG, "Exerption: " + e.toString());

            toastUtility.prepareToast("Exception: Unable to reach Gira!");
        }
    }
}
