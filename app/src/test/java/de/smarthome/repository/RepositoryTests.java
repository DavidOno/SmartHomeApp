package de.smarthome.repository;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.auth.api.credentials.Credential;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.smarthome.app.model.Datapoint;
import de.smarthome.app.model.Function;
import de.smarthome.app.model.Location;
import de.smarthome.app.model.UIConfig;
import de.smarthome.app.model.configs.Channel;
import de.smarthome.app.model.configs.ChannelConfig;
import de.smarthome.app.model.configs.ChannelDatapoint;
import de.smarthome.app.model.responses.CallBackServiceInput;
import de.smarthome.app.model.responses.CallbackValueInput;
import de.smarthome.app.model.responses.ServiceEvent;
import de.smarthome.app.repository.Repository;
import de.smarthome.app.repository.ServerCommunicator;
import de.smarthome.app.repository.StatusRequestType;
import de.smarthome.app.repository.responsereactor.ServerConnectionEvent;
import de.smarthome.command.Command;
import de.smarthome.command.impl.GetValueCommand;
import de.smarthome.server.SmartHomeFMS;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RepositoryTests {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    ServerCommunicator mockSc;

    @InjectMocks
    private Repository r = Repository.getInstance();

    @After
    public void cleanUp(){
        Repository.destroyInstance();
    }

    //ok?
    @Test
    public void testRestartConnectionToServer(){
        MutableLiveData<Boolean> x = new MutableLiveData<>(true);
        Mockito.when(mockSc.getServerConnectionStatus()).thenReturn(x);
        Mockito.when(mockSc.getGiraServerConnectionStatus()).thenReturn(ServerConnectionEvent.GIRA_CONNECTION_FAIL);
        Mockito.when(mockSc.getCallbackServerConnectionStatus()).thenReturn(ServerConnectionEvent.CALLBACK_CONNECTION_FAIL);

        r.restartConnectionToServer();

        verify(mockSc,  times(1)).retryConnectionToServer();
        assertThat(r.getServerConnectionStatus().getValue()).isTrue();
        assertThat(mockSc.getGiraServerConnectionStatus()).isEqualTo(ServerConnectionEvent.GIRA_CONNECTION_FAIL);
        assertThat(mockSc.getCallbackServerConnectionStatus()).isEqualTo(ServerConnectionEvent.CALLBACK_CONNECTION_FAIL);
    }

    //ok
    @Test
    public void testConfirmBeaconLocation(){
        LinkedList<String> fl = new LinkedList<>();
        LinkedList<Location> ll = new LinkedList<>();
        Location location = new Location("l1", "1","dummy", fl, ll,"dummy");
        r.update(location);

        r.confirmBeaconLocation();

        assertThat(r.getSelectedLocation()).isEqualTo(location);
        assertThat(r.getBeaconLocation()).isNull();
    }

    //ok
     @Test
    public void testInitBeaconObserver(){
        r.initBeaconObserver();
        assertThat(r.getBeaconCheck().getValue()).isFalse();
    }

    @Test
    public void testRequestRegisterUser(){
        Credential mockCredential = mock(Credential.class);
        //Mockito.when(mockCredential.getId()).thenReturn("ID");
        //Mockito.when(mockCredential.getPassword()).thenReturn("password");

        r.requestRegisterUser(mockCredential);

        String testId = "testID";
        String value = "199";

        SmartHomeFMS.getServiceObserver().notify(new CallbackValueInput(0,"", "", null, "startup"));
        SmartHomeFMS.getValueObserver().notify(new CallbackValueInput(0,"", testId, value, null));

        assertThat(r.getStatusUpdateMap().getValue()).containsEntry(testId, value);
    }

    @Test
    public void testRequestCurrentStatusValuesWithEmptyLists(){
        r.requestCurrentStatusValues(StatusRequestType.FUNCTION);
        verify(mockSc,  times(0)).getSavedCredentialsForLoginAtGira();

        r.requestCurrentStatusValues(StatusRequestType.DATAPOINT);
        verify(mockSc,  times(0)).getSavedCredentialsForLoginAtGira();
    }

    //TODO: Remove or rework assertions
    //TODO: Add something to verify result
    @Test
    public void testRequestCurrentStatusValuesFunction(){
        LinkedList<String> functionIdList = new LinkedList<>();
        functionIdList.add("1");
        functionIdList.add("2");
        functionIdList.add("3");
        functionIdList.add("4");
        LinkedList<Location> emptyLocationList = new LinkedList<>();
        Location location = new Location("l1", "1","dummy", functionIdList, emptyLocationList,"dummy");

        LinkedList<Datapoint> datapointList = new LinkedList<>();
        datapointList.add(new Datapoint("1", "datapoint1"));
        Function function = new Function("f1_function", "1", "de.gira.schema.channels.Switch", "dummy", datapointList);
        LinkedList<Datapoint> datapointList2 = new LinkedList<>();
        datapointList2.add(new Datapoint("2", "datapoint2"));
        Function functionStatus = new Function("f1_Status", "2", "de.gira.schema.channels.Switch", "dummy", datapointList2);

        LinkedList<Datapoint> datapointList3 = new LinkedList<>();
        datapointList3.add(new Datapoint("3", "datapoint3"));
        Function function2 = new Function("f2_function", "3", "de.gira.schema.channels.RoomTemperatureSwitchable", "dummy", datapointList3);
        LinkedList<Datapoint> datapointList4 = new LinkedList<>();
        datapointList4.add(new Datapoint("4", "datapoint4"));
        Function functionStatus2 = new Function("f2_Status", "4", "de.gira.schema.channels.RoomTemperatureSwitchable", "dummy", datapointList4);

        LinkedList<Function> functionList = new LinkedList<>();
        functionList.add(function);
        functionList.add(functionStatus);
        functionList.add(function2);
        functionList.add(functionStatus2);

        LinkedList<Location> ll2 = new LinkedList<>();
        ll2.add(location);
        UIConfig uiConfig = new UIConfig(functionList, ll2, "1");
        r.initNewUIConfig(uiConfig);
        r.setChannelConfig(createDummyChannelConfig());
        r.initSelectedLocation(location);

        r.requestCurrentStatusValues(StatusRequestType.FUNCTION);

        List<String> resultList = new ArrayList<>();
        resultList.add("2");
        resultList.add("4");

        verify(mockSc,  times(1)).addToExecutorService(ArgumentMatchers.any(Thread.class));
        //verify(mockSc,  times(1)).requestGetValue(resultList);
    }

    //TODO: Remove or rework assertions
    //TODO: Add something to verify result
    @Test
    public void testRequestCurrentStatusValuesDataPoint() throws InterruptedException {
        LinkedList<String> functionIdList = new LinkedList<>();
        functionIdList.add("1");
        functionIdList.add("2");
        LinkedList<Location> emptyLocationList = new LinkedList<>();
        Location location = new Location("l1", "1","dummy", functionIdList, emptyLocationList,"dummy");

        LinkedList<Datapoint> datapointList = new LinkedList<>();
        datapointList.add(new Datapoint("1", "datapoint1"));
        Function function = new Function("f_function", "1", "dummy", "dummy", datapointList);
        LinkedList<Datapoint> datapointList2 = new LinkedList<>();
        datapointList2.add(new Datapoint("2", "datapoint2"));
        Function functionStatus = new Function("f_Status", "2", "dummy", "dummy", datapointList2);

        LinkedList<Function> functionList = new LinkedList<>();
        functionList.add(function);
        functionList.add(functionStatus);

        LinkedList<Location> ll2 = new LinkedList<>();
        ll2.add(location);
        UIConfig uiConfig = new UIConfig(functionList, ll2, "2");
        r.initNewUIConfig(uiConfig);
        r.initSelectedLocation(location);
        r.initSelectedFunction(function);

        r.requestCurrentStatusValues(StatusRequestType.DATAPOINT);
        List<String> resultList = new ArrayList<>();
        resultList.add("2");

        verify(mockSc,  times(1)).addToExecutorService(ArgumentMatchers.any(Thread.class));
        //verify(mockSc,  times(1)).requestGetValue(resultList);
    }

    //ok
    @Test
    public void testUpdateCallbackValueInputWithValue(){
        String testId = "testID";
        String value = "199";

        CallbackValueInput callbackValueInput = new CallbackValueInput(0, "token", testId, value, null);
        r.update(callbackValueInput);

        assertThat(r.getStatusUpdateMap().getValue()).containsEntry(testId, value);
    }

    //TODO: Remove or rework assertions
    @Test
    public void testUpdateCallbackValueInputWithEvent(){
        CallbackValueInput callbackValueInput = new CallbackValueInput(0, "token", null, null, "STARTUP");
        r.update(callbackValueInput);
        //verify(mockSc,  times(1)).getSavedCredentialsForLoginAtGira();

        callbackValueInput = new CallbackValueInput(0, "token", null, null, "UICONFIGCHANGED");
        r.update(callbackValueInput);
        //verify(mockSc,  times(1)).requestOnlyUIConfig();

        callbackValueInput = new CallbackValueInput(0, "token", null, null, "PROJECTCONFIGCHANGED");
        r.update(callbackValueInput);
        //verify(mockSc,  times(1)).requestOnlyAdditionalConfigs();
        verify(mockSc,  times(3)).addToExecutorService(ArgumentMatchers.any(Thread.class));
    }

    //TODO: Remove or rework assertions
    @Test
    public void testUpdateCallBackServiceInput(){
        List<ServiceEvent> eventList = new ArrayList<>();
        eventList.add(new ServiceEvent("startup"));
        eventList.add(new ServiceEvent("uiConfigChanged"));
        eventList.add(new ServiceEvent("projectConfigChanged"));
        CallBackServiceInput callBackServiceInput = new CallBackServiceInput(0, "token",  eventList);

        r.update(callBackServiceInput);
        //verify(mockSc,  times(1)).getSavedCredentialsForLoginAtGira();
        //verify(mockSc,  times(1)).requestOnlyUIConfig();
        //verify(mockSc,  times(1)).requestOnlyAdditionalConfigs();
        verify(mockSc,  times(3)).addToExecutorService(ArgumentMatchers.any(Thread.class));
    }

    //ok
    @Test
    public void testUpdateLocationWithDifferentLocation(){
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
        LinkedList<String> emptyFunctionIdList = new LinkedList<>();
        LinkedList<Location> emptyLocationList = new LinkedList<>();
        Location location = new Location("l1", "1","dummy", emptyFunctionIdList, emptyLocationList,"dummy");
        r.update(location);
        r.initSelectedLocation(location);
        r.setBeaconCheckFalse();

        r.update(location);

        assertThat(r.getBeaconLocation()).isEqualTo(location);
        assertThat(r.getBeaconCheck().getValue()).isFalse();
    }

    private ChannelConfig createDummyChannelConfig(){
        List<Channel> channelList = new ArrayList<>();
        List<ChannelDatapoint> channelDatapoints = new ArrayList<>();

        channelDatapoints.add(new ChannelDatapoint("OnOff", "Binary", "rwe"));
        Channel c1 = new Channel("de.gira.schema.channels.Switch", channelDatapoints);
        channelList.add(c1);

        List<ChannelDatapoint> channelDatapoints2 = new ArrayList<>();
        channelDatapoints2.add(new ChannelDatapoint("Current", "Float", "r"));
        channelDatapoints2.add(new ChannelDatapoint("Set-Point", "Float", "rwe"));
        channelDatapoints2.add(new ChannelDatapoint("OnOff", "Binary", "rwe"));
        Channel c2 = new Channel("de.gira.schema.channels.RoomTemperatureSwitchable", channelDatapoints2);
        channelList.add(c2);

        return new ChannelConfig(channelList);
    }
}