package de.smarthome.command.impl;

import de.smarthome.command.Command;
import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.Request;

/**
 * This class describes the user's intention in unregistering the callback-server at the GIRA-server.
 */
public class UnRegisterCallbackServerAtGiraServer implements Command {

    @Override
    public Request accept(CommandInterpreter commandInterpreter) {
        return commandInterpreter.buildUnRegisterCallbackServerAtGiraServer();
    }
}
