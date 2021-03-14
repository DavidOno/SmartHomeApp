package de.smarthome;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.fragment.NavHostFragment;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SmartHomeApplication extends AppCompatActivity {
    public static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(4);

    private static final int PERMISSION_REQUEST_FINE_LOCATION = 1;
    private static final int PERMISSION_REQUEST_BACKGROUND_LOCATION = 2;

    private NavController navController;
    private NavHostFragment navHostFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkBeaconPermissions();

        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = NavHostFragment.findNavController(navHostFragment);
        selectStartFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void selectStartFragment() {
        NavInflater navInflater = navController.getNavInflater();
        NavGraph graph = navInflater.inflate(R.navigation.nav_graph);

        /*if (navHostFragment.getChildFragmentManager().getBackStackEntryCount() == 0) {
            if (dummyToken.isEmpty()) {
                graph.setStartDestination(R.id.loginFragment);

            } else {
                graph.setStartDestination(R.id.roomsFragment);
            }
        }*/

        graph.setStartDestination(R.id.loginFragment);
        navController.setGraph(graph);

    }

    public void checkBeaconPermissions(){
        if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (this.checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
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
