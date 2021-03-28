package de.smarthome.beacons;

import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.smarthome.model.impl.Location;
import de.smarthome.model.impl.UIConfig;

public class BeaconLocationManager {
    private static final String TAG = "BeaconLocationManager";
    private static final int N = 5;

    private BeaconID nearestBeacon;
    private Map<BeaconID, List<Integer>> beacons2SignalStrength = new HashMap<>();
    private Map<BeaconID, Integer> signalStrengthAvg = new HashMap<>();
    //TODO: Initialise
    private UIConfig uiConfig;
    //TODO: Initialise
    private BeaconLocations locationConfig;
    private BeaconObserver beaconObserver;

    public BeaconLocationManager(UIConfig newUIConfig, BeaconLocations newBeaconConfig) {
        this.uiConfig = newUIConfig;
        this.locationConfig = newBeaconConfig;
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
        Log.d(TAG, "nearestBeacon " + nearestBeacon.toString());

        Optional<Location> currentLocation = getLocation(nearestBeacon);

        beaconObserver.updateLocation(currentLocation);
        }

    private BeaconID retrieveBeaconIDWithMaximumSignalStrength(Map<BeaconID, Integer> signalStrengthAvg) {
        return Collections.max(signalStrengthAvg.entrySet(), (entry1, entry2) -> entry1.getValue() - entry2.getValue()).getKey();
    }

    private List<Integer> addSignalStrength(BeaconID beaconID, Integer signalStrength, List<Integer> signalStrenghts) {
        if(signalStrenghts == null) {
            signalStrenghts = new ArrayList<>();
        }else {
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
