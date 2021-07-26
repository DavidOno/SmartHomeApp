package de.smarthome.beacons.nearest;

import java.util.Map;

import de.smarthome.beacons.BeaconID;

public interface RetrievingStrategy {
    BeaconID getNearest(Map<BeaconID, Integer> updatedBeaconSignals);
}
