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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import de.smarthome.SmartHomeApplication;
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

    //single not ok

    public void testRetryConnectionToServerCallbackFail() throws ExecutionException, InterruptedException {
        ServerConnectionEvent event = ServerConnectionEvent.CALLBACK_CONNECTION_FAIL;
        GiraServerHandler mockGsh = mock(GiraServerHandler.class);
        ServerCommunicator sc = new ServerCommunicator(mockGsh);
        sc.setCallbackServerConnectionStatus(event);

        CompletableFuture<Void> y = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                sc.retryConnectionToServer();
            }
        }).thenRun(new Runnable(){
            @Override
            public void run() {
                assertThat(sc.getCallbackServerConnectionStatus()).isEqualTo(ServerConnectionEvent.CALLBACK_CONNECTION_ACTIVE);
            }
        });
        y.get();
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

    //multi/single not ok

    public void testConnectToGira() throws ExecutionException, InterruptedException {
        GiraServerHandler mockGsh = mock(GiraServerHandler.class);
        ServerCommunicator sc = new ServerCommunicator(mockGsh);

        CompletableFuture<Void> y = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                sc.connectToGira("user", "pwd");
                System.out.print("Hello1");
            }
        }).thenRun(new Runnable(){
            @Override
            public void run() {
                assertThat(sc.getGiraServerConnectionStatus()).isEqualTo(ServerConnectionEvent.GIRA_CONNECTION_ACTIVE);
                System.out.print("Hello2");
            }
        });
        y.get();
        System.out.print("Hello3");

    }

    //multi/single not ok

    public void testConnectToCallbackServer() throws ExecutionException, InterruptedException {
        GiraServerHandler mockGsh = mock(GiraServerHandler.class);
        ServerCommunicator sc = new ServerCommunicator(mockGsh);

        CompletableFuture<Void> y = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                sc.connectToCallbackServer();
            }
        }).thenRun(new Runnable(){
            @Override
            public void run() {
                assertThat(sc.getCallbackServerConnectionStatus()).isEqualTo(ServerConnectionEvent.CALLBACK_CONNECTION_ACTIVE);
            }
        });
        y.get();
    }

    //multi/single not ok

    public void testRequestOnlyUIConfig() throws ExecutionException, InterruptedException {
        GiraServerHandler mockGsh = mock(GiraServerHandler.class);
        ServerCommunicator sc = new ServerCommunicator(mockGsh);

        CompletableFuture<Void> y = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                sc.requestOnlyUIConfig();
                System.out.print("Hello1");
            }
        }).thenRun(new Runnable(){
            @Override
            public void run() {
                System.out.print("Hello22");
                ArgumentCaptor<CommandChain> argument = ArgumentCaptor.forClass(SingleReactorCommandChainImpl.class);
                verify(mockGsh, times(1)).sendRequest(argument.capture());
            }
        });
        y.get();
        System.out.print("Hello3");
    }

    //sngle not ok

    public void testRequestOnlyAdditionalConfigs() throws ExecutionException, InterruptedException {
        GiraServerHandler mockGsh = mock(GiraServerHandler.class);
        ServerCommunicator sc = new ServerCommunicator(mockGsh);

        CompletableFuture<Void> y = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                sc.requestOnlyAdditionalConfigs();
                System.out.print("Hello1");
            }
        }).thenRun(new Runnable(){
            @Override
            public void run() {
                System.out.print("Hello22");
                ArgumentCaptor<CommandChain> argument = ArgumentCaptor.forClass(MultiReactorCommandChain.class);
                verify(mockGsh, times(1)).sendRequest(argument.capture());
            }
        });
        y.get();
        System.out.print("Hello3");
    }

    //ok
    @Test
    public void testRequestSetValue() throws ExecutionException, InterruptedException {
        GiraServerHandler mockGsh = mock(GiraServerHandler.class);
        ServerCommunicator sc = new ServerCommunicator(mockGsh);
        String testID = "TestID";
        String value = "199";

        Command setValueCommand = new ChangeValueCommand(testID, Float.parseFloat(value));
        ResponseEntity<Command> myEntity = new ResponseEntity<>(setValueCommand, HttpStatus.OK);
        Mockito.when(mockGsh.sendRequest(ArgumentMatchers.any(ChangeValueCommand.class))).thenReturn(myEntity);

        CompletableFuture<Void> y = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                sc.requestSetValue(testID, value);
            }
        }).thenRun(new Runnable(){
            @Override
            public void run() {
                ArgumentCaptor<ChangeValueCommand> argument = ArgumentCaptor.forClass(ChangeValueCommand.class);
                verify(mockGsh, times(1)).sendRequest(argument.capture());
            }
        });
        y.get();
    }

    //multi not ok

    public void testRequestGetValue() throws ExecutionException, InterruptedException {
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

        CompletableFuture<Void> y = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                sc.requestGetValue(requestList);
            }
        }).thenRun(new Runnable(){
            @Override
            public void run() {
                assertThat(Repository.getInstance().getStatusGetValueMap().getValue()).containsEntry(testID, value);
                ArgumentCaptor<Command> argument = ArgumentCaptor.forClass(GetValueCommand.class);
                verify(mockGsh, times(1)).sendRequest(argument.capture());
            }
        });
        y.get();
    }

    //multi/single not ok

    public void testUnsubscribeFromEverything() throws ExecutionException, InterruptedException {
        GiraServerHandler mockGsh = mock(GiraServerHandler.class);
        ServerCommunicator sc = new ServerCommunicator(mockGsh);

        CompletableFuture<Void> y = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                sc.unregisterFromServers();
                System.out.print("Hello1");
            }
        }).thenRun(new Runnable(){
            @Override
            public void run() {
                ArgumentCaptor<Command> argument1 = ArgumentCaptor.forClass(UnregisterClientCommand.class);
                ArgumentCaptor<Command> argument2 = ArgumentCaptor.forClass(UnRegisterCallbackServerAtGiraServer.class);
                verify(mockGsh, times(1)).sendRequest(argument1.capture());
                verify(mockGsh, times(2)).sendRequest(argument2.capture());
                System.out.print("Hello2");
            }
        });
        y.get();
        System.out.print("Hello3");
    }
}
