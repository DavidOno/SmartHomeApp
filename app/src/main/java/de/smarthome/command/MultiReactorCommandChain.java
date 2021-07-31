package de.smarthome.command;
/**
 * This interface specifies behavior of the MultiReactorCommandChain.
 * The MultiReactorCommandChain allows to execute multiple commands in a row
 * and defining for every command a new reponseReactor.
 */
public interface MultiReactorCommandChain extends CommandChain{

    /**
     * Adds a command to the commandchain.
     * @param command Command to be added.
     * @param reactor Reactor, which should react to the received response.
     * @return the commandchain with the new command appended.
     */
    CommandChain add(Command command, ResponseReactor reactor);

    /**
     * Adds an asynchronous command to the commandchain.
     * @param command Asynchronous command to be added.
     * @param reactor Reactor, which should react to the received response.
     * @return the commandchain with the new command appended.
     */
    CommandChain add(AsyncCommand command, ResponseReactor reactor);
}
