package de.smarthome.server.gira;

import de.smarthome.command.Command;
import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.Request;
import de.smarthome.server.ServerHandler;

public class GiraServerHandler implements ServerHandler {

    private CommandInterpreter commandInterpreter;

    @Override
    public void sendRequest(Command command) {
        Request request = command.accept(commandInterpreter);
    }

    @Override
    public String receiveRequest() {
        return null;
    }

    @Override
    public void setCommandInterpreter(CommandInterpreter commandInterpreter) {
        this.commandInterpreter = commandInterpreter;
    }
}
