package de.smarthome.model.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import de.smarthome.model.impl.Location;
import de.smarthome.model.repository.Repository;

public class HomeOverviewViewModel extends AndroidViewModel {
    private  final String TAG = "HomeOverviewViewmodel";
    MutableLiveData<List<Location>> roomSet;
    Repository repository;

    public HomeOverviewViewModel(@NonNull Application application)  {
        super(application);

        repository = Repository.getInstance();
        roomSet = repository.getRooms();
    }

    public LiveData<List<Location>> getRooms(){
        return roomSet;
    }

    public void setSelectedLocation(Location location){
        repository.setSelectedLocation(location);
    }
}
