package de.smarthome.app.model.responses;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ValueEvent {
	
	private final String uid;
	private final Object value;


	public ValueEvent(@JsonProperty("uid") String uid, @JsonProperty("value") Object value) {
		super();
		this.uid = uid;
		this.value = value;
	}


	public String getUid() {
		return uid;
	}


	public Object getValue() {
		return value;
	}
	
	public Map<String, String> convertToData(){
		Map<String, String> data = new LinkedHashMap<>();
		data.put("uid", uid);
		data.put("value", String.valueOf(value));
		return data;
	}


	@Override
	public String toString() {
		return "Event [uid=" + uid + ", value=" + value + "]";
	}
	
	

}
