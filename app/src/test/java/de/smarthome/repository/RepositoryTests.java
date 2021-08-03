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
import de.smarthome.server.ServerHandler;
import de.smarthome.server.SmartHomeFMS;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

//@RunWith(MockitoJUnitRunner.class)
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
            //Repository.getInstance(null).setBeaconLocations(mapper.readValue(locationConfigString, new TypeReference<BeaconLocations>() {}));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //ok
    @Test
    public void testConfirmBeaconLocation(){
        Repository r = Repository.getInstance();
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
        Repository r = Repository.getInstance();
        r.setParentApplication(a);
        //fillWithDummyValueBeaconConfig();
        LinkedList<Function> emptyFunctionList = new LinkedList<>();
        LinkedList<Location> emptyLocationList = new LinkedList<>();
        UIConfig uiConfig = new UIConfig(emptyFunctionList, emptyLocationList, "1");
        r.initNewUIConfig(uiConfig);

        r.initBeaconObserver();

        assertThat(r.getBeaconCheck().getValue()).isFalse();
    }

    //assert that notify MyFirebaseSubscriber ==> trigger update in Repo
    @Test
    public void requestRegisterUser(){
        Repository r = Repository.getInstance();

        /*Credential credential = new Credential.Builder("userName")
                .setPassword("pwd")
                .build();
        r.requestRegisterUser(credential);*/

        //SmartHomeFMS.getServiceObserver().notify(new CallbackValueInput(0,"", "", null, null));
        //SmartHomeFMS.getValueObserver().notify(new CallbackValueInput(0,"", "", null, null));
    }

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
    //assert taht I reached RESTART && DEFAULT
    public void updateCallBackServiceInput(){
        //For CallBackServiceInput
    }

    //ok
    @Test
    public void testUpdateLocationWithNewLocation(){
        Repository r = Repository.getInstance();
        LinkedList<String> emptyFunctionIdList = new LinkedList<>();
        LinkedList<Location> emptyLocationList = new LinkedList<>();
        Location location = new Location("l1", "1","dummy", emptyFunctionIdList, emptyLocationList,"dummy");

        r.update(location);

        assertThat(r.getBeaconLocation()).isEqualTo(location);
        assertThat(r.getBeaconCheck().getValue()).isTrue();
    }

    //ok
    @Test
    public void testUpdateLocationWithSameLocation(){
        Repository r = Repository.getInstance();
        LinkedList<String> emptyFunctionIdList = new LinkedList<>();
        LinkedList<Location> emptyLocationList = new LinkedList<>();
        Location location = new Location("l1", "1","dummy", emptyFunctionIdList, emptyLocationList,"dummy");
        r.update(location);
        r.setSelectedLocation(location);
        r.setBeaconCheckFalse();

        r.update(location);

        assertThat(r.getBeaconLocation()).isEqualTo(location);
        assertThat(r.getBeaconCheck().getValue()).isFalse();
    }

    //assert that all subscribers are empty
    public void unsubscribeFromEverything(){}
}
