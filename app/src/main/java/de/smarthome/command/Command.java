package de.smarthome.command;

import java.util.List;

public interface Command {

    List<Request> accept(CommandInterpreter commandInterpreter);
}
