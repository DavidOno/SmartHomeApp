package de.smarthome.beacons;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import de.smarthome.beacons.nearest.RetrievingStrategy;
import de.smarthome.beacons.nearest.SingleBestStrategy;
import de.smarthome.beacons.nearest.ThresholderStrategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class SingleBestTest {
    @Test
    public void testStrongestBeaconConsistency_twoBeaconComparison() {
        RetrievingStrategy strategy = new SingleBestStrategy();
        Map<BeaconID, Integer> newSignals = new HashMap<>();
        BeaconID beaconID1 = mock(BeaconID.class);
        BeaconID beaconID2 = mock(BeaconID.class);
        newSignals.put(beaconID1, -50);
        newSignals.put(beaconID2, -90);
        BeaconID nearest = strategy.getNearest(newSignals);

        assertThat(nearest).isEqualTo(beaconID1);
    }

    @Test
    public void testStrongestBeaconConsistency_threeBeaconComparison() {
        RetrievingStrategy strategy = new SingleBestStrategy();
        Map<BeaconID, Integer> newSignals = new HashMap<>();
        BeaconID beaconID1 = mock(BeaconID.class);
        BeaconID beaconID2 = mock(BeaconID.class);
        BeaconID beaconID3 = mock(BeaconID.class);
        newSignals.put(beaconID1, -50);
        newSignals.put(beaconID2, -99);
        newSignals.put(beaconID3, -30);
        BeaconID nearest = strategy.getNearest(newSignals);

        assertThat(nearest).isEqualTo(beaconID3);
    }
}
