package de.smarthome.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.Map;

import de.smarthome.app.model.Function;
import de.smarthome.app.model.Location;
import de.smarthome.app.repository.Repository;

public class RoomOverviewViewModel  extends AndroidViewModel {
    private  final String TAG = "RoomOverviewViewModel";
    private Repository repository;

    public RoomOverviewViewModel(@NonNull Application application)  {
        super(application);

        repository = Repository.getInstance(application);
    }

    public LiveData<Map<Function, Function>> getUsableRoomFunctions(){
        return repository.getRoomUsableFunctions();
    }

    public LiveData<Map<Function, Function>> getRoomStatusFunctions(){
        return repository.getRoomStatusFunctions();
    }

    public boolean isChannelInputOnlyBinary(Function function){
        return repository.getSmartHomeChannelConfig().isFirstDataPointBinary(function);
    }

    public void requestSetValue(String ID, String value){
        repository.requestSetValue(ID, value);
    }

    public void setSelectedFunction(Function function){
        repository.setSelectedFunction(function);
    }

    public LiveData<Map<String, String>> getStatusList(){
        return repository.getStatusList();
    }

    public Location getSelectedLoction(){
        return repository.getSelectedLocation();
    }

    public LiveData<Map<String, String>> getStatusList2(){
        return repository.statusList2;
    }

}
