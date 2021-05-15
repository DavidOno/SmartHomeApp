package de.smarthome.command.impl;

import de.smarthome.command.Command;
import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.Request;

public class RegisterCallbackServerAtGiraServer implements Command {

    private final String ipOfCallbackServer;

    public RegisterCallbackServerAtGiraServer(String ipOfCallbackServer){
        this.ipOfCallbackServer = ipOfCallbackServer;
    }

    @Override
    public Request accept(CommandInterpreter commandInterpreter) {
        return commandInterpreter.buildRegisterCallbackServerAtGiraServer(ipOfCallbackServer);
    }
}
