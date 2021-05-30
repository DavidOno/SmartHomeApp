package de.smarthome.app.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import de.smarthome.R;
import de.smarthome.command.Command;
import de.smarthome.command.gira.HomeServerCommandInterpreter;
import de.smarthome.command.impl.ChangeValueCommand;
import de.smarthome.command.impl.CheckAvailabilityCommand;
import de.smarthome.command.impl.GetValueCommand;
import de.smarthome.command.impl.RegisterCallbackServerAtGiraServer;
import de.smarthome.command.impl.RegisterClientCommand;
import de.smarthome.command.impl.UIConfigCommand;
import de.smarthome.command.impl.UnRegisterCallbackServerAtGiraServer;
import de.smarthome.server.NoSSLRestTemplateCreater;
import de.smarthome.server.ServerHandler;
import de.smarthome.server.gira.GiraServerHandler;

public class ServerTestActivity extends AppCompatActivity {

    private static final String USERNAME = "";
    private static final String PWD = "";
    private static final String ipOfCallbackServer = "127.0.0.1"; //192.168.132.211:8443
    private ServerHandler sh = new GiraServerHandler(new HomeServerCommandInterpreter(new NoSSLRestTemplateCreater()));
    private Button testAvailability;
    private Button register;
    private Button getUIConfig;
    private Button getValue;
    private Button setValue;
    private Button registerCallbackButton;
    private Button rcah;
    private Button showDeviceIPs;
    private EditText id;
    private EditText value;
    private TextView displayText;
    private boolean registerToogle = false;
    private boolean registerCAHToogle = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_test);

        getViewsById();
        testAvailability.setOnClickListener(v -> {
            new Thread(() -> {
                Command availability = new CheckAvailabilityCommand();
                sh.sendRequest(availability);
            }).start();
        });

        register.setOnClickListener(v -> {
            new Thread(() -> {
                Command register = new RegisterClientCommand(USERNAME, PWD);
                sh.sendRequest(register);
            }).start();
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

        /*registerCallbackButton.setOnClickListener(v -> {
            if(!registerToogle) {
                new Thread(() -> {
                    AsyncCommand register = new RegisterCallback(ipOfCallbackServer);
                    Command channelConfig = new AdditionalConfigCommand(ipOfCallbackServer, AdditionalConfigs.CHANNEL);
                    Command locationConfig = new AdditionalConfigCommand(ipOfCallbackServer, AdditionalConfigs.LOCATION);
                    sh.sendRequest(new CommandChainImpl().add(register).add(channelConfig).add(locationConfig));
                }).start();
                registerToogle = !registerToogle;
                registerCallbackButton.setText("Unregister Callback");
            }else{
                new Thread(() -> {
                    AsyncCommand register = new UnRegisterCallback(ipOfCallbackServer);//TODO: change to correct ip:port
                    sh.sendRequest(register);
                }).start();
                registerToogle = !registerToogle;
                registerCallbackButton.setText("Register Callback");
            }
        });*/

        rcah.setOnClickListener(v -> {
            if(!registerCAHToogle) {
                new Thread(() -> {
                    Command register = new RegisterCallbackServerAtGiraServer(ipOfCallbackServer);
                    sh.sendRequest(register);
                }).start();
                registerCAHToogle = !registerCAHToogle;
                rcah.setText("URCAH");
            }else{
                new Thread(() -> {
                    Command register = new UnRegisterCallbackServerAtGiraServer();
                    sh.sendRequest(register);
                }).start();
                registerCAHToogle = !registerCAHToogle;
                rcah.setText("RCAH");
            }
        });

        showDeviceIPs.setOnClickListener(v -> {
//            new Thread(() -> {
//                Log.d("Main", "Started scanning IPs");
//                new GiraServerHandler(null).showReachableInetAdresses(getApplicationContext());
//            }).start();
            //is not required anymore
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
        //displayText = findViewById(R.id.display);
        registerCallbackButton = findViewById(R.id.register_callback_button);
        showDeviceIPs = findViewById(R.id.showDeviceIps);
        rcah = findViewById(R.id.registerCallbackAtHomeServer);
    }
}