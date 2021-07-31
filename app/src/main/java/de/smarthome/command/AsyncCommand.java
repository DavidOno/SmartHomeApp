package de.smarthome.command;

import java.util.function.Consumer;

import de.smarthome.server.ServerHandler;
/**
 * An asynchronous command describes the user's intention in interacting with his smarthome.
 * It is not coupled to any kind of REST/request/http-logic.
 * But it has to implement a double-dispatch to transform the user's intention to, for example, a REST-request.
 */
public interface AsyncCommand extends CommandChainable{
    /**
     * Part of the visitor-pattern.
     * It implements a double-dispatch by accepting a certain commandInterpreter, which interprets the command
     * accordingly to the specifically used GIRA-API.
     * @param commandInterpreter A commandInterpreter to interpret the command.
     * @param callback It's possible to provide a callback, since it's an asynchronous method.
     * @return The interpreted command as a complete REST-request.
     */
    void accept(CommandInterpreter commandInterpreter, Consumer<Request> callback);

    /**
     * @inheritDoc
     */
    @Override
    default void proceedInChain(ServerHandler handler, CommandChain commandChain) {
        handler.proceedInChain(this, commandChain);
    }
}
