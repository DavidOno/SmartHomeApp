package de.smarthome.app.viewmodel;

import android.app.Application;
import android.widget.Toast;

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
    private Repository repository;
    private ToastUtility toastUtility;

    public OptionsViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance(application);
        toastUtility = ToastUtility.getInstance();
    }

    public void deleteCredential(Credential credential){
        CredentialsClient credentialsClient = Credentials.getClient(getApplication());

        credentialsClient.delete(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                toastUtility.prepareToast("Login data deleted");
            }
        });

    }

    public void getDataFromGoogleAndDelete() {
        CredentialRequest credentialRequest = new CredentialRequest.Builder()
                .setPasswordLoginSupported(true)
                .build();

        CredentialsClient credentialsClient = Credentials.getClient(getApplication());

        credentialsClient.request(credentialRequest).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                // See "Handle successful credential requests"
                deleteCredential(task.getResult().getCredential());
            }else{
                // See "Handle unsuccessful and incomplete credential requests"
                toastUtility.prepareToast("Not able to retrieve Login data");
            }
        });
    }
}
