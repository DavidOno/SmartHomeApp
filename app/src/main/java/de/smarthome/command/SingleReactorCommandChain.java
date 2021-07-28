package de.smarthome.command;

public interface SingleReactorCommandChain extends CommandChain {

    CommandChain add(Command command);
    CommandChain add(AsyncCommand command);
}
