package de.smarthome.command.impl;

import java.util.List;

import de.smarthome.command.Command;
import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.Request;

public class GetValueCommand implements Command {

    private String idOfRequestedDatapoint;

    public GetValueCommand(String idOfRequestedDatapoint){
        this.idOfRequestedDatapoint = idOfRequestedDatapoint;
    }


    @Override
    public Request accept(CommandInterpreter commandInterpreter) {
        return commandInterpreter.buildGetValueCommand(idOfRequestedDatapoint);
    }
}
