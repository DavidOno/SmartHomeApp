package de.smarthome.beacons.nearest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.smarthome.beacons.BeaconID;

public class ThresholderStrategy implements RetrievingStrategy{

    public static final int SIGNAL_HISTORY_LENGTH = 5;

    private final List<BeaconThresholder> beaconThresholders = new ArrayList<>();

    @Override
    public BeaconID getNearest(Map<BeaconID, Integer> newBeaconSignals) {
        updateHistory(newBeaconSignals);
        beaconThresholders.sort(new TimeThresholdComparator());
        if(isStrongestBeaconConsistent(beaconThresholders)){
            return beaconThresholders.get(0).getBeaconID();
        }
        return null;
    }

    private boolean isStrongestBeaconConsistent(List<BeaconThresholder> beaconThresholders) {
        final BeaconThresholder strongestBeacon = beaconThresholders.get(0);
        return beaconThresholders.stream().skip(1).noneMatch(beaconThresholder -> beaconThresholder.isAtLeastOnceLowerThan(strongestBeacon, SIGNAL_HISTORY_LENGTH));
    }

    private void updateHistory(Map<BeaconID, Integer> updatedBeaconSignals) {
        for(BeaconThresholder beaconThresholder : beaconThresholders){
            Integer signalStrength = updatedBeaconSignals.remove(beaconThresholder.getBeaconID());
            if(signalStrength != null){
                beaconThresholder.addSignal(signalStrength);
            }else{
                beaconThresholder.addSignal(0);
            }
        }
        for(Map.Entry<BeaconID, Integer> entry : updatedBeaconSignals.entrySet()){
            ArrayList<Integer> signals = new ArrayList<>();
            signals.add(entry.getValue());
            BeaconThresholder newBeaconThreshold = new BeaconThresholder(entry.getKey(), signals);
            beaconThresholders.add(newBeaconThreshold);
        }
        System.out.println("::::"+beaconThresholders);
    }
}
