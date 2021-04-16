package de.smarthome.model.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.Map;

import de.smarthome.model.configs.ChannelDatapoint;
import de.smarthome.model.impl.Datapoint;
import de.smarthome.model.impl.Function;
import de.smarthome.model.impl.Location;
import de.smarthome.model.repository.Repository;

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
