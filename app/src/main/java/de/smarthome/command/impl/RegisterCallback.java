package de.smarthome.command.impl;

import java.util.function.Consumer;

import de.smarthome.command.AsyncCommand;
import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.Request;

/**
 * This class describes the user's intention to register himself to the GIRA-server.
 */
public class RegisterCallback implements AsyncCommand {

    private final String ipOfCallbackServer;

    public RegisterCallback(String ipOfCallbackServer) {
        this.ipOfCallbackServer = ipOfCallbackServer;
    }

    /*
     * @inheritDoc
     */
    @Override
    public void accept(CommandInterpreter commandInterpreter, Consumer<Request> callback) {
        commandInterpreter.buildRegisterAtCallbackServerCommand(ipOfCallbackServer, callback);
    }
}
