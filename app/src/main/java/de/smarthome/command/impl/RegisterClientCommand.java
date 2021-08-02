package de.smarthome.command.impl;

import de.smarthome.command.Command;
import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.Request;

public class RegisterClientCommand implements Command {

    private final String username;
    private final String pwd;

    public RegisterClientCommand(String username, String pwd) {
        this.username = username;
        this.pwd = pwd;
    }

    /*
     * @inheritDoc
     */
    @Override
    public Request accept(CommandInterpreter commandInterpreter) {
        return commandInterpreter.buildRegisterClientRequest(username, pwd);
    }
}
