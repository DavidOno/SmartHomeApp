package de.smarthome.beacons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeaconLocationManager {
    private static final int N = 5;

    private BeaconID nearestBeacon;
    private Map<BeaconID, List<Integer>> beacons2SignalStrength = new HashMap<>();
    private Map<BeaconID, Integer> signalStrengthAvg = new HashMap<>();
    private Locations locations;

    public BeaconLocationManager(Locations locations) {
        this.locations = locations;
    }

    void addNewBeaconStatus(Map<BeaconID, Integer> map) {
        for(Map.Entry<BeaconID, Integer> entry: map.entrySet()) {
            BeaconID beaconID = entry.getKey();
            Integer signalStrength = entry.getValue();
            List<Integer> signalStrengths = beacons2SignalStrength.get(beaconID);
            signalStrengths = addSignalStrength(beaconID, signalStrength, signalStrengths);
            int average = calculateAverageSignalStrength(signalStrengths);
            signalStrengthAvg.put(beaconID, average);
        }
        nearestBeacon = retrieveBeaconIDWithMaximumSignalStrength(signalStrengthAvg);
        System.out.println("NEAREST::"+nearestBeacon.toString());
        System.out.println("RSSI_AVG::" + signalStrengthAvg.toString());
        System.out.println("LIST::" + beacons2SignalStrength.toString());
    }

    private BeaconID retrieveBeaconIDWithMaximumSignalStrength(Map<BeaconID, Integer> signalStrengthAvg) {
        return Collections.max(signalStrengthAvg.entrySet(), (entry1, entry2) -> entry1.getValue() - entry2.getValue()).getKey();
    }

    private List<Integer> addSignalStrength(BeaconID beaconID, Integer signalStrength, List<Integer> signalStrengths) {
        if(signalStrengths == null) {
            signalStrengths = new ArrayList<>();
        } else {
            if(signalStrengths.size() == N) {
                signalStrengths.remove(0);
            }
        }
        addSignalStrengthToMap(beaconID, signalStrength, signalStrengths);
        return signalStrengths;
    }

    private int calculateAverageSignalStrength(List<Integer> signalStrengths) {
        return signalStrengths.stream().reduce(0, Integer::sum) / signalStrengths.size();
    }

    private void addSignalStrengthToMap(BeaconID beaconID, Integer signalStrength, List<Integer> signalStrengths) {
        signalStrengths.add(signalStrength);
        beacons2SignalStrength.put(beaconID, signalStrengths);
    }

    public RoomLocation lookUpLocation(BeaconID nearestBeacon) {
        //TODO: Compare BeaconID of nearestBeacon and entries of JSON
        System.out.println("BeaconID:::" + nearestBeacon.extractBeaconId());
        /*
         * Übergebe nearestBeacon
         * Überprüfung von JSON => Welche RaumID steht hinter BeaconID
         * Zurückgeben der RaumID als RoomLocation-Objekt (roomId, roomName)
         */

        //RoomLocation currentRoom = new RoomLocation()

        return null;
    }
}
