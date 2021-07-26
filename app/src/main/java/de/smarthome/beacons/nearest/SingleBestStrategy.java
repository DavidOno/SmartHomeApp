package de.smarthome.beacons.nearest;

import java.util.Map;

import de.smarthome.beacons.BeaconID;

public class SingleBestStrategy implements RetrievingStrategy{
    @Override
    public BeaconID getNearest(Map<BeaconID, Integer> updatedBeaconSignals) {
        return updatedBeaconSignals.entrySet().stream().min((e1, e2) -> e1.getValue() > e2.getValue() ? 1 : -1).get().getKey();
    }
}
