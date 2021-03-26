package de.smarthome.model.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import de.smarthome.model.impl.Function;
import de.smarthome.model.impl.Location;
import de.smarthome.model.repository.Repository;

public class RoomOverviewViewModel  extends AndroidViewModel {
    private  final String TAG = "RoomOverviewViewModel";
    private Repository repository;

    public RoomOverviewViewModel(@NonNull Application application)  {
        super(application);

        repository = Repository.getInstance();
    }

    public LiveData<List<Function>> getUsableRoomFunctions(){
        return repository.getRoomUsableFunctions();
    }

    public LiveData<List<Function>> getRoomStatusFunctions(){
        return repository.getRoomStatusFunctions();
    }

    public boolean isChannelInputOnlyBinary(Function function){
        return repository.getSmartHomeChannelConfig().isOnlyBinary(function);
    }

    public void requestSetValue(String ID, String value){
        repository.requestSetValue(ID, value);
    }

    public void setSelectedFunction(Function function){
        repository.setSelectedFunction(function);
    }
}
