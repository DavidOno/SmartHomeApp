package de.smarthome;

import android.app.Application;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SmartHomeApplication extends Application {

    public static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(4);
}
