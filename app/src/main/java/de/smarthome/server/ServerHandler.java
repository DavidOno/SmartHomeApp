package de.smarthome.server;

import android.content.Context;

import java.net.InetAddress;
import java.util.List;

import de.smarthome.command.AsyncCommand;
import de.smarthome.command.Command;

public interface ServerHandler {

    void sendRequest(Command command);
    void sendRequest(AsyncCommand command);
    String receiveRequest();
    List<InetAddress> showReachableInetAdresses(Context context);
    void selectServer(String ip);
}
