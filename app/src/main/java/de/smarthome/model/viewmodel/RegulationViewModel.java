package de.smarthome.model.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import de.smarthome.model.impl.Function;
import de.smarthome.model.repository.Repository;

public class RegulationViewModel extends AndroidViewModel {
    private  final String TAG = "RegulationViewModel";
    private Repository repository;

    public RegulationViewModel(@NonNull Application application)  {
        super(application);

        repository = Repository.getInstance();
    }

    public Function getFunctionAtPosition(String roomName, int position){
        return repository.getRoomIDToIDs().get(roomName).get(position);
    }

    public void requestSetValue(String ID, String value){
        repository.requestSetValue(ID, value);
    }

    public Function getFunctionByUID(String UID){
        return repository.getFunctionByUID(UID);
    }
}
