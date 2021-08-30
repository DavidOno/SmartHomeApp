package de.smarthome.app.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.content.IntentSender;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsClient;
import com.google.android.gms.auth.api.credentials.CredentialsOptions;
import com.google.android.gms.common.api.ResolvableApiException;

import de.smarthome.SmartHomeApplication;
import de.smarthome.app.repository.Repository;
import de.smarthome.app.utility.ToastUtility;

/**
 * This class is the viewmodel of the loginfragment.
 * It handles the communication with the repository and can save the login data.
 */
public class LoginViewModel extends AndroidViewModel {
    private static final String TAG = "LoginViewModel";
    private final ToastUtility toastUtility;
    private final Repository repository;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance();
        toastUtility = ToastUtility.getInstance();
    }

    /**
     * Request to register the user at the gira server.
     * @param userCredential Credentials of the user
     */
    public void registerUser(Credential userCredential) {
        repository.requestRegisterUser(userCredential);
    }

    public LiveData<Boolean> getLoginStatus(){
        return repository.getLoginStatus();
    }

    /**
     * Saves the credentials by the google password manager.
     * @param activity Activity that is required for error handling
     * @param userCredential Credentials that are saved
     */
    public void saveCredential(Activity activity, Credential userCredential){
        Thread saveCredentialByGoogleThread = new Thread(() -> {
            CredentialsOptions options = new CredentialsOptions.Builder()
                    .forceEnableSaveDialog()
                    .build();

            CredentialsClient credentialsClient = Credentials.getClient(activity, options);
            credentialsClient.save(userCredential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "SAVE: OK");
                    toastUtility.prepareToast("Credentials saved.");
                    return;
                }
                Exception e = task.getException();
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException rae = (ResolvableApiException) e;
                    try {
                        rae.startResolutionForResult(activity, 1);
                    } catch (IntentSender.SendIntentException exception) {
                        Log.e(TAG, "Failed to send resolution.", exception);
                        toastUtility.prepareToast("Failed to save Login Data.");
                        e.printStackTrace();
                    }
                } else {
                    toastUtility.prepareToast("Failed to save Login Data.");
                }
            });
        });
        SmartHomeApplication.EXECUTOR_SERVICE.execute(saveCredentialByGoogleThread);
    }
}