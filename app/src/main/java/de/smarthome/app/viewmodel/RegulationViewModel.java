package de.smarthome.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.Map;

import de.smarthome.app.model.Datapoint;
import de.smarthome.app.model.Function;
import de.smarthome.app.model.Location;
import de.smarthome.app.model.configs.Boundary;
import de.smarthome.app.model.configs.BoundaryDataPoint;
import de.smarthome.app.repository.Repository;

public class RegulationViewModel extends AndroidViewModel {
    private  final String TAG = "RegulationViewModel";
    private   Repository repository;

    public RegulationViewModel(@NonNull Application application)  {
        super(application);

        repository = Repository.getInstance(application);
    }


    public void requestSetValue(String id, String value){
        repository.requestSetValue(id, value);
    }

    public LiveData<Map<Datapoint, Datapoint>> getDataPoints(){
        return repository.getDataPoints();
    }

    public LiveData<Map<String, String>> getStatusList(){
        return repository.getStatusList();
    }

    public Function getSelectedFunction(){
        return repository.getSelectedFunction();
    }

    //TODO: Refactor
    public LiveData<Map<Datapoint, BoundaryDataPoint>> getTest(){
        return  repository.getTest();
    }
}
