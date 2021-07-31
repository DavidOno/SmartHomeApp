package de.smarthome.app.model.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
/**
 * This class stores stores all information given by GIRA when the callback-service-loop detects
 * a change in the observed values.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CallbackValueInput {

	private final int failures;
	private final String token;
	private final String uid; //UID from the Status Function
	private final Object value; //Value from the Status Function
	public final Events event; //Other Event ==> Value == null
	
	public CallbackValueInput(@JsonProperty("failures") int failures,
							  @JsonProperty("token") String token,
							  @JsonProperty("uid") String uid,
							  @JsonProperty("value") Object value,
							  @JsonProperty("event") String event) {
		this.failures = failures;
		this.token = token;
		this.uid = uid;
		this.value = value;
		this.event = Events.convert(event);
	}

	public int getFailures() {
		return failures;
	}

	public String getToken() {
		return token;
	}

	public String getUid() {
		return uid;
	}

	public Object getValue() {
		return value;
	}

	public Events getEvent() {
		return event;
	}

	@Override
	public String toString() {
		return "CallbackInput [failures=" + failures + ", token=" + token + ", value= "+value+", uid="+uid+", event:"+((event == null) ? "null" : event.toString())+"]";
	}
	
	

}
