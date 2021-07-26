package de.smarthome.beacons;

import android.util.Log;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import de.smarthome.app.model.Location;
import de.smarthome.app.model.UIConfig;
import de.smarthome.beacons.nearest.RetrievingStrategy;

public class BeaconLocationManager {
    private static final String TAG = "BeaconLocationManager";
    private static final int N = 5;

    private BeaconID nearestBeacon;
    private Map<BeaconID, List<Integer>> beacons2SignalStrength = new HashMap<>();
    private Map<BeaconID, Integer> signalStrengthAvg = new HashMap<>();
    private UIConfig uiConfig;
    private BeaconLocations locationConfig;
    private BeaconObserver beaconObserver;
    private RetrievingStrategy retrievingStrategy;

    public BeaconLocationManager(UIConfig newUIConfig, BeaconLocations newBeaconConfig, RetrievingStrategy retrievingStrategy) {
        this.uiConfig = newUIConfig;
        this.locationConfig = newBeaconConfig;
        this.retrievingStrategy = retrievingStrategy;
    }

    /**
     *
     * @param updatedBeaconSignals
     */
    void updateCurrentLocation(Map<BeaconID, Integer> updatedBeaconSignals) {
        System.out.println("UPDATE:"+updatedBeaconSignals);
        nearestBeacon = retrievingStrategy.getNearest(updatedBeaconSignals);
        if(nearestBeacon != null) {
            System.out.println("NEARESTBEACON::: " + nearestBeacon.toString());
            Optional<Location> currentLocation = getLocation(nearestBeacon);
            beaconObserver.updateLocation(currentLocation);
        }else{
            System.out.println("NEARESTBEACON::: no nearest beacon found");
        }
    }

    private BeaconID retrieveBeaconIdWithMaxRssi(Map<BeaconID, Integer> updatedBeaconSignals) {
        return updatedBeaconSignals.entrySet().stream().max((e1, e2) -> e1.getValue() > e2.getValue() ? 1:-1).get().getKey();
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

    private String uiConfigString = "{\n" +
            "    \"functions\": [\n" +
            "        {\n" +
            "            \"functionType\": \"de.gira.schema.functions.Switch\",\n" +
            "            \"channelType\": \"de.gira.schema.channels.Switch\",\n" +
            "            \"displayName\": \"Tischleuchte_schalten\",\n" +
            "            \"uid\": \"aael\",\n" +
            "            \"dataPoints\": [\n" +
            "                {\n" +
            "                    \"uid\": \"aauy\",\n" +
            "                    \"name\": \"OnOff\"\n" +
            "                }\n" +
            "            ]\n" +
            "        },\n" +
            "        {\n" +
            "            \"functionType\": \"de.gira.schema.functions.Switch\",\n" +
            "            \"channelType\": \"de.gira.schema.channels.Switch\",\n" +
            "            \"displayName\": \"Tischleuchte_Status\",\n" +
            "            \"uid\": \"aae4\",\n" +
            "            \"dataPoints\": [\n" +
            "                {\n" +
            "                    \"uid\": \"aajc\",\n" +
            "                    \"name\": \"OnOff\"\n" +
            "                }\n" +
            "            ]\n" +
            "        },\n" +
            "        {\n" +
            "            \"functionType\": \"de.gira.schema.functions.Switch\",\n" +
            "            \"channelType\": \"de.gira.schema.channels.Switch\",\n" +
            "            \"displayName\": \"Stehleuchte_schalten\",\n" +
            "            \"uid\": \"aaet\",\n" +
            "            \"dataPoints\": [\n" +
            "                {\n" +
            "                    \"uid\": \"aat8\",\n" +
            "                    \"name\": \"OnOff\"\n" +
            "                }\n" +
            "            ]\n" +
            "        },\n" +
            "        {\n" +
            "            \"functionType\": \"de.gira.schema.functions.Switch\",\n" +
            "            \"channelType\": \"de.gira.schema.channels.Switch\",\n" +
            "            \"displayName\": \"Stehleuchte_Status\",\n" +
            "            \"uid\": \"aae2\",\n" +
            "            \"dataPoints\": [\n" +
            "                {\n" +
            "                    \"uid\": \"aajb\",\n" +
            "                    \"name\": \"OnOff\"\n" +
            "                }\n" +
            "            ]\n" +
            "        },\n" +
            "        {\n" +
            "            \"functionType\": \"de.gira.schema.functions.KNX.HeatingCooling\",\n" +
            "            \"channelType\": \"de.gira.schema.channels.RoomTemperatureSwitchable\",\n" +
            "            \"displayName\": \"Temperatur_Wohnen\",\n" +
            "            \"uid\": \"aae8\",\n" +
            "            \"dataPoints\": [\n" +
            "                {\n" +
            "                    \"uid\": \"aajf\",\n" +
            "                    \"name\": \"Set-Point\"\n" +
            "                }\n" +
            "            ]\n" +
            "        },\n" +
            "        {\n" +
            "            \"functionType\": \"de.gira.schema.functions.Covering\",\n" +
            "            \"channelType\": \"de.gira.schema.channels.BlindWithPos\",\n" +
            "            \"displayName\": \"Markise_bewegen\",\n" +
            "            \"uid\": \"aafe\",\n" +
            "            \"dataPoints\": [\n" +
            "                {\n" +
            "                    \"uid\": \"aajv\",\n" +
            "                    \"name\": \"Step-Up-Down\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"uid\": \"aaju\",\n" +
            "                    \"name\": \"Up-Down\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"uid\": \"aajw\",\n" +
            "                    \"name\": \"Position\"\n" +
            "                }\n" +
            "            ]\n" +
            "        },\n" +
            "        {\n" +
            "            \"functionType\": \"de.gira.schema.functions.Covering\",\n" +
            "            \"channelType\": \"de.gira.schema.channels.BlindWithPos\",\n" +
            "            \"displayName\": \"Markise_Status\",\n" +
            "            \"uid\": \"aafg\",\n" +
            "            \"dataPoints\": [\n" +
            "                {\n" +
            "                    \"uid\": \"aajv\",\n" +
            "                    \"name\": \"Step-Up-Down\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"uid\": \"aaju\",\n" +
            "                    \"name\": \"Up-Down\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"uid\": \"aajx\",\n" +
            "                    \"name\": \"Position\"\n" +
            "                }\n" +
            "            ]\n" +
            "        }\n" +
            "    ],\n" +
            "    \"uid\": \"cczk\",\n" +
            "    \"locations\": [\n" +
            "        {\n" +
            "            \"functions\": [],\n" +
            "            \"displayName\": \"Shop\",\n" +
            "            \"uid\": \"aaei\",\n" +
            "            \"$type\": \"4\",\n" +
            "            \"locations\": [\n" +
            "                {\n" +
            "                    \"functions\": [\n" +
            "                        \"aael\",\n" +
            "                        \"aae4\"\n" +
            "                    ],\n" +
            "                    \"displayName\": \"Bereich Essen\",\n" +
            "                    \"uid\": \"aaej\",\n" +
            "                    \"$type\": \"4\",\n" +
            "                    \"locations\": [],\n" +
            "                    \"locationType\": \"Room\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"functions\": [\n" +
            "                        \"aaet\",\n" +
            "                        \"aae2\",\n" +
            "                        \"aae8\"\n" +
            "                    ],\n" +
            "                    \"displayName\": \"Bereich Wohnen\",\n" +
            "                    \"uid\": \"aaex\",\n" +
            "                    \"$type\": \"4\",\n" +
            "                    \"locations\": [],\n" +
            "                    \"locationType\": \"Room\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"functions\": [\n" +
            "                        \"aafe\",\n" +
            "                        \"aafg\"\n" +
            "                    ],\n" +
            "                    \"displayName\": \"Terrasse\",\n" +
            "                    \"uid\": \"aafb\",\n" +
            "                    \"$type\": \"4\",\n" +
            "                    \"locations\": [],\n" +
            "                    \"locationType\": \"Room\"\n" +
            "                }\n" +
            "            ],\n" +
            "            \"locationType\": \"Floor\"\n" +
            "        }\n" +
            "    ]\n" +
            "}";

    private String locationConfigString = "{\n" +
            "\t\"locations\": [\n" +
            "\t\t{\n" +
            "\t\t\t\"roomUID\": \"aaei\",\n" +
            "\t\t\t\"beaconId\": \"ebefd083-70a2-47c8-9837-e7b5634df55570\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"roomUID\": \"aacads\",\n" +
            "\t\t\t\"beaconId\": \"xyz\"\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"roomUID\": \"fdasdf\",\n" +
            "\t\t\t\"beaconId\": \"qwert\"\n" +
            "\t\t}\n" +
            "\t]\n" +
            "}";
}
