package de.smarthome.beacons;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import de.smarthome.app.model.Location;
import de.smarthome.app.model.UIConfig;

public class BeaconLocationManager {
    private static final String TAG = "BeaconLocationManager";
    private static final int N = 5;

    private BeaconID nearestBeacon;
    private Map<BeaconID, List<Integer>> beacons2SignalStrength = new HashMap<>();
    private Map<BeaconID, Integer> signalStrengthAvg = new HashMap<>();
    private UIConfig uiConfig;
    private BeaconLocations locationConfig;
    private BeaconObserver beaconObserver;

    public BeaconLocationManager(UIConfig newUIConfig, BeaconLocations newBeaconConfig) {
        this.uiConfig = newUIConfig;
        this.locationConfig = newBeaconConfig;
    }

    void addNewBeaconStatus(Map<BeaconID, Integer> updatedBeaconSignals) {
        updateSignalsStrengths(updatedBeaconSignals);
        nearestBeacon = retrieveBeaconIDWithMaxAverageSignalStrength(signalStrengthAvg);
        //Log.d(TAG, "nearestBeacon " + nearestBeacon.toString());
        System.out.println("NEARESTBEACON::: " + nearestBeacon.toString());
        System.out.println("RSSI_AVG::: " + signalStrengthAvg.toString());
        System.out.println("LIST::: " + beacons2SignalStrength.toString());
        Optional<Location> currentLocation = getLocation(nearestBeacon);

        beaconObserver.updateLocation(currentLocation);
        }

    private void updateSignalsStrengths(Map<BeaconID, Integer> updatedBeaconSignals) {
        for(Map.Entry<BeaconID, List<Integer>> entry : beacons2SignalStrength.entrySet()){
            updateKnownSignalStrengths(updatedBeaconSignals, entry);
        }
        addNewSignalStrengths(updatedBeaconSignals);
    }

    private void addNewSignalStrengths(Map<BeaconID, Integer> updatedBeaconSignals) {
        for(Map.Entry<BeaconID, Integer> remainingEntry : updatedBeaconSignals.entrySet()){
            addNewSignalStrength(remainingEntry);
        }
    }

    private void updateKnownSignalStrengths(Map<BeaconID, Integer> updatedBeaconSignals, Map.Entry<BeaconID, List<Integer>> entry) {
        BeaconID beaconID = entry.getKey();
        if(updatedBeaconSignals.containsKey(beaconID)){
            updateSignalStrength(updatedBeaconSignals, beaconID);
            updatedBeaconSignals.remove(beaconID);
        }else{
            fillMissingSignalStrengthsWithZero(beaconID);
        }
    }

    private void fillMissingSignalStrengthsWithZero(BeaconID beaconID) {
        List<Integer> signalStrengths = beacons2SignalStrength.get(beaconID);
        signalStrengths = addSignalStrength(beaconID, 0, signalStrengths);
        int average = calculateAverageSignalStrength(signalStrengths);
        signalStrengthAvg.put(beaconID, average);
    }

    private void addNewSignalStrength(Map.Entry<BeaconID, Integer> entry) {
        BeaconID beaconID = entry.getKey();
        List<Integer> signalStrengths = beacons2SignalStrength.get(beaconID);
        Integer signalStrength = entry.getValue();
        signalStrengths = addSignalStrength(beaconID, signalStrength, signalStrengths);
        int average = calculateAverageSignalStrength(signalStrengths);
        signalStrengthAvg.put(beaconID, average);
    }

    private void updateSignalStrength(Map<BeaconID, Integer> updatedBeaconSignals, BeaconID beaconID) {
        List<Integer> signalStrengths = beacons2SignalStrength.get(beaconID);
        Integer signalStrength = updatedBeaconSignals.get(beaconID);
        signalStrengths = addSignalStrength(beaconID, signalStrength, signalStrengths);
        int average = calculateAverageSignalStrength(signalStrengths);
        signalStrengthAvg.put(beaconID, average);
    }

    private BeaconID retrieveBeaconIDWithMaxAverageSignalStrength(Map<BeaconID, Integer> signalStrengthAvg) {
        return Collections.max(signalStrengthAvg.entrySet(), (entry1, entry2) -> entry1.getValue() - entry2.getValue()).getKey();
    }

    private List<Integer> addSignalStrength(BeaconID beaconID, Integer signalStrength, List<Integer> signalStrenghts) {
        if(signalStrenghts == null) {
            signalStrenghts = Collections.nCopies(N, 0).stream().collect(Collectors.toCollection(ArrayList::new));
        }
        signalStrenghts.remove(0);
        addSignalStrengthToMap(beaconID, signalStrength, signalStrenghts);
        //Log.d("BEACONID:", beaconID.toString());
        //Log.d("SIGNALSTRENGTHS: ", signalStrenghts.toString());
        //Log.d("GESAMT: ", beacons2SignalStrength.toString());
        System.out.println("BEACONID:::" + beaconID.toString());
        System.out.println("SIGNALSTRENGTHS:::" + signalStrenghts.toString());
        System.out.println("GESAMT:::" + beacons2SignalStrength.toString());
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
