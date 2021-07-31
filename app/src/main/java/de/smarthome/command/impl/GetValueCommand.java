package de.smarthome.command.impl;

import de.smarthome.command.Command;
import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.Request;

/**
 * With this class the user's intention in getting the value of a device(-datapoint) can be described.
 */
public class GetValueCommand implements Command {

    private String idOfRequestedDatapoint;

    public GetValueCommand(String idOfRequestedDatapoint){
        this.idOfRequestedDatapoint = idOfRequestedDatapoint;
    }

    /*
     * @inheritDoc
     */
    @Override
    public Request accept(CommandInterpreter commandInterpreter) {
        return commandInterpreter.buildGetValueCommand(idOfRequestedDatapoint);
    }
}
