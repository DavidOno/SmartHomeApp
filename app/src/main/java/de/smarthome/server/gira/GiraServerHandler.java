package de.smarthome.server.gira;

import org.apache.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Array;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import de.smarthome.command.Command;
import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.Request;
import de.smarthome.server.ServerHandler;

public class GiraServerHandler implements ServerHandler {

    private CommandInterpreter commandInterpreter;

    public GiraServerHandler(CommandInterpreter commandInterpreter) {
        this.commandInterpreter = commandInterpreter;
    }

    @Override
    public void sendRequest(Command command) {
        List<Request> requests = command.accept(commandInterpreter);
//        ResponseEntity<String> result = request.execute(); //TODO: Check generic String: has to be improved
        List<ResponseEntity
                > results = requests.stream().map(Request::execute).collect(Collectors.toList());
    }

    @Override
    public String receiveRequest() {
        return null;
    }

    @Override
    public List<String> showDeviceIPs() {
        return getNetworkIPs();
    }

    @Override
    public void selectServer(String ip) {
        commandInterpreter.setIP(ip);
    }


    private List<String> getNetworkIPs() {
        final byte[] ip;
        try {
            ip = InetAddress.getLocalHost().getAddress();
        } catch (Exception e) {
            return Collections.emptyList();     // exit method, otherwise "ip might not have been initialized"
        }
        List<String> ips = new ArrayList<>();
        for(int i=1;i<=254;i++) {
            final int j = i;
            new Thread(new Runnable() {
                public void run() {
                    try {
                        ip[3] = (byte)j;
                        InetAddress address = InetAddress.getByAddress(ip);
//	                    address.getAddress()
//	                    address.getHostName()
                        String output = address.toString().substring(1);
                        if (address.isReachable(5000)) {
                            System.out.println(output + " is on the network");
                            ips.add(output);
                        } else {
                            System.out.println("Not Reachable: "+output);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        return ips;
    }
}
