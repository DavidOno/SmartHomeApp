package de.smarthome.beacons.nearest;

import java.util.Comparator;
import java.util.List;

public class TimeThresholdComparator implements Comparator<BeaconThresholder> {

    @Override
    public int compare(BeaconThresholder o1, BeaconThresholder o2) {
        List<Integer> firstLastNSignals = o1.getLastNSignals(ThresholderStrategy.SIGNAL_HISTORY_LENGTH);
        List<Integer> secondLastNSignals1 = o2.getLastNSignals(ThresholderStrategy.SIGNAL_HISTORY_LENGTH);
        State state = new GreaterState();
        for(int i = 0; i < ThresholderStrategy.SIGNAL_HISTORY_LENGTH; i++){
            State newState = state.compare(firstLastNSignals.get(i), secondLastNSignals1.get(i));
            if(newState != state){
                i = resetCounter();
                state = newState;
            }
            if(newState instanceof EqualState){
                break;
            }
        }
        return state.getReturnValue();
    }

    private int resetCounter() {
        return -1;
    }

    public static interface State {
        State compare(int a, int b);
        int getReturnValue();
    }

    public static class GreaterState implements State {
        private final State nextState = new LowerState();

        @Override
        public State compare(int a, int b) {
            if(a < b){
                return nextState;
            }
            return this;
        }

        @Override
        public int getReturnValue() {
            return -1;
        }
    }

    public static class EqualState implements State {

        @Override
        public State compare(int a, int b) {
            return this;
        }

        @Override
        public int getReturnValue() {
            return 0;
        }
    }

    public static class LowerState implements State {
        private final State nextState = new EqualState();

        @Override
        public State compare(int a, int b) {
            if(a > b){
                return nextState;
            }
            return this;
        }

        @Override
        public int getReturnValue() {
            return 1;
        }
    }
}
