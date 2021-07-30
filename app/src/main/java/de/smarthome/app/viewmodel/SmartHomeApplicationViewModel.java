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
    private boolean timerCompletionStatus;

    public SmartHomeApplicationViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance(application);
        timerCompletionStatus = true;
    }

    public boolean hasTimerCompleted() {
        return timerCompletionStatus;
    }

    public LiveData<Boolean> checkBeacon() {
        return repository.getBeaconCheck();
    }

    public void initBeaconCheck() {
        repository.resetBeaconCheck();
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

    public void startTimer(){
        timerCompletionStatus = false;
        Thread timerThread = new Thread(() -> {
            try {
                Thread.sleep(3000);
                timerCompletionStatus = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        timerThread.start();
    }
}
