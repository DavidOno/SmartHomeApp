package de.smarthome.server.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.List;

import de.smarthome.R;
import de.smarthome.command.AsyncCommand;
import de.smarthome.command.Command;
import de.smarthome.command.gira.HomeServerCommandInterpreter;
import de.smarthome.command.impl.ChangeValueCommand;
import de.smarthome.command.impl.CheckAvailabilityCommand;
import de.smarthome.command.impl.GetValueCommand;
import de.smarthome.command.impl.RegisterCallback;
import de.smarthome.command.impl.UIConfigCommand;
import de.smarthome.server.ServerHandler;
import de.smarthome.server.gira.GiraServerHandler;

public class ServerTestActivity extends AppCompatActivity {

    private static final Logger LOG = LoggerFactory.getLogger(ServerTestActivity.class.getSimpleName());

    private ServerHandler sh = new GiraServerHandler(new HomeServerCommandInterpreter());
    private Button testAvailability;
    private Button register;
    private Button getUIConfig;
    private Button getValue;
    private Button setValue;
    private Button registerCallbackButton;
    private Button showDeviceIPs;
    private EditText id;
    private EditText value;
    private TextView displayText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LOG.debug("Hello from {} saved instance state {}", this, savedInstanceState);
        setContentView(R.layout.activity_server_test);

        getViewsById();
        testAvailability.setOnClickListener(v -> {
            new Thread(() -> {
                Command availability = new CheckAvailabilityCommand();
                sh.sendRequest(availability);
            }).start();
        });

        register.setOnClickListener(v -> {
            Toast.makeText(this, "is not supported", Toast.LENGTH_LONG).show();
        });

        getUIConfig.setOnClickListener(v -> {
            new Thread(() -> {
                Command getConfig = new UIConfigCommand();
                sh.sendRequest(getConfig);
            }).start();
        });

        getValue.setOnClickListener(v -> {
            new Thread(() -> {
                Command getValueCommand = new GetValueCommand(id.getText().toString());
                sh.sendRequest(getValueCommand);
            }).start();
        });

        setValue.setOnClickListener(v -> {
            new Thread(() -> {
                Command setValueCommand = new ChangeValueCommand(id.getText().toString(), Integer.parseInt(value.getText().toString()));
                sh.sendRequest(setValueCommand);
            }).start();
        });

        registerCallbackButton.setOnClickListener(v -> {
            new Thread(() -> {
                AsyncCommand register = new RegisterCallback(":8443");//TODO: change to correct ip:port
                sh.sendRequest(register);
            }).start();
        });

        showDeviceIPs.setOnClickListener(v -> {
            new Thread(() -> {
                Log.d("Main", "Started scanning IPs");
                new GiraServerHandler(null).showReachableInetAdresses(getApplicationContext());
            }).start();
        });

        Log.d("Main", "Started");
        Toast.makeText(this, "Started", Toast.LENGTH_SHORT).show();
    }

    private void getViewsById() {
        testAvailability = findViewById(R.id.availability);
        register = findViewById(R.id.register);
        getUIConfig = findViewById(R.id.uiconfig);
        setValue = findViewById(R.id.setvalue);
        getValue = findViewById(R.id.getvalue);
        id = findViewById(R.id.id);
        value = findViewById(R.id.value);
        displayText = findViewById(R.id.display);
        registerCallbackButton = findViewById(R.id.register_callback_button);
        showDeviceIPs = findViewById(R.id.showDeviceIps);
    }
}