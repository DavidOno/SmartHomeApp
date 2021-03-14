package de.smarthome.model.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class OptionsViewModel extends AndroidViewModel {
    private  final String TAG = "OptionsViewModel";

    public OptionsViewModel(@NonNull Application application) {
        super(application);
    }
}
