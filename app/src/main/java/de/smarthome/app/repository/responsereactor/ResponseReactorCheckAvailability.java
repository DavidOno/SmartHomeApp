package de.smarthome.app.repository.responsereactor;

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
                System.out.println("response received " + TAG);
                System.out.println(responseEntity.getBody());

                Log.d(TAG, "Communication with Server possible.\nStatus: " + responseEntity.getStatusCode());

            } else {
                System.out.println("error occurred");
                System.out.println(responseEntity.getStatusCode());

                Log.d(TAG, "Problem when trying to reach Server.\nStatus: " + responseEntity.getStatusCode());

            }
        }catch(Exception e){
            Log.d(TAG, "Exception: " + e.toString());
        }
    }
}
