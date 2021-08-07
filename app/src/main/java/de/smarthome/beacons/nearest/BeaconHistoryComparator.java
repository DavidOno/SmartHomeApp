package de.smarthome.beacons.nearest;

import java.util.Comparator;
import java.util.List;

/**
 * Comparator for beacon signal histories
 */
public class BeaconHistoryComparator implements Comparator<BeaconSignalHistory> {

    /**
     * Compares two beacon histories
     * @param o1 First beacon history
     * @param o2 Second beacon history
     * @return -1 if all signals in first history are greater than in the second one. 1 if the first
     * one is lower than the second one otherwise 0.
     */
    @Override
    public int compare(BeaconSignalHistory o1, BeaconSignalHistory o2) {
        List<Integer> firstLastNSignals = o1.getLastNSignals(HistoryBestStrategy.SIGNAL_HISTORY_LENGTH);
        List<Integer> secondLastNSignals1 = o2.getLastNSignals(HistoryBestStrategy.SIGNAL_HISTORY_LENGTH);
        State state = new GreaterState();
        for(int i = 0; i < HistoryBestStrategy.SIGNAL_HISTORY_LENGTH; i++){
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

    /**
     * Specifies the current state within the comparison process.
     */
    private interface State {
        State compare(int a, int b);
        int getReturnValue();
    }

    private static class GreaterState implements State {
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

    private static class EqualState implements State {

        @Override
        public State compare(int a, int b) {
            return this;
        }

        @Override
        public int getReturnValue() {
            return 0;
        }
    }

    private static class LowerState implements State {
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
