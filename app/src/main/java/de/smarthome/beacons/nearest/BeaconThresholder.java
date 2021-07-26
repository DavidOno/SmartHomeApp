package de.smarthome.beacons.nearest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.smarthome.beacons.BeaconID;

public class BeaconThresholder{

    private final BeaconID beaconID;
    private final List<Integer> signalHistory;

    public BeaconThresholder(BeaconID beaconID, List<Integer> signals) {
        this.beaconID = beaconID;
        this.signalHistory = createHistory(signals);
    }

    private List<Integer> createHistory(List<Integer> signals) {
        List<Integer> history = new ArrayList<>();
        int numberOfTrailingZeros = ThresholderStrategy.SIGNAL_HISTORY_LENGTH - signals.size();
        for(int i = 0; i < numberOfTrailingZeros; i++){
            history.add(0);
        }
        for(Integer signal: signals){
            history.add(signal);
        }
        if(history.size() > ThresholderStrategy.SIGNAL_HISTORY_LENGTH){
            throw new IllegalStateException("history too long");
        }
        return history;
    }


    public List<Integer> getLastNSignals(int n){
        if(signalHistory.size() < n){
            throw new IllegalArgumentException("More elements from history requested than stored.");
        }
        return signalHistory.stream().skip(Math.max(0, signalHistory.size() - n)).collect(Collectors.toList());
    }

    public boolean isAtLeastOnceLowerThan(BeaconThresholder o, int signalHistoryLength){
        List<Integer> lastNSignals = getLastNSignals(signalHistoryLength);
        List<Integer> otherLastNSignals = o.getLastNSignals(signalHistoryLength);
        for(int i = 0; i < signalHistoryLength; i++){
            if(lastNSignals.get(i) < otherLastNSignals.get(i)){
                return true;
            }
        }
        return false;
    }

    public BeaconID getBeaconID() {
        return beaconID;
    }

    public void addSignal(Integer signalStrength) {
        signalHistory.add(signalStrength);
        if(signalHistory.size() > ThresholderStrategy.SIGNAL_HISTORY_LENGTH){
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
