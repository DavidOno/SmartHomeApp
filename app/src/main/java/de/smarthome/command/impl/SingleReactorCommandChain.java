package de.smarthome.command.impl;

import de.smarthome.command.AsyncCommand;
import de.smarthome.command.Command;
import de.smarthome.command.CommandChain;

public interface SingleReactorCommandChain extends CommandChain {

    CommandChain add(Command command);
    CommandChain add(AsyncCommand command);
}
