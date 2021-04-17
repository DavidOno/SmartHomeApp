package de.smarthome.app.model.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RegisterResponse {

    private final String token;

    public RegisterResponse(@JsonProperty("token") String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "RegisterResponse{" +
                "token='" + token + '\'' +
                '}';
    }
}
