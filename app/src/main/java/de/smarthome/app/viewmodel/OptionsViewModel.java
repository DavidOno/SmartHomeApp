package de.smarthome.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import de.smarthome.app.repository.Repository;

public class OptionsViewModel extends AndroidViewModel {
    private  final String TAG = "OptionsViewModel";
    private Repository repository;

    public OptionsViewModel(@NonNull Application application) {
        super(application);

        repository = Repository.getInstance(application);
    }
}
