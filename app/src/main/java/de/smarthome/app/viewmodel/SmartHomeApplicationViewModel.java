package de.smarthome.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.android.gms.auth.api.credentials.Credential;

import de.smarthome.app.model.Location;
import de.smarthome.app.repository.Repository;

public class SmartHomeApplicationViewModel extends AndroidViewModel {
    private static final String TAG = "SmartHomeApplicationViewModel";
    private Repository repository;

    public SmartHomeApplicationViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance();
        repository.setParentApplication(application);
    }

    public LiveData<Boolean> getServerConnectionStatus() {
        return repository.getServerConnectionStatus();
    }

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

    public void confirmBeacon() {
        repository.confirmBeaconLocation();
    }

    public Location getBeaconLocation() {
        return repository.getBeaconLocation();
    }

    public void unsubscribeFromEverything() {
        repository.unsubscribeFromEverything();
    }

    public void requestRegisterUser(Credential credential) {
        repository.requestRegisterUser(credential);
    }
}
