package de.smarthome.command.impl;

import de.smarthome.command.Command;
import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.Request;

/**
 * This class describes the user's intention in getting the ui-config from the GIRA-Server.
 */
public class UIConfigCommand implements Command {

    /**
     * @inheritDoc
     */
    @Override
    public Request accept(CommandInterpreter commandInterpreter) {
        return commandInterpreter.buildUIConfigRequest();
    }
}
