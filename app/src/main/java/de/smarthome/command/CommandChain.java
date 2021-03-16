package de.smarthome.command;

import org.springframework.http.ResponseEntity;

/**
 * Allows to execute multiple commands in a strict order.
 * A CommandChain can only be executed once.
 */
public interface CommandChain {

    boolean hasNext();
    Object getNext();
    void putResult(ResponseEntity responseEntity);
}
