package de.smarthome.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Model class for capturing errors, which happen when interactivng with GIRA-server.
 */
public class ServerError {

    private final ErrorObject error;

    public ServerError(@JsonProperty("error")ErrorObject error) {
        super();
        this.error = error;
    }

    @Override
    public String toString() {
        return "ServerError [error=" + error + "]";
    }

    private static class ErrorObject {

        private final String code;
        private final String message;


        public ErrorObject(@JsonProperty("code")String code,
                           @JsonProperty("message")String message) {
            this.code = code;
            this.message = message;
        }


        @Override
        public String toString() {
            return "ErrorObject [code=" + code + ", message=" + message + "]";
        }

    }
}
