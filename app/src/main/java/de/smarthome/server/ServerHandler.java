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
    void sendRequest(CommandChain commandChain);
    void proceedInChain(Command command, CommandChain commandChain);
    void proceedInChain(AsyncCommand command, CommandChain commandChain);
}
