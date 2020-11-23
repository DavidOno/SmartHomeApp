package de.smarthome.server.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

import de.smarthome.R;
import de.smarthome.command.Command;
import de.smarthome.command.gira.HomeServerCommandInterpreter;
import de.smarthome.command.impl.ChangeValueCommand;
import de.smarthome.command.impl.CheckAvailabilityCommand;
import de.smarthome.command.impl.GetValueCommand;
import de.smarthome.command.impl.UIConfigCommand;
import de.smarthome.server.ServerHandler;
import de.smarthome.server.gira.GiraServerHandler;

public class ServerTestActivity extends AppCompatActivity {

    private ServerHandler sh = new GiraServerHandler(new HomeServerCommandInterpreter());
    private Button testAvailability;
    private Button register;
    private Button getUIConfig;
    private Button getValue;
    private Button setValue;
    private TextView id;
    private TextView value;
    private TextView displayText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_test);

        getViewsById();
        testAvailability.setOnClickListener(v -> {
            Command availability = new CheckAvailabilityCommand();
            sh.sendRequest(availability);
        });

        register.setOnClickListener(v -> {
            Toast.makeText(this, "is not supported", Toast.LENGTH_LONG).show();
        });

        getUIConfig.setOnClickListener(v -> {
            Command getConfig = new UIConfigCommand();
            sh.sendRequest(getConfig);
        });

        getValue.setOnClickListener(v -> {
            Command getValueCommand = new GetValueCommand(UUID.fromString(id.getText().toString()));
            sh.sendRequest(getValueCommand);
        });

        setValue.setOnClickListener(v -> {
            Command setValueCommand = new ChangeValueCommand(UUID.fromString(id.getText().toString()), Integer.parseInt(value.getText().toString()));
            sh.sendRequest(setValueCommand);
        });

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
    }
}