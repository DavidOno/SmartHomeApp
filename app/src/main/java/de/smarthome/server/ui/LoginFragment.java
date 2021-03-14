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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import de.smarthome.R;
import de.smarthome.model.viewmodel.LoginViewModel;


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

    private CredentialsClient credentialsClient;
    private Credential userCredential;
    private boolean gotCredential = false;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        findViewsByID(view);
        credentialsClient = Credentials.getClient(this.getActivity());
        //checkForSavedCredential();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        buttonLogin.setOnClickListener(v -> loginUser());
        //buttonReset.setOnClickListener(v -> deleteCredential(getSavedCredential()));
        buttonDummy.setOnClickListener(v -> navigateToRoomsFragment());

        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
    }

    private boolean getCredentialsFromUI(){
        userName = editTextUserName.getText().toString();
        password = editTextPwd.getText().toString();

        if(userName.isEmpty() || password.isEmpty()){
            Toast.makeText(getActivity(), "Username or Password is empty!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void loginUser(){
        if(getCredentialsFromUI()){
            //loginViewModel.registerUser(userName, password);
            loginViewModel.registerUser("?????", "?????");
            //saveCredential(userName, password);
            navigateToRoomsFragment();
        }
    }

    private void autoLoginUser(String savedUserName, String savedPassword){
        loginViewModel.registerUser(savedUserName, savedPassword);
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

    public void saveCredential(String userName, String password){
        userCredential = new Credential.Builder(userName)
                //.setAccountType("GIRA") Account type must be a valid Http/Https URI
                .setPassword(password)
                .build();

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

                    ResolvableApiException rae = (ResolvableApiException) e;
                    try {
                        //can not start in VM
                        rae.startResolutionForResult(getActivity(), 1);

                    } catch (IntentSender.SendIntentException exception) {
                        Log.e(TAG, "Failed to send resolution.", exception);
                        Toast.makeText(getActivity(), "Save failed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Save failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void deleteCredential(Credential credential){
        credentialsClient.delete(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Login data deleted", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void onCredentialRetrieved(Credential credential) {
        String accountType = credential.getAccountType();
        if (accountType == null) {
            Log.d(TAG, "onCredentialRetrieved username: " + credential.getId() + " password " + credential.getPassword());
            gotCredential = true;
            autoLoginUser(credential.getId(), credential.getPassword());
        }
    }
}