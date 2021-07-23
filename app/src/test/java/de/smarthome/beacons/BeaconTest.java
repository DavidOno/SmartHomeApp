package de.smarthome.beacons;

import android.app.Application;
import android.content.Context;

import com.fasterxml.jackson.databind.JsonNode;

import org.altbeacon.beacon.BeaconManager;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.smarthome.SmartHomeApplication;
import de.smarthome.app.model.Location;
import de.smarthome.app.model.UIConfig;
import de.smarthome.app.model.configs.ChannelConfig;
import de.smarthome.app.model.responses.AvailabilityResponse;
import de.smarthome.app.model.responses.GetValueResponse;
import de.smarthome.app.model.responses.RegisterResponse;
import de.smarthome.app.model.responses.UID_Value;
import de.smarthome.beacons.BeaconLocations;
import de.smarthome.command.AdditionalConfigs;
import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.gira.HomeServerCommandInterpreter;
import de.smarthome.command.impl.AdditionalConfigCommand;
import de.smarthome.command.impl.ChangeValueCommand;
import de.smarthome.command.impl.CheckAvailabilityCommand;
import de.smarthome.command.impl.GetValueCommand;
import de.smarthome.command.impl.RegisterCallbackServerAtGiraServer;
import de.smarthome.command.impl.RegisterClientCommand;
import de.smarthome.command.impl.UIConfigCommand;
import de.smarthome.command.impl.UnRegisterCallbackServerAtGiraServer;
import de.smarthome.command.impl.UnregisterClientCommand;
import de.smarthome.server.gira.GiraServerHandler;

import static org.mockito.Mockito.*;

import static org.assertj.core.api.Assertions.assertThat;

//@RunWith(MockitoJUnitRunner.class)
public class BeaconTest {
    /*@Test
    public void testAvailabilityCommand(){
        BeaconManagerCreator mockedBeaconManagerCreator = mock(BeaconManagerCreator.class);
        BeaconManager mockedBeaconManager = mock(BeaconManager.class);
        when(mockedBeaconManagerCreator.create(any())).thenReturn(mockedBeaconManager);
        Application mockedApp = mock(Application.class);
        Context mockedContext = mock(Context.class);
        BeaconObserverSubscriber mockedSubscriber = mock(BeaconObserverSubscriber.class);

        //Construct UIConfig
        List<Location> locations = new ArrayList<>();
        Location home = new Location("house","abc","4", Collections.emptyList(),
                Collections.emptyList(),"room");
        locations.add(home);

        UIConfig uiConfig = new UIConfig(Collections.emptyList(),locations,"heureka");

        //Construct BeaconConfig
        BeaconLocation homeBeacon = new BeaconLocation("abc",
                "7b44b47b-52a1-5381-90c2-f09b6838c5d490");
        BeaconLocations beaconConfig = new BeaconLocations(Arrays.asList(homeBeacon));


        BeaconObserverImplementation beaconObserver = new BeaconObserverImplementation(
                mockedApp, mockedContext.getApplicationContext(),
                uiConfig, beaconConfig, mockedBeaconManagerCreator);
        beaconObserver.subscribe(mockedSubscriber);
        beaconObserver.init();

        assertThat(beaconObserver.getCurrentLocation()).isEqualToComparingFieldByField(home);
        verify(mockedSubscriber, times(1)).update(home);
    }*/
}
