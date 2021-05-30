package de.smarthome.command.impl;

import org.springframework.web.client.RestTemplate;

public interface RestTemplateCreater {

    abstract RestTemplate create();
}
