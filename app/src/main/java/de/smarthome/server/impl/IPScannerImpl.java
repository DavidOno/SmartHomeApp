package de.smarthome.server.impl;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import de.smarthome.server.IPScanner;

public class IPScannerImpl implements IPScanner {


    private static final int TIMEOUT = 5000;
    private static final String TAG = "IPScannerImpl";

    @Override
    public List<InetAddress> showReachableInetAdresses(Context context) {
        List<InetAddress> reachableDevicesAdresses;
        WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String subnet = getSubnetAddress(mWifiManager.getDhcpInfo().gateway);
        reachableDevicesAdresses = new ArrayList<>(getReachableHosts(subnet));
        return reachableDevicesAdresses;
    }


    private List<InetAddress> getReachableHosts(String subnet) {
        List<InetAddress> reachableDeviceAdresses = new ArrayList<>();
        for (int i = 1; i < 255; i++) {
            final int j = i;
            new Thread(() -> {
                String host = subnet + "." + j;
                try {
                    InetAddress address = InetAddress.getByName(host);
                    if (address.isReachable(TIMEOUT)) {
                        Log.d(TAG, "checkHosts() :: " + host + "(" + address.getHostName() + ")" + " is reachable");
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
        return String.format(
                "%d.%d.%d",
                (address & 0xff),
                (address >> 8 & 0xff),
                (address >> 16 & 0xff));
    }

}

