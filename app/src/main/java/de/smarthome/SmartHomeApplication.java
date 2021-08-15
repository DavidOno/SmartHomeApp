package de.smarthome;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsClient;
import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.smarthome.app.model.Location;
import de.smarthome.app.ui.HomeOverviewFragment;
import de.smarthome.app.ui.LoginFragment;
import de.smarthome.app.ui.OptionsFragment;
import de.smarthome.app.ui.RegulationFragment;
import de.smarthome.app.ui.RoomOverviewFragment;
import de.smarthome.app.utility.ToastUtility;
import de.smarthome.app.viewmodel.SmartHomeApplicationViewModel;

/**
 * This is the hostactivity of the entire application. It host the fragment container that displays the fragments
 * and a threadpool executorservice. It also handles the observers of events that are important during the entire runtime of the app.
 */
public class SmartHomeApplication extends AppCompatActivity {
    public static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(4);

    private static final int PERMISSION_REQUEST_FINE_LOCATION = 1;
    private static final int PERMISSION_REQUEST_BACKGROUND_LOCATION = 2;

    private NavController navController;
    private NavHostFragment navHostFragment;

    private SmartHomeApplicationViewModel viewModel;
    private ToastUtility toastUtility;

    private boolean beaconDialogShown;
    private boolean connectionSnackbarShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        checkBeaconPermissions();
        beaconDialogShown = false;
        connectionSnackbarShown = false;

        viewModel = new ViewModelProvider(this).get(SmartHomeApplicationViewModel.class);
        toastUtility = ToastUtility.getInstance();

        setNewToastObserver();
        setBeaconObserver();
        setServerConnectionObserver();

        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = NavHostFragment.findNavController(navHostFragment);

        if(savedInstanceState == null){
            getSavedCredentials();
        }
    }

    private void setServerConnectionObserver() {
        viewModel.getServerConnectionStatus().observe(this, aBoolean -> {
            Fragment currentFragment = navHostFragment.getChildFragmentManager().getFragments().get(0);
            if(!aBoolean && !connectionSnackbarShown
                    && !currentFragment.getClass().equals(LoginFragment.class)){
                connectionSnackbarShown = true;
                showConnectionSnackbar();
            }
        });
    }

    private void setBeaconObserver() {
        viewModel.checkBeacon().observe(this, aBoolean -> {
            if(aBoolean && !beaconDialogShown){
                    viewModel.setBeaconCheckFalse();
                    beaconDialogShown = true;
                    showBeaconSnackbar(viewModel.getBeaconLocation());
                }
        });
    }

    private void setNewToastObserver() {
        toastUtility.getNewToast().observe(this, aBoolean -> {
            if(aBoolean){
                Toast.makeText(getApplicationContext(), toastUtility.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showBeaconSnackbar(Location location) {
        LinearLayout usedLayout = findViewById(R.id.smartHomeApplicationLinearLayout);
        int snackBarDuration = 20000;
        Snackbar snackbar = Snackbar.make(usedLayout,
                "Switch to " + location.getName(), snackBarDuration)
                .setAction("ACCEPT", v -> {
                    Snackbar resultMessageSnackbar = Snackbar.make(usedLayout, "Switch successful", Snackbar.LENGTH_LONG);
                    resultMessageSnackbar.show();

                    viewModel.confirmBeacon();
                    goToRoomFragment();
                });
        snackbar.show();
        beaconDialogShown = false;
    }

    private void showConnectionSnackbar() {
        LinearLayout usedLayout = findViewById(R.id.smartHomeApplicationLinearLayout);
        Snackbar snackbar = Snackbar.make(usedLayout,
                "Connection to Server failed.", Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", v -> {
                    Snackbar resultMessageSnackbar = Snackbar.make(usedLayout, "Trying to connect to Server...", Snackbar.LENGTH_LONG);
                    resultMessageSnackbar.show();

                    viewModel.retryConnectionToServerAfterFailure();
                    connectionSnackbarShown = false;
                })
                .setActionTextColor(Color.RED);
        snackbar.show();
    }

    @Override
    protected void onDestroy() {
        viewModel.unsubscribeFromEverything();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_options, menu);
        getMenuInflater().inflate(R.menu.menu_home_overview, menu);
        return true;
    }

    /**
     * Enables and disables menu items depending on the currently displayed fragment
     * @param menu Menu containing the menu items
     * @return true
     */
    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        Fragment currentFragment = navHostFragment.getChildFragmentManager().getFragments().get(0);

        if (currentFragment.getClass().equals(LoginFragment.class)){
            menu.getItem(0).setEnabled(false);
            menu.getItem(1).setEnabled(false);
        }else if(currentFragment.getClass().equals(OptionsFragment.class)){
            menu.getItem(0).setEnabled(false);
            menu.getItem(1).setEnabled(true);
        }else if(currentFragment.getClass().equals(HomeOverviewFragment.class)){
            menu.getItem(0).setEnabled(true);
            menu.getItem(1).setEnabled(false);
        }else{
            menu.getItem(0).setEnabled(true);
            menu.getItem(1).setEnabled(true);
        }
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home_overview:
                viewModel.setSelectedLocation(null);
                goToFragment(R.id.action_home_overview);
                return true;

            case R.id.action_settings:
                goToFragment(R.id.action_settings);
                return true;

            case android.R.id.home:
                this.onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startBeaconDialog(Location newLocation){
        Dialog dialog = new Dialog(SmartHomeApplication.this);
        dialog.setContentView(R.layout.dialog_beacon);

        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();

        TextView roomName = dialog.findViewById(R.id.text_view_room_name);
        roomName.setText(newLocation.getName());

        Button buttonYes = dialog.findViewById(R.id.button_left);
        Button buttonNo = dialog.findViewById(R.id.button_right);

        buttonYes.setOnClickListener(v -> {
            dialog.dismiss();
            viewModel.confirmBeacon();
            setStartFragment(R.id.roomOverviewFragment);
            beaconDialogShown = false;
        });

        buttonNo.setOnClickListener(v -> {
            dialog.dismiss();
            beaconDialogShown = false;
        });
    }

    private void goToRoomFragment() {
        Fragment currentFragment = navHostFragment.getChildFragmentManager().getFragments().get(0);

        if (currentFragment.getClass().equals(HomeOverviewFragment.class)){
            navController.navigate(R.id.action_homeOverviewFragment_to_roomOverviewFragment);

        }else if(currentFragment.getClass().equals(RegulationFragment.class)){
            navController.navigate(R.id.action_regulationFragment_to_roomOverviewFragment);

        }else if(currentFragment.getClass().equals(OptionsFragment.class) ){
            navController.navigate(R.id.action_optionsFragment_to_roomOverviewFragment);
        }
    }


    private void setStartFragment(int destinationFragment) {
        NavInflater navInflater = navController.getNavInflater();
        NavGraph graph = navInflater.inflate(R.navigation.nav_graph);

        graph.setStartDestination(destinationFragment);
        navController.setGraph(graph);
    }

    private void goToFragment(int pressedMenuItem){
        Fragment currentFragment = navHostFragment.getChildFragmentManager().getFragments().get(0);

        if (currentFragment.getClass().equals(HomeOverviewFragment.class) && pressedMenuItem == R.id.action_settings){
            navController.navigate(R.id.action_homeOverviewFragment_to_optionsFragment);

        }else if(currentFragment.getClass().equals(RoomOverviewFragment.class)){
            if(pressedMenuItem == R.id.action_settings){
                navController.navigate(R.id.action_roomOverviewFragment_to_optionsFragment);
            }else if(pressedMenuItem == R.id.action_home_overview){
                navController.navigate(R.id.action_roomOverviewFragment_to_HomeOverviewFragment);
            }

        }else if(currentFragment.getClass().equals(RegulationFragment.class)){
            if(pressedMenuItem == R.id.action_settings){
                navController.navigate(R.id.action_regulationFragment_to_optionsFragment);
            }else if(pressedMenuItem == R.id.action_home_overview){
                navController.navigate(R.id.action_regulationFragment_to_HomeOverviewFragment);
            }

        }else if(currentFragment.getClass().equals(OptionsFragment.class) && pressedMenuItem == R.id.action_home_overview){
            navController.navigate(R.id.action_optionsFragment_to_HomeOverviewFragment);
        }
    }

    private void getSavedCredentials() {
        Thread getSavedCredentialsFromGoogleThread = new Thread(() -> {
            CredentialRequest credentialRequest = new CredentialRequest.Builder()
                    .setPasswordLoginSupported(true)
                    .build();

            CredentialsClient credentialsClient = Credentials.getClient(this);
            credentialsClient.request(credentialRequest).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    onCredentialRetrieved(task.getResult().getCredential());
                } else {
                    setStartFragment(R.id.loginFragment);
                }
            });
        });
        EXECUTOR_SERVICE.execute(getSavedCredentialsFromGoogleThread);
    }

    private void onCredentialRetrieved(Credential credential) {
        String accountType = credential.getAccountType();
        if (accountType == null) {
            viewModel.requestRegisterUser(credential);
            setStartFragment(R.id.HomeOverviewFragment);
        }
    }

    private void checkBeaconPermissions(){
        if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                    this.checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                if (!this.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("This app needs background location access");
                    builder.setMessage("Please grant location access so this app can detect beacons in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @TargetApi(23)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                                    PERMISSION_REQUEST_BACKGROUND_LOCATION);
                        }

                    });
                    builder.show();
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since background location access has not been granted, this app will not be able to discover beacons in the background.  Please go to Settings -> Applications -> Permissions and grant background location access to this app.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(dialog -> {
                    });
                    builder.show();
                }
            }
        } else {
            if (!this.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                        PERMISSION_REQUEST_FINE_LOCATION);
            } else {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Functionality limited");
                builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons.  Please go to Settings -> Applications -> Permissions and grant location access to this app.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(dialog -> {
                });
                builder.show();
            }
        }
    }
}