package de.smarthome.command.impl;

import org.springframework.web.client.RestTemplate;

/**
 * Interface for creating restTemplates
 */
public interface RestTemplateCreater {

    /**
     * Creates a resttemplate.
     * @return a restTemplate.
     */
    RestTemplate create();
}
