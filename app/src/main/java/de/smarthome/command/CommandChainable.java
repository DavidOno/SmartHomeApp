package de.smarthome.command;

import de.smarthome.server.ServerHandler;

/**
 * This interface the behavior of a class, which can be part of a commandchain.
 */
public interface CommandChainable {

    /**
     * Defines how a single step looks like, when processing a commandchain.
     * @param handler The serverhandler, which executes the commandchain.
     * @param commandChain The whole commandchain.
     */
    void proceedInChain(ServerHandler handler, CommandChain commandChain);
}
