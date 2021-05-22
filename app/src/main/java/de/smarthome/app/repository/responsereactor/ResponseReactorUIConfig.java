package de.smarthome.app.repository.responsereactor;

import android.util.Log;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.smarthome.app.repository.ConfigContainer;
import de.smarthome.command.ResponseReactor;
import de.smarthome.app.model.UIConfig;
import de.smarthome.app.utility.ToastUtility;

public class ResponseReactorUIConfig implements ResponseReactor {
    private static final String TAG = "ResponseReactorUIConfig";
    private UIConfig responseUIConfig;
    private ConfigContainer configContainer;

    private ToastUtility toastUtility;

    public ResponseReactorUIConfig() {
        this.configContainer = ConfigContainer.getInstance();
        this.toastUtility = ToastUtility.getInstance();
    }

    @Override
    public void react(ResponseEntity responseEntity) {
        try {
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                Log.d(TAG, "response received UIConfig");
                Log.d(TAG, responseEntity.getBody().toString());

                responseUIConfig = (UIConfig) responseEntity.getBody();
                sendUIConfigToRepo(responseUIConfig);

                Log.d(TAG, "UIConfig successfully retrieved.\nStatus: " + responseEntity.getStatusCode());

            } else {
                Log.d(TAG, "error occurred");
                Log.d(TAG, responseEntity.getStatusCode().toString());

                Log.d(TAG, "Problem when trying to retrieve UIConfig.\nStatus: " + responseEntity.getStatusCode());
            }
        }catch(Exception e){
            Log.d(TAG, "Exception: " + e.toString());
        }
    }

    public UIConfig getResponseUIConfig() {
        return responseUIConfig;
    }

    public void sendUIConfigToRepo(UIConfig newUIConfig){
        configContainer.setUIConfig(newUIConfig);
    }
}
