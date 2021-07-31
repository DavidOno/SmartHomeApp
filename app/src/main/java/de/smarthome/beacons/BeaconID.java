package de.smarthome.beacons;

import org.altbeacon.beacon.Identifier;

import java.util.Objects;

/**
 * This class creates a beacon object.
 * The object is used to save all information to clearly identify a bluetooth beacon.
 */
public class BeaconID {
    private final Identifier uuid;
    private final Identifier minor;
    private final Identifier major;

    /**
     * Constructor to build the object BeaconID.
     * The BeaconID is unambiguously used for every known beacon.
     * @param uuid Universally Unique Identifier. Identifier of the scanned beacon.
     * @param minor Identifier for greater accuracy. Identify and distinguish group of beacons.
     * @param major Identifier for greater accuracy. Identify and distinguish an individual.
     */
    BeaconID(Identifier uuid, Identifier minor, Identifier major) {
        this.uuid = uuid;
        this.minor = minor;
        this.major = major;
    }

    @Override
    public String toString() {
        return "BeaconID{" +
                "uuid=" + uuid +
                ", minor=" + minor +
                ", major=" + major +
                '}';
    }

    public String getId() {
        return uuid.toString() + major.toString() + minor.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BeaconID beaconID = (BeaconID) o;
        return uuid.equals(beaconID.uuid) &&
                minor.equals(beaconID.minor) &&
                major.equals(beaconID.major);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, minor, major);
    }
}
