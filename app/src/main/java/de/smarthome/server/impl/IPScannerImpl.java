package de.smarthome.server.impl;

import java.net.InetAddress;

import de.smarthome.server.IPScanner;

public class IPScannerImpl implements IPScanner {

    public void getNetworkIPs() {
        final byte[] ip;
        try {
            ip = InetAddress.getLocalHost().getAddress();
        } catch (Exception e) {
            return;
        }

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
                        } else {
                            System.out.println("Not Reachable: "+output);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}

