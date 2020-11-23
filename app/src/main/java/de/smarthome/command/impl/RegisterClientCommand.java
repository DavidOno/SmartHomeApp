package de.smarthome.command.impl;

import java.util.List;

import de.smarthome.command.Command;
import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.Request;

public class RegisterClientCommand implements Command {

    @Override
    public List<Request> accept(CommandInterpreter commandInterpreter) {
        return commandInterpreter.buildRegisterClientRequest();
    }
}
