package de.smarthome.server;

import java.util.List;

import de.smarthome.command.AsyncCommand;
import de.smarthome.command.Command;

public interface ServerHandler {

    void sendRequest(Command command);
    void sendRequest(AsyncCommand command);
    String receiveRequest();
    List<String> showDeviceIPs();
    void selectServer(String ip);
}
