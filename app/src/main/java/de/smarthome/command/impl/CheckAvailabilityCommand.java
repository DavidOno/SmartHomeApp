package de.smarthome.command.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.smarthome.command.Command;
import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.Request;

public class CheckAvailabilityCommand implements Command {
    @Override
    public Request accept(CommandInterpreter commandInterpreter) {
        return commandInterpreter.buildAvailabilityCheckRequest();
    }
}
