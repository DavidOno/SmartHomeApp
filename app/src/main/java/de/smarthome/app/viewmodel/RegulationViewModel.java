package de.smarthome.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.Map;

import de.smarthome.app.model.impl.Datapoint;
import de.smarthome.app.model.impl.Function;
import de.smarthome.app.model.impl.Location;
import de.smarthome.app.repository.Repository;

public class RegulationViewModel extends AndroidViewModel {
    private  final String TAG = "RegulationViewModel";
    private   Repository repository;

    public RegulationViewModel(@NonNull Application application)  {
        super(application);

        repository = Repository.getInstance(application);
    }


    public void requestSetValue(String ID, String value){
        repository.requestSetValue(ID, value);
    }

    public Function getFunctionByUID(String UID, Location location){
        return repository.getFunctionByUID(UID, location);
    }

    public LiveData<Map<Datapoint, Datapoint>> getDataPoints(){
        return repository.getDataPoints();
    }

    public LiveData<Map<String, String>> getStatusList(){
        return repository.getStatusList();
    }
}
