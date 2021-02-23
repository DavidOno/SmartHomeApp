package de.smarthome.server.gira;

import android.content.Context;
import android.util.Log;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.InetAddress;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import de.smarthome.command.AsyncCommand;
import de.smarthome.command.Command;
import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.Request;
import de.smarthome.model.responses.RegisterResponse;
import de.smarthome.server.IPScanner;
import de.smarthome.server.ServerHandler;

public class GiraServerHandler implements ServerHandler {

    private static final String TAG = "GiraServerHandler";
    private CommandInterpreter commandInterpreter;
    private IPScanner ipScanner;

    public GiraServerHandler(CommandInterpreter commandInterpreter) {
        this.commandInterpreter = commandInterpreter;
    }

    @Override
    public List<ResponseEntity> sendRequest(Command command) {
        List<Request> requests = command.accept(commandInterpreter);
        List<ResponseEntity> results = requests.stream().map(Request::execute).collect(Collectors.toList());
        checkIfNewTokenWasGiven(results);
        checkIfTokenWasRevocted(results);
        Log.d(TAG, results.toString());
        return results;
    }

    private void checkIfTokenWasRevocted(List<ResponseEntity> results) {
        for(ResponseEntity response : results){
            if(response == null){
                continue;
            }
            if (wasRevocted(response)) {
                commandInterpreter.setToken(null);
            }
        }
    }

    private void checkIfNewTokenWasGiven(List<ResponseEntity> results) {
        for(ResponseEntity response : results){
            if(response == null){
                continue;
            }
            if (containsToken(response)) {
                RegisterResponse registerResponse = (RegisterResponse) response.getBody();
                commandInterpreter.setToken(registerResponse.getToken());
            }
        }
    }

    private boolean containsToken(ResponseEntity response) {
        return response.getStatusCode() == HttpStatus.OK && response.getBody() instanceof RegisterResponse;
    }

    private boolean wasRevocted(ResponseEntity response) {
        return response.getStatusCode() == HttpStatus.NO_CONTENT;
    }

    @Override
    public void sendRequest(AsyncCommand command) {
        Consumer<Request> requestCallback = request ->
                new Thread(() -> {  //this thread is required since the callback gets executed on main-thread.
                    ResponseEntity result = request.execute();
                    Log.d(TAG, result != null ? result.toString() : "Result is null");
                }).start();
        command.accept(commandInterpreter, requestCallback);
    }

    @Override
    public void selectServer(String ip) {
        commandInterpreter.setIP(ip);
    }

    public void setIpScanner(IPScanner ipScanner) {
        this.ipScanner = ipScanner;
    }

    @Override
    public List<InetAddress> scanForReachableDevices(Context context) {
        return ipScanner.showReachableInetAdresses(context);
    }
}
