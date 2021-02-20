package de.smarthome.server.impl;

import android.util.Log;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

public class StandardErrorHandler implements ResponseErrorHandler {

    private static final String TAG = "StandardErrorHandler";

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().value() != 200;
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        Log.d(TAG, "Status text: " + response.getStatusText());
        Log.d(TAG, "Status raw code: " + response.getRawStatusCode());
        Log.d(TAG, "Status code: " + response.getStatusCode().value());
    }
}
