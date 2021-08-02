package de.smarthome.command.impl;

import de.smarthome.command.Command;
import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.Request;

/**
 * With this class, a user's intention of changing the value of a device can be described.
 */
public class ChangeValueCommand implements Command {

    private final String id;
    private final Object newValue;

    public ChangeValueCommand(String id, Object change){
        this.id = id;
        this.newValue = change;
    }

    /*
     * @inheritDoc
     */
    @Override
    public Request accept(CommandInterpreter commandInterpreter) {
        return commandInterpreter.buildChangeValueCommand(id, newValue);
    }
}
