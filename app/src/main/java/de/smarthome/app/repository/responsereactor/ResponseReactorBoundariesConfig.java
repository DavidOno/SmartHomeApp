package de.smarthome.app.repository.responsereactor;

import android.util.Log;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.smarthome.app.model.configs.BoundariesConfig;
import de.smarthome.command.ResponseReactor;
import de.smarthome.app.repository.Repository;

public class ResponseReactorBoundariesConfig implements ResponseReactor {
    private final String TAG = "ResponseReactorBoundariesConfig";
    private BoundariesConfig responseBoundariesConfig;
    private Repository parentRepository;

    public ResponseReactorBoundariesConfig(Repository parentRepository) {
        this.parentRepository = parentRepository;
    }
    //TODO:Rework Logs
    @Override
    public void react(ResponseEntity responseEntity) {
        try {
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                System.out.println("response received UIConfig");
                System.out.println(responseEntity.getBody());

                responseBoundariesConfig = (BoundariesConfig) responseEntity.getBody();
                sendBoundariesConfigToRepo(responseBoundariesConfig);

                Log.d(TAG, "Communication with Server possible.\nStatus: " + responseEntity.getStatusCode());
            } else {
                System.out.println("error occurred");
                System.out.println(responseEntity.getStatusCode());
                Log.d(TAG, "Problem when trying to reach Server.\nStatus: " + responseEntity.getStatusCode());
            }
        }catch(Exception e){
            Log.d(TAG, "Exerption: " + e.toString());
        }
    }

    public BoundariesConfig getResponseBoundariesConfig() {
        return responseBoundariesConfig;
    }

    public void sendBoundariesConfigToRepo(BoundariesConfig newBoundariesConfig){
        parentRepository.setBoundaryConfig(newBoundariesConfig);
    }
}