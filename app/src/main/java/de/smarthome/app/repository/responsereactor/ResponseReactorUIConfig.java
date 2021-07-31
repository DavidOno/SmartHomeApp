package de.smarthome.app.repository.responsereactor;

import android.util.Log;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.smarthome.app.repository.ConfigContainer;
import de.smarthome.app.repository.Repository;
import de.smarthome.command.ResponseReactor;
import de.smarthome.app.model.UIConfig;

public class ResponseReactorUIConfig implements ResponseReactor {
    private static final String TAG = "ResponseReactorUIConfig";
    private Repository repository;

    public ResponseReactorUIConfig() {
        this.repository = Repository.getInstance(null);
    }

    @Override
    public void react(ResponseEntity responseEntity) {
        try {
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                Log.d(TAG, "response received UIConfig");
                Log.d(TAG, responseEntity.getBody().toString());

                UIConfig responseUIConfig = (UIConfig) responseEntity.getBody();
                sendUIConfigToRepo(responseUIConfig);

                Log.d(TAG, "UIConfig successfully retrieved.\nStatus: " + responseEntity.getStatusCode());

            } else {
                Log.d(TAG, "error occurred");
                Log.d(TAG, responseEntity.getStatusCode().toString());

                Log.d(TAG, "Problem when trying to retrieve UIConfig.\nStatus: " + responseEntity.getStatusCode());
            }
        }catch(Exception e){
            Log.d(TAG, "Exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public void sendUIConfigToRepo(UIConfig newUIConfig){
        repository.initNewUIConfig(newUIConfig);
    }
}