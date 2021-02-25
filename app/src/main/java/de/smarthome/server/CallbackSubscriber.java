package de.smarthome.server;

import de.smarthome.model.responses.CallBackServiceInput;
import de.smarthome.model.responses.CallbackValueInput;

public interface CallbackSubscriber {

    void update(CallbackValueInput input);
    void update(CallBackServiceInput input);
}
