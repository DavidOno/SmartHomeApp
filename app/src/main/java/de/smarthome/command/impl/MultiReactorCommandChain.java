package de.smarthome.command.impl;

import de.smarthome.command.AsyncCommand;
import de.smarthome.command.Command;
import de.smarthome.command.CommandChain;

public interface MultiReactorCommandChain extends CommandChain{

    CommandChain add(Command command, ResponseReactor reactor);
    CommandChain add(AsyncCommand command, ResponseReactor reactor);
}
