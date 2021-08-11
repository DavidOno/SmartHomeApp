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

/**
 * This class is the viewmodel of the roomoverviewmodel.
 * It handles the communication with the repository.
 */
public class RoomOverviewViewModel extends AndroidViewModel {
    private static final String TAG = "RoomOverviewViewModel";
    private final Repository repository;

    public RoomOverviewViewModel(@NonNull Application application)  {
        super(application);
        repository = Repository.getInstance();
    }

    /**
     * Requests the current status values and returns the functionMap
     * @return map containing all functions of the selected location
     */
    public LiveData<Map<Function, Function>> getFunctionMap(){
        //TODO: EDIT JavaDoc
        return repository.getFunctionMap();
    }

    public void requestCurrentStatusValues(){
        repository.requestCurrentStatusValues(StatusRequestType.FUNCTION);
    }
    public void requestSetValue(String id, String value){
        repository.requestSetValue(id, value);
    }

    public void setSelectedFunction(Function function){
        repository.initSelectedFunction(function);
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