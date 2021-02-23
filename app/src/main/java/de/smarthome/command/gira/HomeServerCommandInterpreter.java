package de.smarthome.command.gira;


import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.firebase.messaging.FirebaseMessaging;

import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.function.Consumer;
import java.util.function.Function;

import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.Request;
import de.smarthome.command.impl.RequestImpl;
import de.smarthome.model.impl.UIConfig;
import de.smarthome.model.responses.AvailabilityResponse;
import de.smarthome.model.responses.GetValueReponse;
import de.smarthome.model.responses.RegisterResponse;

public class HomeServerCommandInterpreter implements CommandInterpreter {

    private static final String NO_CACHE = "no-cache";
    private static final String TAG = "HomeServerCommandInterpreter";
    private String token = "53Tg8Xdu6XgIW855pEkIB5tvrD5ODmyc";
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
        return new RequestImpl(uri, HttpMethod.POST, entity, RegisterResponse.class);
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
        return new RequestImpl(uri, HttpMethod.PUT, entity, JsonNode.class);
    }

    @Override
    public Request buildGetValueCommand(String id) {
        String uri = uriPrefix + "/api/v2/values/" + id + "?token=" + token;
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return new RequestImpl(uri, HttpMethod.GET, entity, GetValueReponse.class);
    }

    @Override
    public Request buildUIConfigRequest() {
        String uri = uriPrefix + "/api/v2/uiconfig?expand=locations&token=" + token;
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return new RequestImpl(uri, HttpMethod.GET, entity, UIConfig.class);
    }

    @Override
    public Request buildAvailabilityCheckRequest() {
        String uri = uriPrefix + "/api";
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return new RequestImpl(uri, HttpMethod.GET, entity, AvailabilityResponse.class);
    }

    @Override
    public Request buildUnregisterUserRequest(){
        String uri = uriPrefix+"/api/clients/"+token;
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return new RequestImpl(uri, HttpMethod.DELETE, entity, JsonNode.class);
    }

    @Override
    public Request buildGetAllValuesRequest(String locationID, UIConfig uiConfig) {
        return null;
    }

    @Override
    public void buildUnRegisterCallbackCommand(String ip, Consumer<Request> callback) {
        Function<String, Request> postExecutable = firebaseToken -> buildUnregisterRequest(ip, firebaseToken);
        getFirebaseTokenToCallbackServer(ip, postExecutable, callback);
    }

    @Override
    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public void buildRegisterCallbackCommand(String ip,  Consumer<Request> callback) {
        Function<String, Request> postExecutable = firebaseToken -> buildRegisterRequest(ip, firebaseToken);
        getFirebaseTokenToCallbackServer(ip, postExecutable, callback);
    }

    private void getFirebaseTokenToCallbackServer(String ip, Function<String,Request> postExecutable, Consumer<Request> callback) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    token = task.getResult();
                    Log.d(TAG, "Firebase-Token retrieved: "+token);
                    Request request = postExecutable.apply(token);
                    callback.accept(request);
                });
    }

    private Request buildRegisterRequest(String ip, String token) {
//        String uri = "https://"+ip+"/register"; //TODO: change to generic solution, via argument
        String uri = "https://10.59.2.45:8443/register";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String jsonBody = "[{\"token\": \""+token +"\"}]";
        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
        return new RequestImpl(uri, HttpMethod.POST, entity, String.class);   
    }

    private Request buildUnregisterRequest(String ip, String token){
//        String uri = "https://"+ip+"/register"; //TODO: change to generic solution, via argument
//        String uri = "https://192.168.132.212:8443/unregister";
        String uri = "https://10.59.2.45:8443/unregister?firebaseToken="+token;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        return new RequestImpl(uri, HttpMethod.DELETE, entity, JsonNode.class);
    }

    @Override
    public void setIP(String ip) {
        this.uriPrefix = "https://"+ip;
    }
}
