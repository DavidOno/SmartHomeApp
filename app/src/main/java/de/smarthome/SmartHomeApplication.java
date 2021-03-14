package de.smarthome;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.fragment.NavHostFragment;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SmartHomeApplication extends AppCompatActivity {

    private NavController navController;
    private NavHostFragment navHostFragment;
    public static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(4);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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
}
