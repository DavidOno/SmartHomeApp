package de.smarthome.server;

import de.smarthome.app.model.responses.CallBackServiceInput;
import de.smarthome.app.model.responses.CallbackValueInput;

/**
 * This interface allows to use the information, received from the GIRA-server callback-loop.
 */
public interface CallbackSubscriber {

    /**
     * Specifies how to use the information from the given input, containing changed values.
     * @param input Input from the GIRA-server callback-loop.
     */
    void update(CallbackValueInput input);
    /**
     * Specifies how to use the information from the given input, containing registered events.
     * @param input Input from the GIRA-server callback-loop.
     */
    void update(CallBackServiceInput input);
}
