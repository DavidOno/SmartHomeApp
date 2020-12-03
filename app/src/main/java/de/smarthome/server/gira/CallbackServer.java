package de.smarthome.server.gira;
import java.util.Set;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RequestMapping("/")
@RestController
public class CallbackServer {
	
	@PostMapping(path="value")
	public void receiveCallBackValue(@RequestBody String body) throws JsonMappingException, JsonProcessingException {
		System.out.println(body);
		ObjectMapper m = new ObjectMapper();
//		Set<DeviceImpl> products = m.readValue(body, new TypeReference<Set<DeviceImpl>>() {});
//		products.forEach(p -> System.out.println(p));
	}
	
	@PostMapping(path="error")
	public void receiveCallBackError(@RequestBody String body) throws JsonMappingException, JsonProcessingException {
		System.out.println(body);
		ObjectMapper m = new ObjectMapper();
//		Set<ServerError> products = m.readValue(body, new TypeReference<Set<ServerError>>() {});
//		products.forEach(p -> System.out.println(p));
	}
	
	@PostMapping(path="service")
	public void receiveCallBackService(@RequestBody String body) {
		System.out.println(body);
		
	}
	
	
//	@PostMapping(path="json")
//	public void receiveJson(@RequestBody DeviceImpl body) {
//		System.out.println(body);
//	}
}
