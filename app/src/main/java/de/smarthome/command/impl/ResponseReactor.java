package de.smarthome.command.impl;

import org.springframework.http.ResponseEntity;

public interface ResponseReactor {
    void react(ResponseEntity responseEntity);
}
