package de.smarthome.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsClient;

import de.smarthome.app.repository.Repository;
import de.smarthome.app.utility.ToastUtility;

public class OptionsViewModel extends AndroidViewModel {
    private static final String TAG = "OptionsViewModel";
    private ToastUtility toastUtility;
    private Repository repository;

    public OptionsViewModel(@NonNull Application application) {
        super(application);
        toastUtility = ToastUtility.getInstance();
        repository = Repository.getInstance(application);
    }

    private void deleteCredential(Credential credential){
        CredentialsClient credentialsClient = Credentials.getClient(getApplication());
        credentialsClient.delete(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                toastUtility.prepareToast("Login data deleted.");
            }
        });

    }

    public void getDataFromGoogleAndDelete() {
        repository.setLoginStatus(false);
        CredentialRequest credentialRequest = new CredentialRequest.Builder()
                .setPasswordLoginSupported(true)
                .build();

        CredentialsClient credentialsClient = Credentials.getClient(getApplication());
        credentialsClient.request(credentialRequest).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                deleteCredential(task.getResult().getCredential());
            }else{
                toastUtility.prepareToast("Not able to retrieve Login data.");
            }
        });
    }
}