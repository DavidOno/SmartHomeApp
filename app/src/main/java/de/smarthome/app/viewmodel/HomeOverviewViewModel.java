package de.smarthome.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import de.smarthome.app.model.Location;
import de.smarthome.app.repository.Repository;

public class HomeOverviewViewModel extends AndroidViewModel {
    private static final String TAG = "HomeOverviewViewmodel";
    private Repository repository;

    public HomeOverviewViewModel(@NonNull Application application)  {
        super(application);
        repository = Repository.getInstance(application);
        //TODO: Remove after Testing
        repository.test();
    }

    public LiveData<List<Location>> getLocationList(){
        return repository.getLocationList();
    }

    public void setSelectedLocation(Location location){
        repository.setSelectedLocation(location);
    }

}