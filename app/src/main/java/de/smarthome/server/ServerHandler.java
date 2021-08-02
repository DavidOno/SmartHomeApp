package de.smarthome.server;

import org.springframework.http.ResponseEntity;

import de.smarthome.command.AsyncCommand;
import de.smarthome.command.Command;
import de.smarthome.command.CommandChain;

/**
 * This interface specifies how users can issue their commands to the GIRA-server.
 */
public interface ServerHandler {

    /**
     * Sends a request to the GIRA-server.
     * @param command Command to be executed.
     * @return a ResponseEntity containing status code and a body.
     */
    ResponseEntity sendRequest(Command command);

    /**
     * Sends a asynchronous request to the GIRA-server.
     * @param command Asynchronous command to be executed.
     */
    void sendRequest(AsyncCommand command);

    /**
     * Sends a a chain of requests to the GIRA-server.
     * All requests are executed in a row, in the order they were inserted.
     * @param commandChain Command-chain, which is to be executed.
     */
    void sendRequest(CommandChain commandChain);

    /**
     * Defines a single step on how to work off a command-chain.
     * Normally this requires sending the request, storing the result and recursively proceeding with the command-chain.
     * @param command Single command from command-chain to be executed.
     * @param commandChain Command-chain to be executed.
     */
    void proceedInChain(Command command, CommandChain commandChain);

    /**
     * Defines a single step on how to work off a command-chain.
     * Normally this requires sending the request, storing the result and recursively proceeding with the command-chain.
     * @param command Single asynchronous command from command-chain to be executed.
     * @param commandChain Command-chain to be executed.
     */
    void proceedInChain(AsyncCommand command, CommandChain commandChain);
}
