package de.smarthome.command.impl;

import android.util.Log;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.smarthome.app.model.responses.GetValueReponse;
import de.smarthome.app.repository.Repository;
import de.smarthome.app.utility.ToastUtility;
import de.smarthome.command.ResponseReactor;

public class ResponseReactorGetValue implements ResponseReactor {
    private final String TAG = "ResponseReactorGetValue";
    private Repository parentRepository;

    private ToastUtility toastUtility;

    public ResponseReactorGetValue(Repository parentRepository) {
        this.toastUtility = ToastUtility.getInstance();
        this.parentRepository = parentRepository;

        this.toastUtility = ToastUtility.getInstance();
    }

    @Override
    public void react(ResponseEntity responseEntity) {
        try {
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                System.out.println("response received " + TAG);
                System.out.println(responseEntity.getBody());

                //TODO: Change Logs!
                Log.d(TAG, "No Problems when registering Client at Gira.\nStatus: " + responseEntity.getStatusCode());
                GetValueReponse x = (GetValueReponse) responseEntity.getBody();

                String value = x.getValues().get(0).getValue();
                String uID = x.getValues().get(0).getUid();
                parentRepository.updateStatusList2(uID, value);

            } else {
                System.out.println("error occurred");
                System.out.println(responseEntity.getStatusCode());

                Log.d(TAG, "Problem when registering Client at Gira.\nStatus: " + responseEntity.getStatusCode());
            }
        }catch(Exception e){
            Log.d(TAG, "Exerption: " + e.toString());

            toastUtility.prepareToast("Exception: Unable to register User at Gira!");
        }
    }
}

