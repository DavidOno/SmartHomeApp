package de.smarthome.server.ui;

import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.CredentialRequestResponse;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsClient;
import com.google.android.gms.auth.api.credentials.CredentialsOptions;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import de.smarthome.R;
import de.smarthome.SmartHomeApplication;
import de.smarthome.model.viewmodel.LoginViewModel;
import de.smarthome.utility.ToastUtility;


public class LoginFragment extends Fragment {
    private LoginViewModel loginViewModel;
    private EditText editTextUserName;
    private EditText editTextPwd;
    private Button buttonLogin;
    private Button buttonReset;

    private Button buttonDummy;

    private String userName;
    private String password;

    private final String TAG = "LoginFragment";

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        findViewsByID(view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        buttonLogin.setOnClickListener(v -> registerNewUser());
        //buttonReset.setOnClickListener(v -> deleteCredential(getSavedCredential()));
        buttonDummy.setOnClickListener(v -> navigateToRoomsFragment());


        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);

        getSavedCredentials();
    }

    public Credential buildCredential(String Username, String pwd) {
        Credential credential = new Credential.Builder(Username)
                .setPassword(pwd)
                .build();

        return credential;
    }

    private boolean getCredentialsFromUI(){
        userName = editTextUserName.getText().toString();
        password = editTextPwd.getText().toString();

        if(userName.isEmpty() || password.isEmpty()){
            Toast.makeText(getActivity(), "Username or Password is empty!", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    //TODO: Data needs to be verified before it gets saved!
    private void registerNewUser(){
        if(getCredentialsFromUI()){
            loginViewModel.registerUser(userName, password);
            saveCredential(buildCredential(userName, password));
            navigateToRoomsFragment();
        }
    }

    private void registerExistingUser(String savedUserName, String savedPassword){
        loginViewModel.registerUser(savedUserName, savedPassword);
        navigateToRoomsFragment();
    }


    private void navigateToRoomsFragment() {
        NavController navController = NavHostFragment.findNavController(this);
        NavDirections toRoomsFragment = LoginFragmentDirections.actionLoginFragmentToRoomsFragment();
        navController.navigate(toRoomsFragment);
    }

    private void findViewsByID(View view) {
        editTextUserName = view.findViewById(R.id.editText_username);
        editTextPwd = view.findViewById(R.id.editText_password);
        buttonLogin = view.findViewById(R.id.button_login);

        buttonReset = view.findViewById(R.id.button_reset);

        buttonDummy = view.findViewById(R.id.button_dummy);

    }

    public void saveCredential(Credential userCredential){
        CredentialsOptions options = new CredentialsOptions.Builder()
                .forceEnableSaveDialog()
                .build();

        CredentialsClient credentialsClient = Credentials.getClient(this.getActivity(), options);

        credentialsClient.save(userCredential).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "SAVE: OK");
                    Toast.makeText(getActivity(), "Credentials saved", Toast.LENGTH_SHORT).show();
                    return;
                }

                Exception e = task.getException();
                if (e instanceof ResolvableApiException) {
                    // Try to resolve the save request. This will prompt the user if
                    // the credential is new.
                    ResolvableApiException rae = (ResolvableApiException) e;
                    try {
                        //can not start in VM
                        rae.startResolutionForResult(getActivity(), 1);

                    } catch (IntentSender.SendIntentException exception) {
                        // Could not resolve the request
                        Log.e(TAG, "Failed to send resolution.", exception);
                        Toast.makeText(getActivity(), "Save failed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Request has no resolution
                    Toast.makeText(getActivity(), "Save failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    //not here has to be moved into "OptionsFragment"
    public void deleteCredential(Credential credential){
        CredentialsClient credentialsClient = Credentials.getClient(this.getActivity());

        credentialsClient.delete(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Login data deleted", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void getSavedCredentials() {
        CredentialRequest credentialRequest = new CredentialRequest.Builder()
                .setPasswordLoginSupported(true)
                .build();

        CredentialsClient credentialsClient = Credentials.getClient(this.getActivity());

        credentialsClient.request(credentialRequest).addOnCompleteListener(new OnCompleteListener<CredentialRequestResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<CredentialRequestResponse> task) {

                        if (task.isSuccessful()) {
                            // See "Handle successful credential requests"
                            onCredentialRetrieved(task.getResult().getCredential());
                        }
                        // See "Handle unsuccessful and incomplete credential requests"
                        // ...
                    }
                });
    }

    private void onCredentialRetrieved(Credential credential) {
        String accountType = credential.getAccountType();
        if (accountType == null) {
            //TODO: Remove Log!!
            Log.d(TAG, "Username: " + credential.getId() + ", password: "  + credential.getPassword());
            registerExistingUser(credential.getId(), credential.getPassword());
        }
    }
}