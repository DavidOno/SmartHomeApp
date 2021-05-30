package de.smarthome.command.impl;

import java.util.function.Consumer;

import de.smarthome.command.AsyncCommand;
import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.Request;

public class RegisterCallback implements AsyncCommand {

    private final String ipOfCallbackServer;

    public RegisterCallback(String ipOfCallbackServer) {
        this.ipOfCallbackServer = ipOfCallbackServer;
    }

    @Override
    public void accept(CommandInterpreter commandInterpreter, Consumer<Request> callback) {
        commandInterpreter.buildRegisterAtCallbackServerCommand(ipOfCallbackServer, callback);
    }
}
