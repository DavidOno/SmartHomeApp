package de.smarthome.command.impl;

import java.util.List;

import de.smarthome.command.Command;
import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.Request;

public class ChangeValueCommand implements Command {

    private final String id;
    private final Object newValue;

    public ChangeValueCommand(String id, Object change){
        this.id = id;
        this.newValue = change;
    }

    @Override
    public Request accept(CommandInterpreter commandInterpreter) {
        return commandInterpreter.buildChangeValueCommand(id, newValue);
    }
}
