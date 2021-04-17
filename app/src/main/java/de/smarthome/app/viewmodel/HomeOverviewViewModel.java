package de.smarthome.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import de.smarthome.app.model.Location;
import de.smarthome.app.repository.Repository;

public class HomeOverviewViewModel extends AndroidViewModel {
    private  final String TAG = "HomeOverviewViewmodel";
    private  MutableLiveData<List<Location>> roomSet;
    private  Repository repository;

    public HomeOverviewViewModel(@NonNull Application application)  {
        super(application);

        repository = Repository.getInstance(application);
        roomSet = repository.getRooms();
    }

    public LiveData<List<Location>> getRooms(){
        return roomSet;
    }

    public void setSelectedLocation(Location location){
        repository.setSelectedLocation(location);
    }

}
