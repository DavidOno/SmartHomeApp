package de.smarthome.beacons;

import org.altbeacon.beacon.Identifier;

import java.util.Objects;

public class BeaconID {
    private Identifier uuid;
    private Identifier minor;
    private Identifier major;

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
