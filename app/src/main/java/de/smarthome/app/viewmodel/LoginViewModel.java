package de.smarthome.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.google.android.gms.auth.api.credentials.Credential;

import de.smarthome.app.repository.Repository;

public class LoginViewModel extends AndroidViewModel {
    private Repository repository;

    private final String TAG = "LoginViewModel";

    public LoginViewModel(@NonNull Application application) {
        super(application);

        repository = Repository.getInstance(application);
    }

    public void registerUser(Credential userCredential) {
        repository.requestRegisterUser(userCredential);
    }

}
