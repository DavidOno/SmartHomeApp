package de.smarthome.command;

import org.springframework.http.ResponseEntity;

public interface ResponseReactor {
    void react(ResponseEntity responseEntity);
}
