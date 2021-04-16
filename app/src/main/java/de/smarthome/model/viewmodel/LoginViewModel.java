package de.smarthome.model.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.CredentialRequestResponse;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import de.smarthome.model.repository.Repository;

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
