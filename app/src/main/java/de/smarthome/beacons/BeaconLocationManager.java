package de.smarthome.beacons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.smarthome.model.impl.Location;
import de.smarthome.model.impl.UIConfig;

public class BeaconLocationManager {
    private static final int N = 5;

    private BeaconID nearestBeacon;
    private Map<BeaconID, List<Integer>> beacons2SignalStrength = new HashMap<>();
    private Map<BeaconID, Integer> signalStrengthAvg = new HashMap<>();
    //TODO: Initialise
    private UIConfig uiConfig;
    //TODO: Initialise
    private BeaconLocations locationConfig;
    private BeaconObserver beaconObserver;

    void addNewBeaconStatus(Map<BeaconID, Integer> map) {
        System.out.println("MAPSIZE:::"+map.size());
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
        //Log.d("BeaconLocationManager", nearestBeacon.toString());
        //Log.d("BeaconLocationManager", signalStrengthAvg.toString());
        //Log.d("BeaconLocationManager", beacons2Signalstrength.toString());

        Optional<Location> currentLocation = getLocation(nearestBeacon);

        beaconObserver.updateLocation(currentLocation);
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

    private int calculateAverageSignalStrength(List<Integer> signalStrengths) {
        return signalStrengths.stream().reduce(0, Integer::sum) / signalStrengths.size();
    }

    private void addSignalStrengthToMap(BeaconID beaconID, Integer signalStrength, List<Integer> signalStrengths) {
        signalStrengths.add(signalStrength);
        beacons2SignalStrength.put(beaconID, signalStrengths);
    }

    public Optional<Location> getLocation(BeaconID nearestBeacon) {
        return uiConfig.getLocation(locationConfig.getRoomUID(nearestBeacon));
    }

    public void setObserver(BeaconObserverImplementation beaconObserverImplementation) {
        this.beaconObserver = beaconObserverImplementation;
    }
}
