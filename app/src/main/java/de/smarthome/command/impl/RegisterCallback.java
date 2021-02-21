package de.smarthome.command.impl;

import java.util.function.Consumer;

import de.smarthome.command.AsyncCommand;
import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.Request;

public class RegisterCallback implements AsyncCommand {

    private String ip;

    public RegisterCallback(String ip) {
        this.ip = ip;
    }

    @Override
    public void accept(CommandInterpreter commandInterpreter, Consumer<Request> callback) {
        commandInterpreter.buildRegisterCallbackCommand(ip, callback);
    }
}
