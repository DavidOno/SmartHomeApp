package de.smarthome.app.model.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
/**
 * This class is capable of storing all information given as response from GIRA when
 * requesting values from GIRA.
 */
public class GetValueResponse {

    private final List<UID_Value> values;

    public GetValueResponse(@JsonProperty("values") List<UID_Value> values) {
        this.values = values;
    }

    public List<UID_Value> getValues() {
        return values;
    }

    @Override
    public String toString() {
        return "GetValueReponse{" +
                "values=" + values +
                '}';
    }
}
