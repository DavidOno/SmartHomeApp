package de.smarthome.command;

import org.springframework.http.ResponseEntity;

/**
 * Allows to execute multiple commands in a strict order.
 * A CommandChain can only be executed once.
 */
public interface CommandChain {

    /**
     * Checks if the chain has another element.
     * @return true if the chain has one or more elements left, otherwise false
     */
    boolean hasNext();

    /*
     * @return the next element in line.
     */
    CommandChainable getNext();

    /**
     * This method stores the result of the single request.
     * @param responseEntity The result of a request.
     */
    void putResult(ResponseEntity responseEntity);
}
