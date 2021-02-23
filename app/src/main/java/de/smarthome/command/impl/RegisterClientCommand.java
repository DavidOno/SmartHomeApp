package de.smarthome.command.impl;

import java.util.Arrays;
import java.util.List;

import de.smarthome.command.Command;
import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.Request;

public class RegisterClientCommand implements Command {

    private String username;
    private String pwd;

    public RegisterClientCommand(String username, String pwd) {
        this.username = username;
        this.pwd = pwd;
    }

    @Override
    public List<Request> accept(CommandInterpreter commandInterpreter) {
        return Arrays.asList(commandInterpreter.buildRegisterClientRequest(username, pwd));
    }
}
