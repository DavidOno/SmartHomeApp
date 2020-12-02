package de.smarthome.server.impl;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

public class StandardErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().value() != 200;
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        //TODO: Proper error handling
        System.out.println("FOUND AN ERROR");
        System.out.println(response.getStatusText());
        System.out.println(response.getRawStatusCode());
        System.out.println(response.getStatusCode().value());
    }

}
