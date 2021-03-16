package de.smarthome.command;

import org.springframework.http.ResponseEntity;

import de.smarthome.server.ServerHandler;

public interface Command extends CommandChainable{

    Request accept(CommandInterpreter commandInterpreter);

    @Override
    default void proceedInChain(ServerHandler handler, CommandChain commandChain) {
        handler.proceedInChain(this, commandChain);
    }
}
