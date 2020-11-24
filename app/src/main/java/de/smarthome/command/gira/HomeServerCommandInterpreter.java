package de.smarthome.command.gira;


import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.UUID;

import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.Request;
import de.smarthome.command.impl.RequestImpl;

public class HomeServerCommandInterpreter implements CommandInterpreter {

    private static final String NO_CACHE = "no-cache";
    private String token;
    private String uriPrefix;

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
    public Request buildChangeValueCommand(UUID id, Integer value) {
        String uri = uriPrefix + "/api/v2/values/" + id + "?token=" + token;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String jsonBody = "{\"value\": "+value+"}";

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
        return new RequestImpl(uri, HttpMethod.PUT, entity, String.class);
    }

    @Override
    public Request buildGetValueCommand(UUID id) {
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
        return new RequestImpl(uri, HttpMethod.GET, entity, String.class);
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
    public void setIP(String ip) {
        this.uriPrefix = "https://"+ip;
    }
}
