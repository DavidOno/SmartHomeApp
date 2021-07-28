package de.smarthome.beacons;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import de.smarthome.beacons.nearest.RetrievingStrategy;
import de.smarthome.beacons.nearest.ThresholderStrategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class RetrievingTests {

    @Test
    public void testStrongestBeaconConsistency_strongestIsConsistent() {
        RetrievingStrategy strategy = new ThresholderStrategy();
        Map<BeaconID, Integer> newSignals = new HashMap<>();
        BeaconID beaconID1 = mock(BeaconID.class);
        BeaconID beaconID2 = mock(BeaconID.class);
        newSignals.put(beaconID1, -50);
        newSignals.put(beaconID2, -90);
        BeaconID nearest = strategy.getNearest(newSignals);

        assertThat(nearest).isEqualTo(beaconID1);
    }

    @Test
    public void testStrongestBeaconConsistency_strongestIsConsistent2() {
        RetrievingStrategy strategy = new ThresholderStrategy();
        Map<BeaconID, Integer> newSignals = new HashMap<>();
        BeaconID beaconID1 = mock(BeaconID.class);
        BeaconID beaconID2 = mock(BeaconID.class);
        newSignals.put(beaconID1, -90);
        newSignals.put(beaconID2, -50);
        BeaconID nearest = strategy.getNearest(newSignals);

        assertThat(nearest).isEqualTo(beaconID2);
    }

    @Test
    public void testStrongestBeaconConsistency_strongestIsConsistent3() {
        RetrievingStrategy strategy = new ThresholderStrategy();
        Map<BeaconID, Integer> newSignals;
        newSignals = new HashMap<>();
        BeaconID beaconID1 = mock(BeaconID.class);
        BeaconID beaconID2 = mock(BeaconID.class);
        newSignals.put(beaconID1, -50);
        newSignals.put(beaconID2, -90);
        strategy.getNearest(newSignals);

        newSignals = new HashMap<>();
        newSignals.put(beaconID1, -50);
        newSignals.put(beaconID2, -90);
        BeaconID nearest = strategy.getNearest(newSignals);

        assertThat(nearest).isEqualTo(beaconID1);
    }

    @Test
    public void testStrongestBeaconConsistency_strongestIsNOTConsistent() {
        RetrievingStrategy strategy = new ThresholderStrategy();
        Map<BeaconID, Integer> newSignals;
        newSignals = new HashMap<>();
        BeaconID beaconID1 = mock(BeaconID.class);
        BeaconID beaconID2 = mock(BeaconID.class);
        newSignals.put(beaconID1, -50);
        newSignals.put(beaconID2, -90);
        strategy.getNearest(newSignals);

        newSignals = new HashMap<>();
        newSignals.put(beaconID1, -90);
        newSignals.put(beaconID2, -50);
        BeaconID nearest = strategy.getNearest(newSignals);

        assertThat(nearest).isNull();
    }

    @Test
    public void testStrongestBeaconConsistency_strongestIsNOTConsistent2() {
        RetrievingStrategy strategy = new ThresholderStrategy();
        Map<BeaconID, Integer> newSignals;
        newSignals = new HashMap<>();
        BeaconID beaconID1 = mock(BeaconID.class);
        BeaconID beaconID2 = mock(BeaconID.class);
        newSignals.put(beaconID1, -50);
        newSignals.put(beaconID2, -90);
        strategy.getNearest(newSignals);

        newSignals = new HashMap<>();
        newSignals.put(beaconID1, -90);
        newSignals.put(beaconID2, -50);
        strategy.getNearest(newSignals);

        newSignals = new HashMap<>();
        newSignals.put(beaconID1, -90);
        newSignals.put(beaconID2, -50);
        BeaconID nearest = strategy.getNearest(newSignals);

        assertThat(nearest).isNull();
    }

    @Test
    public void testStrongestBeaconConsistency_strongestIsNOTConsistent3() {
        RetrievingStrategy strategy = new ThresholderStrategy();
        Map<BeaconID, Integer> newSignals;
        newSignals = new HashMap<>();
        BeaconID beaconID1 = mock(BeaconID.class);
        BeaconID beaconID2 = mock(BeaconID.class);
        newSignals.put(beaconID1, -50);
        newSignals.put(beaconID2, -90);
        strategy.getNearest(newSignals);

        newSignals = new HashMap<>();
        newSignals.put(beaconID1, -90);
        newSignals.put(beaconID2, -50);
        strategy.getNearest(newSignals);

        newSignals = new HashMap<>();
        newSignals.put(beaconID1, -50);
        newSignals.put(beaconID2, -90);
        BeaconID nearest = strategy.getNearest(newSignals);

        assertThat(nearest).isNull();
    }

    @Test
    public void testStrongestBeaconConsistency_strongestIsConsistentWithEqualSignal() {
        RetrievingStrategy strategy = new ThresholderStrategy();
        Map<BeaconID, Integer> newSignals;
        newSignals = new HashMap<>();
        BeaconID beaconID1 = mock(BeaconID.class);
        BeaconID beaconID2 = mock(BeaconID.class);
        newSignals.put(beaconID1, -90);
        newSignals.put(beaconID2, -90);
        strategy.getNearest(newSignals);

        newSignals = new HashMap<>();
        newSignals.put(beaconID1, -90);
        newSignals.put(beaconID2, -50);
        BeaconID nearest = strategy.getNearest(newSignals);

        assertThat(nearest).isEqualTo(beaconID2);
    }

    @Test
    public void testStrongestBeaconConsistency_WithAllSignalsEqual() {
        RetrievingStrategy strategy = new ThresholderStrategy();
        Map<BeaconID, Integer> newSignals;
        newSignals = new HashMap<>();
        BeaconID beaconID1 = mock(BeaconID.class);
        BeaconID beaconID2 = mock(BeaconID.class);
        newSignals.put(beaconID1, -90);
        newSignals.put(beaconID2, -90);
        strategy.getNearest(newSignals);

        newSignals = new HashMap<>();
        newSignals.put(beaconID1, -90);
        newSignals.put(beaconID2, -90);
        BeaconID nearest = strategy.getNearest(newSignals);

        assertThat(nearest).isNotNull();
    }

    @Test
    public void testStrongestBeaconConsistency_consistentOverHistory() {
        RetrievingStrategy strategy = new ThresholderStrategy();
        Map<BeaconID, Integer> newSignals;
        newSignals = new HashMap<>();
        BeaconID beaconID1 = mock(BeaconID.class);
        BeaconID beaconID2 = mock(BeaconID.class);
        newSignals.put(beaconID1, -50);
        newSignals.put(beaconID2, -90);
        for(int i = 0; i < ThresholderStrategy.SIGNAL_HISTORY_LENGTH; i++) {
            newSignals = collectNewConsistentSignals(strategy, newSignals, beaconID1, beaconID2);
        }
        BeaconID nearest = strategy.getNearest(newSignals);

        assertThat(nearest).isEqualTo(beaconID2);
    }

    private Map<BeaconID, Integer> collectNewConsistentSignals(RetrievingStrategy strategy, Map<BeaconID, Integer> newSignals, BeaconID beaconID1, BeaconID beaconID2) {
        strategy.getNearest(newSignals);

        newSignals = new HashMap<>();
        newSignals.put(beaconID1, -90);
        newSignals.put(beaconID2, -50);
        return newSignals;
    }

    @Test
    public void testStrongestBeaconConsistency_disapperingBeacon() {
        RetrievingStrategy strategy = new ThresholderStrategy();
        Map<BeaconID, Integer> newSignals;
        newSignals = new HashMap<>();
        BeaconID beaconID1 = mock(BeaconID.class);
        BeaconID beaconID2 = mock(BeaconID.class);
        newSignals.put(beaconID1, -90);
        newSignals.put(beaconID2, -50);
        strategy.getNearest(newSignals);

        newSignals = new HashMap<>();
        newSignals.put(beaconID2, -90);
        BeaconID nearest = strategy.getNearest(newSignals);

        assertThat(nearest).isEqualTo(beaconID2);
    }

    @Test
    public void testStrongestBeaconConsistency_disapperingBeacon2() {
        RetrievingStrategy strategy = new ThresholderStrategy();
        Map<BeaconID, Integer> newSignals;
        newSignals = new HashMap<>();
        BeaconID beaconID1 = mock(BeaconID.class);
        BeaconID beaconID2 = mock(BeaconID.class);
        newSignals.put(beaconID1, -90);
        newSignals.put(beaconID2, -50);
        strategy.getNearest(newSignals);

        newSignals = new HashMap<>();
        newSignals.put(beaconID1, -90);
        BeaconID nearest = strategy.getNearest(newSignals);

        assertThat(nearest).isNull();
    }
}
