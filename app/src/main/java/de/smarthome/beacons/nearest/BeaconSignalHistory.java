package de.smarthome.beacons.nearest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.smarthome.beacons.BeaconID;

/**
 * Keeps record of last n beacon signals for a specific beaconID
 */
public class BeaconSignalHistory {

    private final BeaconID beaconID;
    private final List<Integer> signalHistory;

    public BeaconSignalHistory(BeaconID beaconID, List<Integer> signals) {
        this.beaconID = beaconID;
        this.signalHistory = createHistory(signals);
    }

    private List<Integer> createHistory(List<Integer> signals) {
        List<Integer> history = new ArrayList<>();
        int numberOfTrailingZeros = HistoryBestStrategy.SIGNAL_HISTORY_LENGTH - signals.size();
        for(int i = 0; i < numberOfTrailingZeros; i++){
            history.add(-100);
        }
        for(Integer signal: signals){
            history.add(signal);
        }
        if(history.size() > HistoryBestStrategy.SIGNAL_HISTORY_LENGTH){
            throw new IllegalStateException("history too long");
        }
        return history;
    }

    /**
     * Returns last n beacon signals.
     * @param n Number of last beacon signal. Should not be greater than signal history.
     * @return List of last n beacon signals
     */
    public List<Integer> getLastNSignals(int n){
        if(signalHistory.size() < n){
            throw new IllegalArgumentException("More elements from history requested than stored.");
        }
        return signalHistory.stream().skip(Math.max(0, signalHistory.size() - n)).collect(Collectors.toList());
    }

    /**
     * Checks if own signal history is at least once greater than given signal history based on
     * n signals.
     * @param o Given signal history from another beacon.
     * @param signalHistoryLength Length of signals which are compared.
     * @return True if own history is at least greater than given signal history otherwise false.
     */
    public boolean isAtLeastOnceGreaterThan(BeaconSignalHistory o, int signalHistoryLength){
        List<Integer> lastNSignals = getLastNSignals(signalHistoryLength);
        List<Integer> otherLastNSignals = o.getLastNSignals(signalHistoryLength);
        for(int i = 0; i < signalHistoryLength; i++){
            if(lastNSignals.get(i) > otherLastNSignals.get(i)){
                return true;
            }
        }
        return false;
    }

    public BeaconID getBeaconID() {
        return beaconID;
    }

    /**
     * Adds new signal to signal history and removes oldest one.
     * @param signalStrength Current signal strength
     */
    public void addSignal(Integer signalStrength) {
        signalHistory.add(signalStrength);
        if(signalHistory.size() > HistoryBestStrategy.SIGNAL_HISTORY_LENGTH){
            signalHistory.remove(0);
        }
    }

    @Override
    public String toString() {
        return "\nBeaconThresholder{" +
                "beaconID=" + beaconID +
                ", signalHistory=" + signalHistory +
                '}';
    }
}
