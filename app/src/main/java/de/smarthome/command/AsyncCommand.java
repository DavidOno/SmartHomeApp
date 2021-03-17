package de.smarthome.command;

import java.util.function.Consumer;

import de.smarthome.server.ServerHandler;

public interface AsyncCommand extends CommandChainable{

    void accept(CommandInterpreter commandInterpreter, Consumer<Request> callback);

    @Override
    default void proceedInChain(ServerHandler handler, CommandChain commandChain) {
        handler.proceedInChain(this, commandChain);
    }
}
