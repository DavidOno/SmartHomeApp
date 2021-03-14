package de.smarthome.model.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import de.smarthome.model.repository.Repository;

public class LoginViewModel extends AndroidViewModel {
    private Repository repository;

    private final String TAG = "LoginViewModel";

    public LoginViewModel(@NonNull Application application) {
        super(application);

        repository = Repository.getInstance();
    }

    public void registerUser(String userName, String pwd) {
        repository.requestRegisterUser(userName, pwd);
    }

}
