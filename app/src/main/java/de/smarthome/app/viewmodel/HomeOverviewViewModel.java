package de.smarthome.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import de.smarthome.app.model.Location;
import de.smarthome.app.repository.Repository;

/**
 * This class is the viewmodel of the homeoverviewfragment.
 * It handles the communication with the repository.
 */
public class HomeOverviewViewModel extends AndroidViewModel {
    private static final String TAG = "HomeOverviewViewmodel";
    private final Repository repository;

    public HomeOverviewViewModel(@NonNull Application application)  {
        super(application);
        repository = Repository.getInstance();
    }

    public LiveData<List<Location>> getLocationList(){
        return repository.getLocationList();
    }

    /**
     * Initialises a location as the selected location
     * @param location Location to be initialised
     */
    public void initSelectedLocation(Location location){
        repository.initSelectedLocation(location);
    }

    public boolean isChannelConfigLoaded(){
        return repository.getChannelConfig() != null;
    }
}