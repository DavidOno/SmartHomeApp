package de.smarthome.command.impl;

import de.smarthome.command.Command;
import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.Request;

/**
 * This class describes the user's intention in registering the callback-server at GIRA.
 */
public class RegisterCallbackServerAtGiraServer implements Command {

    private final String ipOfCallbackServer;

    public RegisterCallbackServerAtGiraServer(String ipOfCallbackServer){
        this.ipOfCallbackServer = ipOfCallbackServer;
    }

    /*
     * @inheritDoc
     */
    @Override
    public Request accept(CommandInterpreter commandInterpreter) {
        return commandInterpreter.buildRegisterCallbackServerAtGiraServer(ipOfCallbackServer);
    }
}
