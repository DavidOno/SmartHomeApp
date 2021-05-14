package de.smarthome.command.impl;

import android.util.Log;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.smarthome.command.ResponseReactor;
import de.smarthome.app.utility.ToastUtility;

public class ResponseReactorGiraCallbackServer implements ResponseReactor {
    private final String TAG = "ResponseReactorGiraCallbackServer";

    private ToastUtility toastUtility;

    public ResponseReactorGiraCallbackServer() {
        this.toastUtility = ToastUtility.getInstance();
    }

    @Override
    public void react(ResponseEntity responseEntity) {
        try {
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                System.out.println("response received " + TAG);
                System.out.println(responseEntity.getBody());

                Log.d(TAG, "No Problems when registering CallbackServer at Gira.\nStatus: " + responseEntity.getStatusCode());

            } else {
                System.out.println("error occurred");
                System.out.println(responseEntity.getStatusCode());

                Log.d(TAG, "Problem when registering CallbackServer at Gira.\nStatus: " + responseEntity.getStatusCode());
                toastUtility.prepareToast("Unable to reach Gira Server");
            }
        }catch(Exception e){
            Log.d(TAG, "Exception: " + e.toString());
            toastUtility.prepareToast("Exception: Unable to register CallbackServer at Gira!");
        }
    }
}
