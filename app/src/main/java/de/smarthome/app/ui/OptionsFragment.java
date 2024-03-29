package de.smarthome.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import de.smarthome.R;
import de.smarthome.app.viewmodel.OptionsViewModel;

/**
 * This fragment contains two buttons to allow the user to logout and to restart the connections to the servers
 */
public class OptionsFragment extends Fragment {
    private static final String TAG = "OptionsFragment";
    private OptionsViewModel viewModel;
    private Button buttonLogout;
    private Button buttonConnectionRestart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_options, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViewsByID(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity().setTitle(R.string.title_options_fragment);
        buttonLogout.setOnClickListener(v -> logoutUser());
        buttonConnectionRestart.setOnClickListener(v -> retryConnectionToServer());

        viewModel = new ViewModelProvider(requireActivity()).get(OptionsViewModel.class);
    }

    private void findViewsByID(View view) {
        buttonLogout = view.findViewById(R.id.button_logout);
        buttonConnectionRestart = view.findViewById(R.id.button_restart_connection);
    }

    private void logoutUser(){
        viewModel.getDataFromGoogleAndDelete();
        navigateToLoginFragment();
    }

    private void retryConnectionToServer(){
        viewModel.restartConnectionToServer();
    }

    private void navigateToLoginFragment() {
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.loginFragment);
    }
}