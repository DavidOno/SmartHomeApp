package de.smarthome.server.gira;

import android.content.Context;
import android.util.Log;

import org.springframework.http.ResponseEntity;

import java.net.InetAddress;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import de.smarthome.command.AsyncCommand;
import de.smarthome.command.Command;
import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.Request;
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
    public void sendRequest(Command command) {
        List<Request> requests = command.accept(commandInterpreter);
        List<ResponseEntity> results = requests.stream().map(Request::execute).collect(Collectors.toList()); //TODO: is there a meaningfull result
        Log.d(TAG, results.toString());
    }

    @Override
    public void sendRequest(AsyncCommand command){
        Consumer<Request> requestCallback = request -> new Thread(() -> {
            ResponseEntity result = request.execute();//TODO: is there a meaningfull result
        }).start();
        command.accept(commandInterpreter, requestCallback);
    }

    @Override
    public void selectServer(String ip) {
        commandInterpreter.setIP(ip);
    }

    public void setIpScanner(IPScanner ipScanner){
        this.ipScanner = ipScanner;
    }

    @Override
    public List<InetAddress> scanForReachableDevices(Context context) {
        return ipScanner.showReachableInetAdresses(context);
    }
}
