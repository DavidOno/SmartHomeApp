package de.smarthome.model.responses;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonIgnoreProperties(ignoreUnknown = true)
public class CallbackValueInput {

	private final int failures;
	private final String token;
	private final String uid;
	private final Object value;
	
	public CallbackValueInput(@JsonProperty("failures") int failures, @JsonProperty("token") String token, @JsonProperty("uid") String uid, @JsonProperty("value") Object value) {
		this.failures = failures;
		this.token = token;
		this.uid = uid;
		this.value = value;
	}

	public int getFailures() {
		return failures;
	}

	public String getToken() {
		return token;
	}



	@Override
	public String toString() {
		return "CallbackInput [failures=" + failures + ", token=" + token + ", value= "+value+", uid="+uid+"]";
	}
	
	

}
