package de.smarthome.repository;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import de.smarthome.app.model.responses.GetValueResponse;
import de.smarthome.app.model.responses.UID_Value;
import de.smarthome.app.repository.Repository;
import de.smarthome.app.repository.ServerCommunicator;
import de.smarthome.app.repository.responsereactor.ServerConnectionEvent;
import de.smarthome.command.Command;
import de.smarthome.command.CommandChain;
import de.smarthome.command.MultiReactorCommandChain;
import de.smarthome.command.impl.ChangeValueCommand;
import de.smarthome.command.impl.GetValueCommand;
import de.smarthome.command.impl.SingleReactorCommandChainImpl;
import de.smarthome.command.impl.UnRegisterCallbackServerAtGiraServer;
import de.smarthome.command.impl.UnregisterClientCommand;
import de.smarthome.server.gira.GiraServerHandler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class ServerCommunicatorTests {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    //ok
    @Test
    public void testServerConnectionEventCallbackFail(){
        ServerConnectionEvent event = ServerConnectionEvent.CALLBACK_CONNECTION_FAIL;
        ServerCommunicator sc = new ServerCommunicator(null);

        sc.serverConnectionEvent(event);

        assertThat(sc.getCallbackServerConnectionStatus()).isEqualTo(event);
    }

    //ok
    @Test
    public void testServerConnectionEventCallbackSuccess(){
        ServerConnectionEvent event = ServerConnectionEvent.CALLBACK_CONNECTION_SUCCESS;
        ServerCommunicator sc = new ServerCommunicator(null);

        sc.serverConnectionEvent(event);

        assertThat(sc.getCallbackServerConnectionStatus()).isEqualTo(event);
    }

    //ok
    @Test
    public void testServerConnectionEventGiraFail(){
        ServerConnectionEvent event = ServerConnectionEvent.GIRA_CONNECTION_FAIL;
        ServerCommunicator sc = new ServerCommunicator(null);

        sc.serverConnectionEvent(event);

        assertThat(sc.getGiraServerConnectionStatus()).isEqualTo(event);
    }

    //ok
    @Test
    public void testServerConnectionEventGiraSuccess(){
        ServerConnectionEvent event = ServerConnectionEvent.GIRA_CONNECTION_SUCCESS;
        ServerCommunicator sc = new ServerCommunicator(null);

        sc.serverConnectionEvent(event);

        assertThat(sc.getGiraServerConnectionStatus()).isEqualTo(event);
    }

    //not ok
    @Test
    public void testRetryConnectionToServerCallbackFail(){
        ServerConnectionEvent event = ServerConnectionEvent.CALLBACK_CONNECTION_FAIL;
        GiraServerHandler mockGsh = mock(GiraServerHandler.class);
        ServerCommunicator sc = new ServerCommunicator(mockGsh);
        sc.setCallbackServerConnectionStatus(event);

        sc.retryConnectionToServer();
        sleep();

        assertThat(sc.getCallbackServerConnectionStatus()).isEqualTo(ServerConnectionEvent.CALLBACK_CONNECTION_ACTIVE);
    }

    //ok
    @Test
    public void testRetryConnectionToServerGiraFail(){
        ServerConnectionEvent event = ServerConnectionEvent.GIRA_CONNECTION_FAIL;
        GiraServerHandler mockGsh = mock(GiraServerHandler.class);
        ServerCommunicator sc = new ServerCommunicator(mockGsh);
        sc.setGiraServerConnectionStatus(event);

        sc.retryConnectionToServer();

        assertThat(sc.getGiraServerConnectionStatus()).isEqualTo(ServerConnectionEvent.GIRA_CONNECTION_FAIL);
    }

    //not ok
    @Test
    public void testConnectToGira(){
        GiraServerHandler mockGsh = mock(GiraServerHandler.class);
        ServerCommunicator sc = new ServerCommunicator(mockGsh);

        sc.connectToGira("user", "pwd");
        sleep();

        assertThat(sc.getGiraServerConnectionStatus()).isEqualTo(ServerConnectionEvent.GIRA_CONNECTION_ACTIVE);
    }

    //not ok
    @Test
    public void testConnectToCallbackServer(){
        GiraServerHandler mockGsh = mock(GiraServerHandler.class);
        ServerCommunicator sc = new ServerCommunicator(mockGsh);

        sc.connectToCallbackServer();
        sleep();

        assertThat(sc.getCallbackServerConnectionStatus()).isEqualTo(ServerConnectionEvent.CALLBACK_CONNECTION_ACTIVE);
    }

    //not ok
    @Test
    public void testRequestOnlyUIConfig(){
        GiraServerHandler mockGsh = mock(GiraServerHandler.class);
        ServerCommunicator sc = new ServerCommunicator(mockGsh);

        sc.requestOnlyUIConfig();
        sleep();

        ArgumentCaptor<CommandChain> argument = ArgumentCaptor.forClass(SingleReactorCommandChainImpl.class);
        verify(mockGsh, times(1)).sendRequest(argument.capture());
    }

    //not ok
    @Test
    public void testRequestOnlyAdditionalConfigs(){
        GiraServerHandler mockGsh = mock(GiraServerHandler.class);
        ServerCommunicator sc = new ServerCommunicator(mockGsh);

        sc.requestOnlyAdditionalConfigs();
        sleep();

        ArgumentCaptor<CommandChain> argument = ArgumentCaptor.forClass(MultiReactorCommandChain.class);
        verify(mockGsh, times(1)).sendRequest(argument.capture());
    }

    //not OK
    @Test
    public void testRequestSetValue(){
        GiraServerHandler mockGsh = mock(GiraServerHandler.class);
        ServerCommunicator sc = new ServerCommunicator(mockGsh);
        String testID = "TestID";
        String value = "199";

        Command setValueCommand = new ChangeValueCommand(testID, Float.parseFloat(value));
        ResponseEntity<Command> myEntity = new ResponseEntity<>(setValueCommand, HttpStatus.OK);
        Mockito.when(mockGsh.sendRequest(ArgumentMatchers.any(ChangeValueCommand.class))).thenReturn(myEntity);

        sc.requestSetValue(testID, value);
        sleep();

        ArgumentCaptor<ChangeValueCommand> argument = ArgumentCaptor.forClass(ChangeValueCommand.class);
        verify(mockGsh, times(1)).sendRequest(argument.capture());
    }

    //not ok
    @Test
    public void testRequestGetValue(){
        GiraServerHandler mockGsh = mock(GiraServerHandler.class);
        ServerCommunicator sc = new ServerCommunicator(mockGsh);
        String testID = "TestID";
        String value = "199";

        List<UID_Value> values = new ArrayList<>();
        values.add(new UID_Value(testID, value));
        ResponseEntity<GetValueResponse> myEntity = new ResponseEntity<>(new GetValueResponse(values), HttpStatus.OK);
        Mockito.when(mockGsh.sendRequest(ArgumentMatchers.any(GetValueCommand.class))).thenReturn(myEntity);

        List<String> requestList = new ArrayList<>();
        requestList.add(testID);

        sc.requestGetValue(requestList);
        sleep();

        ArgumentCaptor<Command> argument = ArgumentCaptor.forClass(GetValueCommand.class);
        verify(mockGsh, times(1)).sendRequest(argument.capture());

        assertThat(Repository.getInstance().getStatusGetValueMap().getValue()).containsEntry(testID, value);
    }

    //not ok
    @Test
    public void testUnsubscribeFromEverything(){
        GiraServerHandler mockGsh = mock(GiraServerHandler.class);
        ServerCommunicator sc = new ServerCommunicator(mockGsh);

        sc.unregisterFromServers();
        sleep();

        ArgumentCaptor<Command> argument1 = ArgumentCaptor.forClass(UnregisterClientCommand.class);
        ArgumentCaptor<Command> argument2 = ArgumentCaptor.forClass(UnRegisterCallbackServerAtGiraServer.class);
        verify(mockGsh, times(1)).sendRequest(argument1.capture());
        verify(mockGsh, times(1)).sendRequest(argument2.capture());
    }

    private void sleep(){
        try{
            Thread.sleep(30);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
