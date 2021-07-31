package de.smarthome.command;

import org.springframework.http.ResponseEntity;

/**
 * A ResponseReactor allows you to define how to react to certain responses.
 * Normally a ResponseReactor is part of a Commandchain.
 */
public interface ResponseReactor {
    void react(ResponseEntity responseEntity);
}
