package de.smarthome.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsClient;

import de.smarthome.R;
import de.smarthome.app.utility.ToastUtility;

public class OptionsFragment extends Fragment {
    private static final String TAG = "OptionsFragment";

    private Button buttonLogout;

    private final ToastUtility toastUtility = ToastUtility.getInstance();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_options, container, false);

        findViewsByID(view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity().setTitle("Option");

        buttonLogout.setOnClickListener(v -> logoutUser());
    }


    private void findViewsByID(View view) {
        buttonLogout = view.findViewById(R.id.button_logout);
    }

    private void logoutUser(){
        getSavedCredentialsFromGoogle();
    }

    private void navigateToLoginFragment() {
        NavController navController = NavHostFragment.findNavController(this);

        navController.navigate(R.id.loginFragment);
    }

    public void deleteCredential(Credential credential){
        CredentialsClient credentialsClient = Credentials.getClient(this.getActivity());

        credentialsClient.delete(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                toastUtility.prepareToast("Login data deleted");
            }
        });

    }

    public void getSavedCredentialsFromGoogle() {
        CredentialRequest credentialRequest = new CredentialRequest.Builder()
                .setPasswordLoginSupported(true)
                .build();

        CredentialsClient credentialsClient = Credentials.getClient(this.getActivity());

        credentialsClient.request(credentialRequest).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                // See "Handle successful credential requests"
                deleteCredential(task.getResult().getCredential());
                navigateToLoginFragment();
            }else{
                // See "Handle unsuccessful and incomplete credential requests"
                toastUtility.prepareToast("Not able to retrieve Login data");
            }
        });
    }
}