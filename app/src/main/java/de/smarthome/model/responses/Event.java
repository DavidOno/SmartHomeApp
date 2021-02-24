package de.smarthome.model.responses;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Event {
	
	private final String uid;
	private final Object value;
	
	
	public Event(@JsonProperty("uid") String uid, @JsonProperty("value") Object value) {
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
