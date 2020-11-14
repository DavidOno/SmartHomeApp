package de.smarthome.command;

public interface Command {

    public Request accept(CommandInterpreter commandInterpreter);
}
