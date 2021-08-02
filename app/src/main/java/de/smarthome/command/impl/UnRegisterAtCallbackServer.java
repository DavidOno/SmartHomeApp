package de.smarthome.command.impl;

import java.util.function.Consumer;

import de.smarthome.command.AsyncCommand;
import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.Request;

/**
 * This class describes the user's intention in unregistering himself at the callback-server.
 */
public class UnRegisterAtCallbackServer implements AsyncCommand {

    private final String ip;

    public UnRegisterAtCallbackServer(String ip) {
        this.ip = ip;
    }

    @Override
    public void accept(CommandInterpreter commandInterpreter, Consumer<Request> requestCallback) {
        commandInterpreter.buildUnRegisterAtCallbackServerCommand(ip, requestCallback);
    }
}
