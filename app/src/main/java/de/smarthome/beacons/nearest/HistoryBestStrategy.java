package de.smarthome.beacons.nearest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.smarthome.beacons.BeaconID;

/**
 * Retrieves nearest beacon based on a history of beacon signals.
 */
public class HistoryBestStrategy implements NearestBeaconStrategy {

    public static final int SIGNAL_HISTORY_LENGTH = 3;

    private final List<BeaconSignalHistory> beaconSignalHistories = new ArrayList<>();

    /**
     * @inheritDoc
     */
    @Override
    public BeaconID getNearest(Map<BeaconID, Integer> newBeaconSignals) {
        updateHistory(newBeaconSignals);
        beaconSignalHistories.sort(new BeaconHistoryComparator());

        if(isHistoryNotEmpty() && isStrongestBeaconConsistent(beaconSignalHistories)){
            return beaconSignalHistories.get(0).getBeaconID();
        }
        return null;
    }

    private boolean isHistoryNotEmpty() {
        return !beaconSignalHistories.isEmpty();
    }

    private boolean isStrongestBeaconConsistent(List<BeaconSignalHistory> beaconSignalHistories) {
        final BeaconSignalHistory strongestBeacon = beaconSignalHistories.get(0);
        return beaconSignalHistories.stream().skip(1).noneMatch(beaconSignalHistory -> beaconSignalHistory.isAtLeastOnceGreaterThan(strongestBeacon, SIGNAL_HISTORY_LENGTH));
    }

    private void updateHistory(Map<BeaconID, Integer> updatedBeaconSignals) {
        for(BeaconSignalHistory beaconSignalHistory : beaconSignalHistories){
            Integer signalStrength = updatedBeaconSignals.remove(beaconSignalHistory.getBeaconID());
            if(signalStrength != null){
                beaconSignalHistory.addSignal(signalStrength);
            }else{
                beaconSignalHistory.addSignal(-100);
            }
        }
        for(Map.Entry<BeaconID, Integer> entry : updatedBeaconSignals.entrySet()){
            ArrayList<Integer> signals = new ArrayList<>();
            signals.add(entry.getValue());
            BeaconSignalHistory newBeaconThreshold = new BeaconSignalHistory(entry.getKey(), signals);
            beaconSignalHistories.add(newBeaconThreshold);
        }
    }
}
