package de.smarthome.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.android.gms.auth.api.credentials.Credential;

import de.smarthome.app.model.Location;
import de.smarthome.app.repository.Repository;

/**
 * This class is the viewmodel of the smarthomeapplication.
 * It handles the communication with the repository.
 */
public class SmartHomeApplicationViewModel extends AndroidViewModel {
    private static final String TAG = "SmartHomeApplicationViewModel";
    private final Repository repository;

    public SmartHomeApplicationViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance();
        repository.setParentApplication(application);
    }

    public LiveData<Boolean> getServerConnectionStatus() {
        return repository.getServerConnectionStatus();
    }

    /**
     * Restarts only the failed connection to the servers.
     */
    public void retryConnectionToServerAfterFailure(){
        repository.retryConnectionToServerAfterFailure();
    }

    public void setSelectedLocation(Location location){
        repository.initSelectedLocation(location);
    }

    public LiveData<Boolean> checkBeacon() {
        return repository.getBeaconCheck();
    }

    public void setBeaconCheckFalse() {
        repository.setBeaconCheckFalse();
    }

    /**
     * Initialised the beaconlocation as the selectedlocation and then resets beaconlocation.
     */
    public void confirmBeacon() {
        repository.confirmBeaconLocation();
    }

    public Location getBeaconLocation() {
        return repository.getBeaconLocation();
    }

    /**
     * Unsubscribes and unregisters the application from all services.
     */
    public void unsubscribeFromEverything() {
        repository.unsubscribeFromEverything();
    }


    /**
     * Request to register the user at the gira server.
     * @param credential Credentials of the user
     */
    public void requestRegisterUser(Credential credential) {
        repository.requestRegisterUser(credential);
    }
}
