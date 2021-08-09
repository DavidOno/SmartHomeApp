package de.smarthome.beacons.nearest;

import java.util.Map;

import de.smarthome.beacons.BeaconID;

/**
 * Retrieves nearest beacon based on the last signal measurement only.
 */
public class SingleBestStrategy implements NearestBeaconStrategy {
    /**
     * @inheritDoc
     */
    @Override
    public BeaconID getNearest(Map<BeaconID, Integer> updatedBeaconSignals) {
        return updatedBeaconSignals.entrySet().stream().max((e1, e2) -> e1.getValue() > e2.getValue() ? 1 : -1).get().getKey();
    }
}
