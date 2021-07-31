package de.smarthome.command.impl;

import de.smarthome.command.Command;
import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.Request;

/**
 * With this class the user's intention of checking the availability of the GIRA-server can be described.
 */
public class CheckAvailabilityCommand implements Command {

    /*
     * @inheritDoc
     */
    @Override
    public Request accept(CommandInterpreter commandInterpreter) {
        return commandInterpreter.buildAvailabilityCheckRequest();
    }
}
