package de.smarthome.server;

import de.smarthome.app.model.responses.CallBackServiceInput;
import de.smarthome.app.model.responses.CallbackValueInput;

public interface CallbackSubscriber {

    void update(CallbackValueInput input);
    void update(CallBackServiceInput input);
}
