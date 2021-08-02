package de.smarthome.command;

import org.springframework.http.ResponseEntity;

/**
 * This interface defines the behavior of a request.
 */
public interface Request {

    /**
     * This method executes the request.
     * @return a responseEntity containing the reponse (status and body).
     */
    ResponseEntity execute();
}
