package de.smarthome.server.gira;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import de.smarthome.SmartHomeApplication;
import de.smarthome.command.AsyncCommand;
import de.smarthome.command.Command;
import de.smarthome.command.CommandInterpreter;
import de.smarthome.command.Request;
import de.smarthome.server.ServerHandler;

public class GiraServerHandler implements ServerHandler {

    private static final String TAG = "GIRA_SERVER_HANDLER";
    private static final int TIMEOUT = 3000;
    private CommandInterpreter commandInterpreter;

    public GiraServerHandler(CommandInterpreter commandInterpreter) {
        this.commandInterpreter = commandInterpreter;
    }

    @Override
    public void sendRequest(Command command) {
        List<Request> requests = command.accept(commandInterpreter);
        List<ResponseEntity> results = requests.stream().map(Request::execute).collect(Collectors.toList());
        Log.d(TAG, results.toString());
    }

    @Override
    public void sendRequest(AsyncCommand command){
        Consumer<List<Request>> requestsCallback = requests -> {
            new Thread(() -> {
                List<ResponseEntity> results = requests.stream().map(Request::execute).collect(Collectors.toList());
            }).start();
        };
        command.accept(commandInterpreter, requestsCallback);
    }

    @Override
    public String receiveRequest() {
        return null;
    }

    @Override
    public void selectServer(String ip) {
        commandInterpreter.setIP(ip);
    }

    @Override
    public List<InetAddress> showReachableInetAdresses(Context context) {
        List<InetAddress> reachableDevicesAdresses = new ArrayList<>();
//        new Thread(() -> {
            WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            String subnet = getSubnetAddress(mWifiManager.getDhcpInfo().gateway);
            reachableDevicesAdresses.addAll(getReachableHosts(subnet));
//        }).start();
        return reachableDevicesAdresses;
    }

    private List<InetAddress> getReachableHosts(String subnet) {
        List<InetAddress> reachableDeviceAdresses = new ArrayList<>();
        int timeout= TIMEOUT;
        for (int i=1;i<255;i++) {
            final int j = i;
            new Thread(()-> {
                String host = subnet + "." + j;
                try {
                    InetAddress address = InetAddress.getByName(host);
                    if (address.isReachable(timeout)) {
                        Log.d(TAG, "checkHosts() :: "+host+"("+ address.getHostName()+")"+ " is reachable");
                        reachableDeviceAdresses.add(address);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        return reachableDeviceAdresses;
    }

    private String getSubnetAddress(int address) {
        String ipString = String.format(
                "%d.%d.%d",
                (address & 0xff),
                (address >> 8 & 0xff),
                (address >> 16 & 0xff));
        return ipString;
    }
}
