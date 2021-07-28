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
    private boolean timerCompleted;

    public SmartHomeApplicationViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance(application);
        timerCompleted = true;
    }

    public boolean hasTimerCompleted() {
        return timerCompleted;
    }

    public LiveData<Boolean> checkBeacon() {
        return repository.checkBeacon();
    }

    public void initBeaconCheck() {
        repository.initBeaconCheck();
    }

    public void confirmBeacon() {
        repository.confirmBeacon();
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

    //TODO: Check if Timer works as intended
    public void startTimer(){
        timerCompleted = false;
        Thread timerThread = new Thread(() -> {
            try {
                Thread.sleep(10000);
                timerCompleted = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        timerThread.start();
    }
}
