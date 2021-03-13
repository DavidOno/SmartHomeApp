package de.smarthome.command;

/**
 * Allows to execute multiple commands in a strict order.
 * A CommandChain can only be executed once.
 */
public interface CommandChain {

    CommandChain add(Command command);
    CommandChain add(AsyncCommand asyncCommand);
    boolean hasNext();
    Object getNext();
}
