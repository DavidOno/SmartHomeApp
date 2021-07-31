package de.smarthome.command;

/**
 * This interface specifies behavior of the SingleReactorCommandChain.
 * The SingleReactorCommandChain allows to execute multiple commands in a row
 * and collecting all responses in a single responseReactor.
 */
public interface SingleReactorCommandChain extends CommandChain {

    /**
     * Adds a command to the commandchain.
     * @param command Command to be added.
     * @return the commandchain with the new command appended.
     */
    CommandChain add(Command command);

    /**
     * Adds an asynchronous command to the commandchain.
     * @param command Asynchronous command to be added.
     * @return the commandchain with the new command appended.
     */
    CommandChain add(AsyncCommand command);
}
