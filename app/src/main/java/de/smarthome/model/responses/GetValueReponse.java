package de.smarthome.model.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GetValueReponse {

    private final List<UID_Value> values;

    public GetValueReponse(@JsonProperty("values") List<UID_Value> values) {
        this.values = values;
    }
}
