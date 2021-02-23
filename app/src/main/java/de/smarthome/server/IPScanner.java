package de.smarthome.server;

import android.content.Context;

import java.net.InetAddress;
import java.util.List;

public interface IPScanner {

    List<InetAddress> showReachableInetAdresses(Context context);
}
