package de.smarthome.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.auth.api.credentials.Credential;

import de.smarthome.R;
import de.smarthome.app.viewmodel.LoginViewModel;
import de.smarthome.app.utility.ToastUtility;

/**
 * This fragment handles the login of the user at the initial start of the application
 */
public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";
    private LoginViewModel viewModel;
    private EditText editTextUserName;
    private EditText editTextPwd;
    private Button buttonLogin;

    //TODO: Remove at the end of testing
    private Button buttonDummy;

    private String userName;
    private String password;

    private ToastUtility toastUtility;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        findViewsByID(view);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity().setTitle(R.string.title_login_fragment);

        buttonLogin.setOnClickListener(v -> registerNewUser());

        //TODO: Remove at the end of testing
        buttonDummy.setOnClickListener(v -> navigateToHomeOverviewFragment());

        setLoginStatusObserver();
    }

    private void setLoginStatusObserver() {
        viewModel.getLoginStatus().observe(this.getViewLifecycleOwner(), requestStatus -> {
            if(requestStatus){
                viewModel.saveCredential(this.getActivity(), buildCredential(userName, password));
                navigateToHomeOverviewFragment();
            }else{
                if(userName != null && password != null)
                    toastUtility.prepareToast("Login Data incorrect!");
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        toastUtility = ToastUtility.getInstance();
    }

    private Credential buildCredential(String userName, String pwd) {
        if(userName == null || pwd == null){
            throw new IllegalArgumentException();
        }
        return new Credential.Builder(userName)
                .setPassword(pwd)
                .build();
    }

    private boolean getCredentialsFromUI(){
        userName = editTextUserName.getText().toString();
        password = editTextPwd.getText().toString();

        if(userName.isEmpty() || password.isEmpty()){
            toastUtility.prepareToast("Username or Password is empty!");
            return false;
        }
        return true;
    }

    private void registerNewUser(){
        if(getCredentialsFromUI()){
            viewModel.registerUser(buildCredential(userName, password));
        }
    }

    private void navigateToHomeOverviewFragment() {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(LoginFragmentDirections.actionLoginFragmentToHomeOverviewFragment());
    }

    private void findViewsByID(View view) {
        editTextUserName = view.findViewById(R.id.editText_username);
        editTextPwd = view.findViewById(R.id.editText_password);
        buttonLogin = view.findViewById(R.id.button_login);

        //TODO: Remove at the end of testing
        buttonDummy = view.findViewById(R.id.button_dummy);
    }
}