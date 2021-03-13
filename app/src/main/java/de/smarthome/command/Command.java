package de.smarthome.command;

import java.util.List;

public interface Command {

    Request accept(CommandInterpreter commandInterpreter);
}
