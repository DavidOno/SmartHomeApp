package de.smarthome.command.impl;

import de.smarthome.command.Command;
import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.Request;

public class UnregisterClientCommand implements Command {
    @Override
    public Request accept(CommandInterpreter commandInterpreter) {
        return commandInterpreter.buildUnregisterClientRequest();
    }
}

