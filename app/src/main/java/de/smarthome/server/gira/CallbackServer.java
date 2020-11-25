package de.smarthome.server.gira;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

public class CallbackServer {

    @RequestMapping("/")
    @RestController
    public class SpringServer {

        @PostMapping(path = "value")
        public void receiveCallBackValue(@RequestBody String body) {
            System.out.println(body);
        }

        @PostMapping(path = "service")
        public void receiveCallBackService(@RequestBody String body) {
            System.out.println(body);
        }


    }
}
