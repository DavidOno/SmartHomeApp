package de.smarthome.repository;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.auth.api.credentials.Credential;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.OngoingStubbing;

import java.util.LinkedList;

import javax.inject.Inject;

import de.smarthome.app.model.Function;
import de.smarthome.app.model.Location;
import de.smarthome.app.model.UIConfig;
import de.smarthome.app.model.configs.BoundariesConfig;
import de.smarthome.app.model.configs.ChannelConfig;
import de.smarthome.app.model.responses.CallbackValueInput;
import de.smarthome.app.repository.ConfigContainer;
import de.smarthome.app.repository.Repository;
import de.smarthome.app.repository.ServerCommunicator;
import de.smarthome.beacons.BeaconLocations;
import de.smarthome.server.MyFirebaseMessagingService;
import de.smarthome.server.ServerHandler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

@RunWith(MockitoJUnitRunner.class)
public class RepositoryTests {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private void fillWithDummyValueBeaconConfig() {
        String locationConfigString = "{\n" +
                "\t\"locations\": [\n" +
                "\t\t{\n" +
                "\t\t\t\"roomUID\": \"aafb\",\n" +
                "\t\t\t\"beaconId\": \"7b44b47b-52a1-5381-90c2-f09b6838c5d420\"\n" +
                "\t\t}]}";
        ObjectMapper mapper = new ObjectMapper();
        try {
            ConfigContainer.getInstance().setBeaconLocations(mapper.readValue(locationConfigString, new TypeReference<BeaconLocations>() {}));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // cc.selectedLoc == beaconLocation
    // rep.beaconLocation = null;
    @Test
    public void confirmBeaconLocation(){
        Repository r = Repository.getInstance(null);
        LinkedList<String> fl = new LinkedList<>();
        LinkedList<Location> ll = new LinkedList<>();
        Location location = new Location("l1", "1","dummy", fl, ll,"dummy");
        r.update(location);

        r.confirmBeaconLocation();

        assertThat(r.getSelectedLocation()).isEqualTo(location);
        assertThat(r.getBeaconLocation()).isNull();
    }

    //assert BeaconCheck = false
    //how to check that I get subscribed??
    @Test
    public void initBeaconObserver(){
        Application a = mock(Application.class);
        Context c = mock(Context.class);
        Mockito.when(a.getApplicationContext()).thenReturn(c);

        a.getApplicationContext();
        Repository r = Repository.getInstance(a);
        fillWithDummyValueBeaconConfig();
        LinkedList<Function> emptyFunctionList = new LinkedList<>();
        LinkedList<Location> emptyLocationList = new LinkedList<>();
        UIConfig uiConfig = new UIConfig(emptyFunctionList, emptyLocationList, "1");
        r.initNewUIConfig(uiConfig);

        r.initBeaconObserver();

    }

    //assert that notify MyFirebaseSubscriber ==> trigger update in Repo
    @Test
    public void requestRegisterUser(){
        Repository r = Repository.getInstance(null);

        Credential credential = new Credential.Builder("userName")
                .setPassword("pwd")
                .build();
        r.requestRegisterUser(credential);

        MyFirebaseMessagingService.getServiceObserver().notify(new CallbackValueInput(0,"", "", null, null));
        MyFirebaseMessagingService.getValueObserver().notify(new CallbackValueInput(0,"", "", null, null));


    }

    @Test
    //assert length of requestlist
    public void getFunctionMap(){
        //because requestCurrentFunctionValues is called
    }
    //assert length of requestlist
    public void getDataPointMap(){
        //because requestCurrentDataPointValues is called
    }

    //assert that cc.initStatusUpdateMap() is called
    public void updateCallbackValueInput(){
        //For CallbackValueInput
    }

    //assert that  serverCommunicator.getSavedCredentialsForLoginAfterRestart() is called
    //assert that  serverCommunicator.requestUIConfigAfterRestart() is called
    //assert that  serverCommunicator.requestAdditionalConfigsAfterRestart() is called
    //assert that  serverCommunicator.getSavedCredentialsForLoginAfterRestart() is called
    public void updateCallBackServiceInput(){
        //For CallBackServiceInput
    }

    //assert that  beaconLocation = newLocation;
    //beaconCheck == true
    public void updateLocation(){
        //For Location
    }

    //assert that all subscribers are empty
    public void unsubscribeFromEverything(){}
}
