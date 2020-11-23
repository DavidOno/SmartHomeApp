package de.smarthome.server;

import java.net.InetAddress;
import java.util.List;

import de.smarthome.command.Command;
import de.smarthome.command.CommandInterpreter;

public interface ServerHandler {

    public void sendRequest(Command command);
    public String receiveRequest();
    public List<String> showDeviceIPs();
    public void selectServer(String ip);
}
