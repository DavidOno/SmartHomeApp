package de.smarthome.command.gira;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.util.UUID;

import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.Request;
import de.smarthome.command.impl.RequestImpl;

public class HomeServerCommandInterpreter implements CommandInterpreter {

    private String token;
    private String uriPrefix;

    @Override
    public Request buildRegisterClientRequest() {
        return null;
    }

    @Override
    public Request buildUnregisterClientRequest() {
        return null;
    }

    @Override
    public Request buildChangeValueCommand() {
        return null;
    }

    @Override
    public Request buildGetValueCommand(UUID id) {
        String uri = buildURI(id);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return new RequestImpl(uri, HttpMethod.GET, entity, String.class);
    }

    private String buildURI(UUID id) {
        return new StringBuilder(uriPrefix).append("/api/v2/values/")
                                            .append(id)
                                            .append("?token=")
                                            .append(token)
                                            .toString();
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUriPrefix(String uriPrefix) {
        this.uriPrefix = uriPrefix;
    }

    @Override
    public Request buildSynchronizeRequest() {
        return null;
    }
}
