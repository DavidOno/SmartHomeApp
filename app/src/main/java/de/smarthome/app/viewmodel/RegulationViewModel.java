package de.smarthome.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.Map;

import de.smarthome.app.model.Datapoint;
import de.smarthome.app.model.Function;
import de.smarthome.app.model.configs.BoundaryDataPoint;
import de.smarthome.app.model.configs.ChannelConfig;
import de.smarthome.app.repository.Repository;

public class RegulationViewModel extends AndroidViewModel {
    private static final String TAG = "RegulationViewModel";
    private Repository repository;

    public RegulationViewModel(@NonNull Application application)  {
        super(application);
        repository = Repository.getInstance();
    }

    public void requestSetValue(String id, String value){
        repository.requestSetValue(id, value);
    }

    public LiveData<Map<Datapoint, Datapoint>> getDataPointMap(){
        return repository.getDataPointMap();
    }

    public LiveData<Map<String, String>> getStatusUpdateMap(){
        return repository.getStatusUpdateMap();
    }

    public Function getSelectedFunction(){
        return repository.getSelectedFunction();
    }

    public LiveData<Map<Datapoint, BoundaryDataPoint>> getBoundaryMap(){
        return  repository.getBoundaryMap();
    }

    public LiveData<Map<String, String>> getStatusGetValueMap() {
        return repository.getStatusGetValueMap();
    }

    public ChannelConfig getChannelConfig(){
        return repository.getChannelConfig();
    }
}