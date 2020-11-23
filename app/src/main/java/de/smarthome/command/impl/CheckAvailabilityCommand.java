package de.smarthome.command.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.smarthome.command.Command;
import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.Request;

public class CheckAvailabilityCommand implements Command {
    @Override
    public List<Request> accept(CommandInterpreter commandInterpreter) {
        return Arrays.asList(commandInterpreter.buildUIConfigRequest());
    }
}
