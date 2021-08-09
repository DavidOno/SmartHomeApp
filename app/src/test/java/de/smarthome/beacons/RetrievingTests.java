package de.smarthome.beacons;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import de.smarthome.beacons.nearest.NearestBeaconStrategy;
import de.smarthome.beacons.nearest.HistoryBestStrategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class RetrievingTests {
    //ok
    @Test
    public void testStrongestBeaconConsistency_strongestIsConsistent1() {
        NearestBeaconStrategy strategy = new HistoryBestStrategy();
        Map<BeaconID, Integer> newSignals = new HashMap<>();
        BeaconID beaconID1 = mock(BeaconID.class);
        BeaconID beaconID2 = mock(BeaconID.class);
        newSignals.put(beaconID1, -50);
        newSignals.put(beaconID2, -90);
        BeaconID nearest = strategy.getNearest(newSignals);

        assertThat(nearest).isEqualTo(beaconID1);
    }
    //ok
    @Test
    public void testStrongestBeaconConsistency_strongestIsConsistent2() {
        NearestBeaconStrategy strategy = new HistoryBestStrategy();
        Map<BeaconID, Integer> newSignals = new HashMap<>();
        BeaconID beaconID1 = mock(BeaconID.class);
        BeaconID beaconID2 = mock(BeaconID.class);
        newSignals.put(beaconID1, -90);
        newSignals.put(beaconID2, -50);
        BeaconID nearest = strategy.getNearest(newSignals);

        assertThat(nearest).isEqualTo(beaconID2);
    }
    //ok
    @Test
    public void testStrongestBeaconConsistency_strongestIsConsistent3() {
        NearestBeaconStrategy strategy = new HistoryBestStrategy();
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
    //to do
    @Test
    public void testStrongestBeaconConsistency_strongestIsNOTConsistent() {
        NearestBeaconStrategy strategy = new HistoryBestStrategy();
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
        NearestBeaconStrategy strategy = new HistoryBestStrategy();
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
        NearestBeaconStrategy strategy = new HistoryBestStrategy();
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
        NearestBeaconStrategy strategy = new HistoryBestStrategy();
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
        NearestBeaconStrategy strategy = new HistoryBestStrategy();
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
        NearestBeaconStrategy strategy = new HistoryBestStrategy();
        Map<BeaconID, Integer> newSignals;
        newSignals = new HashMap<>();
        BeaconID beaconID1 = mock(BeaconID.class);
        BeaconID beaconID2 = mock(BeaconID.class);
        newSignals.put(beaconID1, -50);
        newSignals.put(beaconID2, -90);
        for(int i = 0; i < HistoryBestStrategy.SIGNAL_HISTORY_LENGTH; i++) {
            newSignals = collectNewConsistentSignals(strategy, newSignals, beaconID1, beaconID2);
        }
        BeaconID nearest = strategy.getNearest(newSignals);

        assertThat(nearest).isEqualTo(beaconID2);
    }

    private Map<BeaconID, Integer> collectNewConsistentSignals(NearestBeaconStrategy strategy, Map<BeaconID, Integer> newSignals, BeaconID beaconID1, BeaconID beaconID2) {
        strategy.getNearest(newSignals);

        newSignals = new HashMap<>();
        newSignals.put(beaconID1, -90);
        newSignals.put(beaconID2, -50);
        return newSignals;
    }

    @Test
    public void testStrongestBeaconConsistency_disappearingBeacon() {
        NearestBeaconStrategy strategy = new HistoryBestStrategy();
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
    public void testStrongestBeaconConsistency_disappearingBeacon2() {
        NearestBeaconStrategy strategy = new HistoryBestStrategy();
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
     //*********************************************************************************
     //Testing three beacons
     @Test
     public void testStrongestBeaconConsistency_threeBeacons_strongestIsConsistent1() {
         NearestBeaconStrategy strategy = new HistoryBestStrategy();
         Map<BeaconID, Integer> newSignals = new HashMap<>();
         BeaconID beaconID1 = mock(BeaconID.class);
         BeaconID beaconID2 = mock(BeaconID.class);
         BeaconID beaconID3 = mock(BeaconID.class);
         newSignals.put(beaconID1, -50);
         newSignals.put(beaconID2, -90);
         newSignals.put(beaconID3, -30);
         BeaconID nearest = strategy.getNearest(newSignals);

         assertThat(nearest).isEqualTo(beaconID3);
     }

    @Test
    public void testStrongestBeaconConsistency_threeBeacons_strongestIsConsistent2() {
        NearestBeaconStrategy strategy = new HistoryBestStrategy();
        Map<BeaconID, Integer> newSignals = new HashMap<>();
        BeaconID beaconID1 = mock(BeaconID.class);
        BeaconID beaconID2 = mock(BeaconID.class);
        BeaconID beaconID3 = mock(BeaconID.class);
        newSignals.put(beaconID1, -90);
        newSignals.put(beaconID2, -30);
        newSignals.put(beaconID3, -50);
        BeaconID nearest = strategy.getNearest(newSignals);

        assertThat(nearest).isEqualTo(beaconID2);
    }

    @Test
    public void testStrongestBeaconConsistency_threeBeacons_strongestIsConsistent3() {
        NearestBeaconStrategy strategy = new HistoryBestStrategy();
        Map<BeaconID, Integer> newSignals = new HashMap<>();
        BeaconID beaconID1 = mock(BeaconID.class);
        BeaconID beaconID2 = mock(BeaconID.class);
        BeaconID beaconID3 = mock(BeaconID.class);
        newSignals.put(beaconID1, -30);
        newSignals.put(beaconID2, -50);
        newSignals.put(beaconID3, -90);
        BeaconID nearest = strategy.getNearest(newSignals);

        assertThat(nearest).isEqualTo(beaconID1);
    }

    @Test
    public void testStrongestBeaconConsistency_threeBeacons_strongestIsConsistent4() {
        NearestBeaconStrategy strategy = new HistoryBestStrategy();
        Map<BeaconID, Integer> newSignals;
        newSignals = new HashMap<>();
        BeaconID beaconID1 = mock(BeaconID.class);
        BeaconID beaconID2 = mock(BeaconID.class);
        BeaconID beaconID3 = mock(BeaconID.class);
        newSignals.put(beaconID1, -30);
        newSignals.put(beaconID2, -90);
        newSignals.put(beaconID3, -50);
        strategy.getNearest(newSignals);

        newSignals = new HashMap<>();
        newSignals.put(beaconID1, -30);
        newSignals.put(beaconID2, -90);
        newSignals.put(beaconID3, -50);
        BeaconID nearest = strategy.getNearest(newSignals);

        assertThat(nearest).isEqualTo(beaconID1);
    }

    @Test
    public void testStrongestBeaconConsistency_threeBeacons_strongestIsConsistent_whenSignalsEqual() {
        NearestBeaconStrategy strategy = new HistoryBestStrategy();
        Map<BeaconID, Integer> newSignals;
        newSignals = new HashMap<>();
        BeaconID beaconID1 = mock(BeaconID.class);
        BeaconID beaconID2 = mock(BeaconID.class);
        BeaconID beaconID3 = mock(BeaconID.class);
        newSignals.put(beaconID1, -30);
        newSignals.put(beaconID2, -90);
        newSignals.put(beaconID3, -50);
        strategy.getNearest(newSignals);

        newSignals = new HashMap<>();
        newSignals.put(beaconID1, -30);
        newSignals.put(beaconID2, -30);
        newSignals.put(beaconID3, -50);
        BeaconID nearest = strategy.getNearest(newSignals);

        assertThat(nearest).isEqualTo(beaconID1);
    }

    @Test
    public void testStrongestBeaconConsistency_threeBeacons_strongestIsNOTConsistent() {
        NearestBeaconStrategy strategy = new HistoryBestStrategy();
        Map<BeaconID, Integer> newSignals;
        newSignals = new HashMap<>();
        BeaconID beaconID1 = mock(BeaconID.class);
        BeaconID beaconID2 = mock(BeaconID.class);
        BeaconID beaconID3 = mock(BeaconID.class);
        newSignals.put(beaconID1, -30);
        newSignals.put(beaconID2, -90);
        newSignals.put(beaconID3, -50);
        strategy.getNearest(newSignals);

        newSignals = new HashMap<>();
        newSignals.put(beaconID1, -30);
        newSignals.put(beaconID2, -29);
        newSignals.put(beaconID3, -50);
        BeaconID nearest = strategy.getNearest(newSignals);

        assertThat(nearest).isNull();
    }
}
