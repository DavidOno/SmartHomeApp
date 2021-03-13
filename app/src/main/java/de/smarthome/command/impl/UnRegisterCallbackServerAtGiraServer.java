package de.smarthome.command.impl;

import java.util.Arrays;
import java.util.List;

import de.smarthome.command.Command;
import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.Request;

public class UnRegisterCallbackServerAtGiraServer implements Command {

    @Override
    public Request accept(CommandInterpreter commandInterpreter) {
        Request request = commandInterpreter.buildUnRegisterCallbackServerAtGiraServer();
        return request;
    }
}
