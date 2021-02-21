package de.smarthome.command.impl;

import java.util.function.Consumer;

import de.smarthome.command.AsyncCommand;
import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.Request;

public class UnRegisterCallback implements AsyncCommand {

    private String ip;

    public UnRegisterCallback(String ip) {
        this.ip = ip;
    }

    @Override
    public void accept(CommandInterpreter commandInterpreter, Consumer<Request> requestCallback) {
        commandInterpreter.buildUnRegisterCallbackCommand(ip, requestCallback);
    }
}
