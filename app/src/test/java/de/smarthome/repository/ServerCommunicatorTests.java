package de.smarthome.repository;

import android.app.Application;


import com.google.android.gms.auth.api.credentials.Credential;

import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.smarthome.app.model.UIConfig;
import de.smarthome.app.model.responses.AvailabilityResponse;
import de.smarthome.app.model.responses.RegisterResponse;
import de.smarthome.app.repository.ServerCommunicator;
import de.smarthome.server.gira.GiraServerHandler;

import static org.mockito.Mockito.mock;


//@RunWith(MockitoJUnitRunner.class)
public class ServerCommunicatorTests {

    //assert that RRCheckAvailability
    //assert that RRClient
    //assert that RRGiraCallBackServer
    //assert that RRCallbackServer
    //assert that RR for UI && Channel && Beacon && Boundary
    // are called
    public void initialisationOfApplication(){
        GiraServerHandler gh = mock(GiraServerHandler.class);
        ServerCommunicator sc = new ServerCommunicator(gh);
        Credential credential = new Credential.Builder("userName")
                .setPassword("pwd")
                .build();

        //sc.initialisationOfApplication(credential);
    }

    //assert that RR UI
    public void requestUIConfigAfterRestart(){}

    //assert that RR Channel && Beacon && Boundary
    public void requestAdditionalConfigsAfterRestart(){}

    //assert that ChangeValueCommand
    public void requestSetValue(){}

    //assert that sc.handleResponseGetValue()
    public void requestGetValue(){}

    //        requestUnregisterClient(IP_OF_CALLBACK_SERVER);
    //        requestUnregisterCallbackServerAtGiraServer();
    public void unsubscribeFromEverything(){}

    //sc.initialisationOfApplicationAfterRestart()
    //toast
    public void getSavedCredentialsForLoginAfterRestart(){}
}
