package de.smarthome.command.gira;


import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.firebase.messaging.FirebaseMessaging;

import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.LinkedHashMap;
import java.util.Map;
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
    private String token;
    private String uriPrefix = "https://192.168.132.101";

    @Override
    public Request buildRegisterClientRequest(String username, String pwd) {
        String uri = uriPrefix + "/api/v2/clients";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAuthorization(new HttpBasicAuthentication(username, pwd));
        headers.setCacheControl(NO_CACHE);

        Map<Object, Object> jsonBody = new LinkedHashMap<>();
        jsonBody.put("client", "de.haw.la.msp.db");

        HttpEntity<Map<Object, Object>> entity = new HttpEntity<>(jsonBody, headers);
        return new RequestImpl(uri, HttpMethod.POST, entity, RegisterResponse.class);
    }

    @Override
    public Request buildChangeValueCommand(String id, Integer value) {
        String uri = uriPrefix + "/api/v2/values/" + id + "?token=" + token;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<Object, Object> jsonBody = new LinkedHashMap<>();
        jsonBody.put("value", value);

        HttpEntity<Map<Object, Object>> entity = new HttpEntity<>(jsonBody, headers);
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
    public Request buildUnregisterClientRequest(){
        String uri = uriPrefix+"/api/v2/clients/"+token;
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return new RequestImpl(uri, HttpMethod.DELETE, entity, JsonNode.class);
    }

    @Override
    public Request buildRegisterCallbackServerAtGiraServer(String ipCallbackServer) {
        String uri = uriPrefix+"/api/v2/clients/"+token+"/callbacks";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String serviceURI = "https://"+ipCallbackServer+"/service";
        String valueURI = "https://"+ipCallbackServer+"/value";

        Map<Object, Object> jsonBody = new LinkedHashMap();
        jsonBody.put("serviceCallback", serviceURI);
        jsonBody.put("valueCallback", valueURI);
        jsonBody.put("testCallbacks", true);

        HttpEntity<Map<Object, Object>> entity = new HttpEntity<>(jsonBody, headers);
        return new RequestImpl(uri, HttpMethod.POST, entity, JsonNode.class);
    }

    @Override
    public Request buildUnRegisterCallbackServerAtGiraServer() {
        String uri = uriPrefix+"/api/v2/clients/"+token+"/callbacks";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        return new RequestImpl(uri, HttpMethod.DELETE, entity, JsonNode.class);
    }

    @Override
    public void buildUnRegisterAtCallbackServerCommand(String ip, Consumer<Request> callback) {
        Function<String, Request> postExecutable = firebaseToken -> buildUnregisterRequest(ip, firebaseToken);
        getFirebaseTokenToCallbackServer(postExecutable, callback);
    }

    @Override
    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public void buildRegisterAtCallbackServerCommand(String ip, Consumer<Request> callback) {
        Function<String, Request> postExecutable = firebaseToken -> buildRegisterRequest(ip, firebaseToken);
        getFirebaseTokenToCallbackServer(postExecutable, callback);
    }

    private void getFirebaseTokenToCallbackServer(Function<String,Request> postExecutable, Consumer<Request> callback) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    String firebaseToken = task.getResult();
                    Log.d(TAG, "Firebase-Token retrieved: "+firebaseToken);
                    Request request = postExecutable.apply(firebaseToken);
                    callback.accept(request);
                });
    }

    private Request buildRegisterRequest(String ip, String token) {
        String uri = "https://"+ip+"/register";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String jsonBody = "[{\"token\": \""+token +"\"}]";
        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
        return new RequestImpl(uri, HttpMethod.POST, entity, String.class);   
    }

    private Request buildUnregisterRequest(String ip, String token){
        String uri = "https://"+ip+"/unregister?firebaseToken="+token;
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
