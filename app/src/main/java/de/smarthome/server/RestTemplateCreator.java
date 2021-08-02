package de.smarthome.server;

import org.springframework.web.client.RestTemplate;

/**
 * Interface for creating restTemplates.
 */
public interface RestTemplateCreator {

    /**
     * Create a new RestTemplate.
     * @return a RestTemplate.
     */
    RestTemplate create();
}
