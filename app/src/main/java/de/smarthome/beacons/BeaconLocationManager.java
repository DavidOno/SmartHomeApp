package de.smarthome.beacons;

import android.location.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeaconLocationManager {
    private static final int N = 5;

    private BeaconID nearestBeacon;
    private BeaconID beaconID;
    private Map<BeaconID, List<Integer>> beacons2Signalstrength = new HashMap<>();
    private Map<BeaconID, Integer> signalStrengthAvg = new HashMap<>();

    void addNewBeaconStatus(Map<BeaconID, Integer> map) {
        System.out.println("MAPSIZE:::"+map.size());
        for(Map.Entry<BeaconID, Integer> entry: map.entrySet()) {
            BeaconID beaconID = entry.getKey();
            Integer signalStrength = entry.getValue();
            List<Integer> signalStrengths = beacons2Signalstrength.get(beaconID);
            signalStrengths = addSignalStrength(beaconID, signalStrength, signalStrengths);
            int average = calculateAverageSignalStrength(signalStrengths);
            signalStrengthAvg.put(beaconID, average);
        }
        nearestBeacon = retrieveBeaconIDWithMaximumSignalStrength(signalStrengthAvg);
        System.out.println("NEAREST::"+nearestBeacon.toString());
        System.out.println("RSSI_AVG::" + signalStrengthAvg.toString());
        System.out.println("LIST::" + beacons2Signalstrength.toString());
        //Log.d("BeaconLocationManager", nearestBeacon.toString());
        //Log.d("BeaconLocationManager", signalStrengthAvg.toString());
        //Log.d("BeaconLocationManager", beacons2Signalstrength.toString());
        }

    private BeaconID retrieveBeaconIDWithMaximumSignalStrength(Map<BeaconID, Integer> signalStrengthAvg) {
        return Collections.max(signalStrengthAvg.entrySet(), (entry1, entry2) -> entry1.getValue() - entry2.getValue()).getKey();
    }

    private List<Integer> addSignalStrength(BeaconID beaconID, Integer signalStrength, List<Integer> signalStrenghts) {
        if(signalStrenghts == null) {
            System.out.println("New Array List\n");
            signalStrenghts = new ArrayList<>();
        }else {
            System.out.println(signalStrenghts.size());
            if(signalStrenghts.size() == N) {
                signalStrenghts.remove(0);
            }
        }
        addSignalStrengthToMap(beaconID, signalStrength, signalStrenghts);
        return signalStrenghts;
    }

    private int calculateAverageSignalStrength(List<Integer> signalStrenghts) {
        return signalStrenghts.stream().reduce(0, (a, b) -> a + b) / signalStrenghts.size();
    }

    private void addSignalStrengthToMap(BeaconID beaconID, Integer signalStrength, List<Integer> signalStrenghts) {
        signalStrenghts.add(signalStrength);
        beacons2Signalstrength.put(beaconID, signalStrenghts);
    }

    public Location getLocation() {
        //TODO: Compare BeaconID of nearestBeacon and entries of JSON
        // Return corresponding location

        return null;
    }
}
