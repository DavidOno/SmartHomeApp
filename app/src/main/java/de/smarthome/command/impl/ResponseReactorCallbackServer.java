package de.smarthome.command.impl;

import android.util.Log;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.smarthome.utility.ToastUtility;

public class ResponseReactorCallbackServer implements ResponseReactor{
    private final String TAG = "ResponseReactorCallbackServer";

    private ToastUtility toastUtility;

    public ResponseReactorCallbackServer() {
        this.toastUtility = ToastUtility.getInstance();
    }

    @Override
    public void react(ResponseEntity responseEntity) {
        try {
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                System.out.println("response received " + TAG);
                System.out.println(responseEntity.getBody());

                Log.d(TAG, "Registered CallbackServer.\nStatus: " + responseEntity.getStatusCode());

                toastUtility.prepareToast("CallbackServer successfully registered!");
            } else {
                System.out.println("error occurred");
                System.out.println(responseEntity.getStatusCode());

                Log.d(TAG, "Problem when trying to register CallbackServer.\nStatus: " + responseEntity.getStatusCode());

                toastUtility.prepareToast("Unable to register CallbackServer!");
            }
        }catch(Exception e){
            Log.d(TAG, "Exerption: " + e.toString());

            toastUtility.prepareToast("Exception: Unable to register CallbackServer!");
        }
    }
}
