package de.smarthome.server;

import de.smarthome.command.Command;
import de.smarthome.command.CommandInterpreter;

public interface ServerHandler {

    public void sendRequest(Command command);
    public String receiveRequest();
    public void setCommandInterpreter(CommandInterpreter commandInterpreter);
}
