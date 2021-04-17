package de.smarthome.command;

public interface MultiReactorCommandChain extends CommandChain{

    CommandChain add(Command command, ResponseReactor reactor);
    CommandChain add(AsyncCommand command, ResponseReactor reactor);
}
