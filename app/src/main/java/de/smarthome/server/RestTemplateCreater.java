package de.smarthome.server;

import org.springframework.web.client.RestTemplate;

public interface RestTemplateCreater {

    RestTemplate create();
}
