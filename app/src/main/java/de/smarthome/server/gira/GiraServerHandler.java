package de.smarthome.server.gira;

import android.content.Context;
import android.util.Log;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.InetAddress;
import java.util.List;
import java.util.function.Consumer;

import de.smarthome.command.AsyncCommand;
import de.smarthome.command.Command;
import de.smarthome.command.CommandChain;
import de.smarthome.command.CommandChainable;
import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.Request;
import de.smarthome.model.responses.RegisterResponse;
import de.smarthome.server.IPScanner;
import de.smarthome.server.ServerHandler;

import static de.smarthome.SmartHomeApplication.EXECUTOR_SERVICE;

public class GiraServerHandler implements ServerHandler {

    private static final String TAG = "GiraServerHandler";
    private CommandInterpreter commandInterpreter;
    private IPScanner ipScanner;

    public GiraServerHandler(CommandInterpreter commandInterpreter) {
        this.commandInterpreter = commandInterpreter;
    }

    @Override
    public void sendRequest(CommandChain commandChain) {
        if(commandChain.hasNext()) {
            CommandChainable nextCommand = commandChain.getNext();
            nextCommand.proceedInChain(this, commandChain);
        }
    }

    @Override
    public void proceedInChain(Command command, CommandChain commandChain) {
        ResponseEntity responseEntitiy = sendRequest(command);
        commandChain.putResult(responseEntitiy);
        sendRequest(commandChain);
    }

    @Override
    public void proceedInChain(AsyncCommand command, CommandChain commandChain) {
        Consumer<Request> requestCallback = request ->
                EXECUTOR_SERVICE.execute(() -> {  //this thread is required since the callback gets otherwise executed on main-thread.
                    ResponseEntity result = request.execute();
                    Log.d(TAG, result != null ? result.toString() : "Result is null");
                    commandChain.putResult(result);
                    sendRequest(commandChain);
                });
        command.accept(commandInterpreter, requestCallback);
    }


//    public void sendRequest(Command command, CommandChain commandChain) {
//        ResponseEntity responseEntitiy = sendRequest(command);
//        commandChain.putResult(responseEntitiy);
//        sendRequest(commandChain);
//    }

    @Override
    public ResponseEntity sendRequest(Command command) {
        Request request = command.accept(commandInterpreter);
        ResponseEntity result = request.execute();
        checkIfNewTokenWasGiven(result);
        checkIfTokenWasRevoked(result);
        Log.d(TAG, result == null ? "null" : result.toString());
        return result;
    }

    private void checkIfTokenWasRevoked(ResponseEntity result) {
        if (wasRevoked(result)) {
            commandInterpreter.setToken(null);
        }
        
    }

    private void checkIfNewTokenWasGiven(ResponseEntity result) {
        if (containsToken(result)) {
            RegisterResponse registerResponse = (RegisterResponse) result.getBody();
            commandInterpreter.setToken(registerResponse.getToken());
        }
    }

    private boolean containsToken(ResponseEntity response) {
        if(response == null){
            return false;
        }
        return response.getStatusCode() == HttpStatus.OK && response.getBody() instanceof RegisterResponse;
    }

    private boolean wasRevoked(ResponseEntity response) {
        if(response == null){
            return false;
        }
        return response.getStatusCode() == HttpStatus.NO_CONTENT;
    }


//    private void sendRequest(AsyncCommand command, CommandChain commandChain) {
//        Consumer<Request> requestCallback = request ->
//                EXECUTOR_SERVICE.execute(() -> {  //this thread is required since the callback gets otherwise executed on main-thread.
//                    ResponseEntity result = request.execute();
//                    Log.d(TAG, result != null ? result.toString() : "Result is null");
//                    commandChain.putResult(result);
//                    sendRequest(commandChain);
//                });
//        command.accept(commandInterpreter, requestCallback);
//    }

    @Override
    public void sendRequest(AsyncCommand command) {
        Consumer<Request> requestCallback = request ->
                EXECUTOR_SERVICE.execute(() -> {  //this thread is required since the callback gets executed on main-thread.
                    ResponseEntity result = request.execute();
                    Log.d(TAG, result != null ? result.toString() : "Result is null");
                });
        command.accept(commandInterpreter, requestCallback);
    }
}
