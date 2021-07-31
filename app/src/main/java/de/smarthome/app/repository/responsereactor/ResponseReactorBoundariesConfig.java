package de.smarthome.app.repository.responsereactor;

import android.util.Log;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.smarthome.app.model.configs.BoundariesConfig;
import de.smarthome.app.repository.Repository;
import de.smarthome.command.ResponseReactor;

public class ResponseReactorBoundariesConfig implements ResponseReactor {
    private static final String TAG = "ResponseReactorBoundariesConfig";
    private Repository repository;

    public ResponseReactorBoundariesConfig() {
        this.repository = Repository.getInstance(null);
    }
    @Override
    public void react(ResponseEntity responseEntity) {
        try {
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                Log.d(TAG, "response received UIConfig");
                Log.d(TAG, responseEntity.getBody().toString());

                BoundariesConfig responseBoundariesConfig = (BoundariesConfig) responseEntity.getBody();
                sendBoundariesConfigToRepo(responseBoundariesConfig);

                Log.d(TAG, "BoundaryConfig successfully retrieved.\nStatus: " + responseEntity.getStatusCode());
            } else {
                Log.d(TAG, "error occurred");
                Log.d(TAG, responseEntity.getStatusCode().toString());
                Log.d(TAG, "Problem when trying get BoundaryConfig.\nStatus: " + responseEntity.getStatusCode());
            }
        }catch(Exception e){
            Log.d(TAG, "Exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public void sendBoundariesConfigToRepo(BoundariesConfig newBoundariesConfig){
        repository.setBoundaryConfig(newBoundariesConfig);
    }
}