package de.smarthome.model.responses;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CallbackValueInput {

	private final int failures;
	private final String token;
	private final List<ValueEvent> valueEvents;
	
	public CallbackValueInput(@JsonProperty("failure") int failures, @JsonProperty("token") String token, @JsonProperty("events") List<ValueEvent> valueEvents) {
		this.failures = failures;
		this.token = token;
		this.valueEvents = new ArrayList<>(valueEvents);
	}

	public int getFailures() {
		return failures;
	}

	public String getToken() {
		return token;
	}

	public List<ValueEvent> getValueEvents() {
		return valueEvents;
	}

	@Override
	public String toString() {
		return "CallbackInput [failures=" + failures + ", token=" + token + ", events=" + valueEvents + "]";
	}
	
	

}
