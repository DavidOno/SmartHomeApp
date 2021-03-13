package de.smarthome.server;

import android.content.Context;

import org.springframework.http.ResponseEntity;

import java.net.InetAddress;
import java.util.List;

import de.smarthome.command.AsyncCommand;
import de.smarthome.command.Command;
import de.smarthome.command.CommandChain;

public interface ServerHandler {

    ResponseEntity sendRequest(Command command);
    void sendRequest(AsyncCommand command);
    List<ResponseEntity> sendRequest(CommandChain commandChain);
    void selectServer(String ip);
    void setIpScanner(IPScanner ipScanner);
    List<InetAddress> scanForReachableDevices(Context context);
}
