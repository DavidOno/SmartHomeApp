package de.smarthome.app.ui;

import android.app.Activity;
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
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsClient;
import com.google.android.gms.auth.api.credentials.CredentialsOptions;
import com.google.android.gms.common.api.ResolvableApiException;

import de.smarthome.R;
import de.smarthome.app.viewmodel.LoginViewModel;
import de.smarthome.app.utility.ToastUtility;


public class LoginFragment extends Fragment {
    private LoginViewModel loginViewModel;
    private EditText editTextUserName;
    private EditText editTextPwd;
    private Button buttonLogin;

    //TODO: Remove at the end of testing
    private Button buttonDummy;

    private String userName;
    private String password;

    private static final String TAG = "LoginFragment";

    ToastUtility toastUtility;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        findViewsByID(view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity().setTitle("Login");

        buttonLogin.setOnClickListener(v -> registerNewUser());

        //TODO: Remove at the end of testing
        buttonDummy.setOnClickListener(v -> navigateToHomeOverviewFragment());

        loginViewModel.getLoginDataStatus().observe(this.getViewLifecycleOwner(), requestStatus -> {
            if(requestStatus){
                loginViewModel.saveCredential(this.getActivity(), buildCredential(userName, password));
                navigateToHomeOverviewFragment();
            }else{
                toastUtility.prepareToast("Login Data incorrect!");
            }
        });
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        toastUtility = ToastUtility.getInstance();

        test();
    }

    private void test() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                toastUtility.prepareToast("Hello backpress!");
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    public Credential buildCredential(String userName, String pwd) {
        return new Credential.Builder(userName)
                .setPassword(pwd)
                .build();
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

    private void registerNewUser(){
        if(getCredentialsFromUI()){
            loginViewModel.registerUser(buildCredential(userName, password));
        }
    }

    private void navigateToHomeOverviewFragment() {
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_loginFragment_to_homeOverviewFragment);
    }

    private void findViewsByID(View view) {
        editTextUserName = view.findViewById(R.id.editText_username);
        editTextPwd = view.findViewById(R.id.editText_password);
        buttonLogin = view.findViewById(R.id.button_login);

        //TODO: Remove at the end of testing
        buttonDummy = view.findViewById(R.id.button_dummy);

    }
}