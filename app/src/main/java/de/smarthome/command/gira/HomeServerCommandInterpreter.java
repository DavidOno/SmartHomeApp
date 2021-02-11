package de.smarthome.command.gira;


import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;

import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.Request;
import de.smarthome.command.impl.RequestImpl;

public class HomeServerCommandInterpreter implements CommandInterpreter {

    private static final String NO_CACHE = "no-cache";
    private static final String TAG = "HOMESERVERCOMMANDINTERPRETER";
    private String token = "2kF0AOoL1JHgmoy6b1W9UJAr3GDUSbux";
    private String uriPrefix = "https://192.168.132.101";

    @Override
    public Request buildRegisterClientRequest(String username, String pwd) {
        String uri = uriPrefix + "/api/v2/clients";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAuthorization(new HttpBasicAuthentication(username, pwd));
        headers.setCacheControl(NO_CACHE);

        String jsonBody = "{\"client\": \"de.haw.la.msp.db\"}";

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
        return new RequestImpl(uri, HttpMethod.POST, entity, String.class);
    }

    @Override
    public Request buildUnregisterClientRequest() {
        return null;
    }

    @Override
    public Request buildChangeValueCommand(String id, Integer value) {
        String uri = uriPrefix + "/api/v2/values/" + id + "?token=" + token;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String jsonBody = "{\"value\": "+value+"}";

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
        return new RequestImpl(uri, HttpMethod.PUT, entity, String.class);
    }

    @Override
    public Request buildGetValueCommand(String id) {
        String uri = uriPrefix + "/api/v2/values/" + id + "?token=" + token;
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return new RequestImpl(uri, HttpMethod.GET, entity, String.class);
    }

    @Override
    public Request buildUIConfigRequest() {
        String uri = uriPrefix + "/api/v2/uiconfig?token=" + token;
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return new RequestImpl(uri, HttpMethod.GET, entity, String.class);//TODO: change String.class to UIConfig.class
    }

    @Override
    public Request buildAvailabilityCheckRequest() {
        String uri = uriPrefix + "/api";
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return new RequestImpl(uri, HttpMethod.GET, entity, String.class);
    }


    @Override
    public Request buildSynchronizeRequest() {
        return null;
    }

    @Override
    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public void buildRegisterCallbackCommand(String ip, Consumer<List<Request>> requestsCallback) {
        List<Request> requests = new ArrayList<>();
        //send callback ip to homeserver
        //send token to callbackserver, for communication: callback -> app
        Consumer<Request> tokenCallback = request -> {
            requests.add(request);
            Log.d(TAG, "Callback0");
            requestsCallback.accept(requests);
            Log.d(TAG, "Callback1");
        };
        getFirebaseTokenToCallbackServer(ip, tokenCallback);

    }

    private void getFirebaseTokenToCallbackServer(String ip, Consumer<Request> callback) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    token = task.getResult();
                    Log.d(TAG, token);
                    Request request = buildFirebaseTokenRequest(ip, token);
                    callback.accept(request);
                });
    }

    private Request buildFirebaseTokenRequest(String ip, String token) {
//        String uri = "https://"+ip+"/register";
        String uri = "https://10.59.2.45:8443/register";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String jsonBody = "[{" +
                "    \"token\": "+token +
                "}]";
        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
        return new RequestImpl(uri, HttpMethod.POST, entity, String.class);
    }

    @Override
    public void setIP(String ip) {
        this.uriPrefix = "https://"+ip;
    }
}
