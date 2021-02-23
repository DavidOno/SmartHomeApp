package de.smarthome.server;

import android.content.Context;

import java.net.InetAddress;
import java.util.List;

import de.smarthome.command.AsyncCommand;
import de.smarthome.command.Command;

public interface ServerHandler {

    void sendRequest(Command command);
    void sendRequest(AsyncCommand command);
    void selectServer(String ip);
    void setIpScanner(IPScanner ipScanner);
    List<InetAddress> scanForReachableDevices(Context context);
}
