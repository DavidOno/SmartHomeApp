package de.smarthome;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.CredentialRequestResponse;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.smarthome.app.model.Location;
import de.smarthome.app.repository.Repository;
import de.smarthome.app.ui.LoginFragment;
import de.smarthome.app.utility.ToastUtility;

public class SmartHomeApplication extends AppCompatActivity {
    public static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(4);

    private static final int PERMISSION_REQUEST_FINE_LOCATION = 1;
    private static final int PERMISSION_REQUEST_BACKGROUND_LOCATION = 2;

    private NavController navController;
    private NavHostFragment navHostFragment;

    private Repository repository;
    private ToastUtility toastUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkExternalStorage();

        setContentView(R.layout.activity_main);

        checkBeaconPermissions();
        repository = Repository.getInstance(this.getApplication());

        toastUtility = ToastUtility.getInstance();
        toastUtility.getNewToast().observe(this, aBoolean -> {
            if(aBoolean){
                Toast.makeText(getApplicationContext(), toastUtility.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        repository.checkBeacon().observe(this, aBoolean -> {
            if(aBoolean){
                repository.initBeaconCheck();
                startBeaconDialog(repository.getBeaconLocation());
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //TODO: Find a way to stop it from beeing cut or just remove it
        getSupportActionBar().setSubtitle("SmartHome");

        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = NavHostFragment.findNavController(navHostFragment);

        if(savedInstanceState == null){
            getSavedCredentials();
        }
    }

    private void checkExternalStorage() {
        if ( isExternalStorageWritable() ) {

            File appDirectory = new File( Environment.getExternalStorageDirectory() + "/MyPersonalAppFolder" );
            File logDirectory = new File( appDirectory + "/logs" );
            File logFile = new File( logDirectory, "logcat_" + System.currentTimeMillis() + ".txt" );

            // create app folder
            if ( !appDirectory.exists() ) {
                appDirectory.mkdir();
            }

            // create log folder
            if ( !logDirectory.exists() ) {
                logDirectory.mkdir();
            }

            // clear the previous logcat and then write the new one to the file
            try {
                Process process = Runtime.getRuntime().exec("logcat -c");
                process = Runtime.getRuntime().exec("logcat -f " + logFile);
            } catch ( IOException e ) {
                e.printStackTrace();
            }

        } else if ( isExternalStorageReadable() ) {
            // only readable
        } else {
            // not accessible
        }
    }

    //TODO: TO DELETE!!!
    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if ( Environment.MEDIA_MOUNTED.equals( state ) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals( state ) ) {
            return true;
        }
        return false;
    }

    //TODO: TO DELETE!!!
    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if ( Environment.MEDIA_MOUNTED.equals( state ) ) {
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        repository.unsubscribeFromEverything();
        toastUtility.prepareToast("Everything got unsubscribed!");
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_options, menu);
        getMenuInflater().inflate(R.menu.menu_home_overview, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        Fragment x = navHostFragment.getChildFragmentManager().getFragments().get(0);

        if (x.getClass().equals(LoginFragment.class)){
            menu.getItem(0).setEnabled(false);
            menu.getItem(1).setEnabled(false);
        }else{
            menu.getItem(0).setEnabled(true);
            menu.getItem(1).setEnabled(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home_overview:
                repository.setSelectedLocation(null);
                repository.setSelectedFunction(null);
                goToFragment(R.id.HomeOverviewFragment);
                return true;

            case R.id.action_settings:
                goToFragment(R.id.optionsFragment);
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
            repository.confirmBeacon();
            setStartFragment(R.id.roomOverviewFragment);
        });

        buttonNo.setOnClickListener(v -> dialog.dismiss());
    }

    private void setStartFragment(int destinationFragment) {
        NavInflater navInflater = navController.getNavInflater();
        NavGraph graph = navInflater.inflate(R.navigation.nav_graph);

        graph.setStartDestination(destinationFragment);
        navController.setGraph(graph);
    }

    private void goToFragment(int destinationFragment){
        navController.navigate(destinationFragment);
    }

    public void getSavedCredentials() {
        CredentialRequest credentialRequest = new CredentialRequest.Builder()
                .setPasswordLoginSupported(true)
                .build();

        CredentialsClient credentialsClient = Credentials.getClient(this);

        credentialsClient.request(credentialRequest).addOnCompleteListener(new OnCompleteListener<CredentialRequestResponse>() {
            @Override
            public void onComplete(@NonNull Task<CredentialRequestResponse> task) {

                if (task.isSuccessful()) {
                    // See "Handle successful credential requests"
                    onCredentialRetrieved(task.getResult().getCredential());
                }else{
                    // See "Handle unsuccessful and incomplete credential requests"
                    setStartFragment(R.id.loginFragment);
                }
            }
        });
    }

    private void onCredentialRetrieved(Credential credential) {
        String accountType = credential.getAccountType();
        if (accountType == null) {
            repository.requestRegisterUser(credential);
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