package de.smarthome.command;

import java.util.List;

public interface Command {

    public List<Request> accept(CommandInterpreter commandInterpreter);
}
