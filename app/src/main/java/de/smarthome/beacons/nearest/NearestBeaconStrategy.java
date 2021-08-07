package de.smarthome.beacons.nearest;

import java.util.Map;

import de.smarthome.beacons.BeaconID;

/**
 * Specifies behaviour to select nearest beacon
 */
public interface NearestBeaconStrategy {
    /**
     * Retrieves nearest beacon while updating history with new beacon signals
     * @param updatedBeaconSignals Newly scanned beacon signals
     * @return Nearest beacon
     */
    BeaconID getNearest(Map<BeaconID, Integer> updatedBeaconSignals);
}
