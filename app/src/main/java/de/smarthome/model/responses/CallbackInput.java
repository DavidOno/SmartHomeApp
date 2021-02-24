package de.smarthome.model.responses;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CallbackInput {

	private final int failures;
	private final String token;
	private final List<Event> events;
	
	public CallbackInput(@JsonProperty("failure") int failures, @JsonProperty("token") String token, @JsonProperty("events") List<Event> events) {
		this.failures = failures;
		this.token = token;
		this.events = new ArrayList<>(events);
	}

	public int getFailures() {
		return failures;
	}

	public String getToken() {
		return token;
	}

	public List<Event> getEvents() {
		return events;
	}

	@Override
	public String toString() {
		return "CallbackInput [failures=" + failures + ", token=" + token + ", events=" + events + "]";
	}
	
	

}
