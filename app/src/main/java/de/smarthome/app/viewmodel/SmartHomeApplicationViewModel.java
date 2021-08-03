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
    private final int TIMER_DURATION = 15000;

    public SmartHomeApplicationViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance();
        repository.setParentApplication(application);
        timerCompletionStatus = true;
    }

    public int getTimerDuration() {
        return TIMER_DURATION;
    }

    public void setSelectedLocation(Location location){
        repository.setSelectedLocation(location);
    }

    public boolean hasTimerCompleted() {
        return timerCompletionStatus;
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

    public void startTimer(){
        timerCompletionStatus = false;
        Thread timerThread = new Thread(() -> {
            try {
                Thread.sleep(TIMER_DURATION);
                timerCompletionStatus = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        timerThread.start();
    }
}
