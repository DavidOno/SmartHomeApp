package de.smarthome.command;

import de.smarthome.server.ServerHandler;

public interface CommandChainable {

    void proceedInChain(ServerHandler handler, CommandChain commandChain);
}
