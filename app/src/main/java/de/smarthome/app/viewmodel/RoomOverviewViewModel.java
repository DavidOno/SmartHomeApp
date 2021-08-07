package de.smarthome.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.Map;

import de.smarthome.app.model.Function;
import de.smarthome.app.model.Location;
import de.smarthome.app.model.configs.ChannelConfig;
import de.smarthome.app.repository.Repository;
import de.smarthome.app.repository.StatusRequestType;

public class RoomOverviewViewModel  extends AndroidViewModel {
    private static final String TAG = "RoomOverviewViewModel";
    private Repository repository;

    public RoomOverviewViewModel(@NonNull Application application)  {
        super(application);
        repository = Repository.getInstance();
    }

    public LiveData<Map<Function, Function>> getFunctionMap(){
        repository.requestCurrentStatusValues(StatusRequestType.FUNCTION);
        return repository.getFunctionMap();
    }

    public void requestSetValue(String id, String value){
        repository.requestSetValue(id, value);
    }

    public void setSelectedFunction(Function function){
        repository.setSelectedFunction(function);
    }

    public LiveData<Map<String, String>> getStatusUpdateMap(){
        return repository.getStatusUpdateMap();
    }

    public Location getSelectedLocation(){
        return repository.getSelectedLocation();
    }

    public LiveData<Map<String, String>> getStatusGetValueMap(){
        return repository.getStatusGetValueMap();
    }

    public ChannelConfig getChannelConfig(){
        return repository.getChannelConfig();
    }
}