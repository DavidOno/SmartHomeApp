package de.smarthome.command.impl;

import java.util.Arrays;
import java.util.List;

import de.smarthome.command.Command;
import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.Request;

public class SynchronizeCommand implements Command {
    @Override
    public List<Request> accept(CommandInterpreter commandInterpreter) {
        return Arrays.asList(commandInterpreter.buildSynchronizeRequest());
    }
}
