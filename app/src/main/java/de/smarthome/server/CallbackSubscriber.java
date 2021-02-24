package de.smarthome.server;

import de.smarthome.model.responses.CallbackInput;

public interface CallbackSubscriber {

    void update(CallbackInput input);
}
