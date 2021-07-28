package de.smarthome.beacons;

import android.content.Context;

import java.io.File;
import java.io.FileWriter;
import java.util.Map;
import java.util.Optional;

import de.smarthome.app.model.Location;
import de.smarthome.app.model.UIConfig;
import de.smarthome.beacons.nearest.RetrievingStrategy;

public class BeaconLocationManager {
    private static final String TAG = "BeaconLocationManager";
    private final UIConfig uiConfig;
    private final BeaconLocations locationConfig;
    private BeaconObserver beaconObserver;
    private final RetrievingStrategy retrievingStrategy;
    private Context context;

    public BeaconLocationManager(Context context, UIConfig newUIConfig, BeaconLocations newBeaconConfig, RetrievingStrategy retrievingStrategy) {
        this.uiConfig = newUIConfig;
        this.locationConfig = newBeaconConfig;
        this.retrievingStrategy = retrievingStrategy;
        this.context = context; //TODO: remove this argument, only needed to write beacon signals to file
    }

    /**
     *
     * @param updatedBeaconSignals
     */
    void updateCurrentLocation(Map<BeaconID, Integer> updatedBeaconSignals) {
        System.out.println("UPDATE:"+updatedBeaconSignals);
        writeFileOnInternalStorage(context, "Beacon", "\nUpdate:::\n"+updatedBeaconSignals.toString());
        BeaconID nearestBeacon = retrievingStrategy.getNearest(updatedBeaconSignals);

        if(nearestBeacon != null) {
            System.out.println("NEARESTBEACON::: " + nearestBeacon.toString());
            writeFileOnInternalStorage(context, "Beacon", "\nNearest:::\n"+nearestBeacon.toString());
                    Optional<Location> currentLocation = getLocation(nearestBeacon);
            beaconObserver.updateLocation(currentLocation);
        }else{
            System.out.println("NEARESTBEACON::: no nearest beacon found");
            writeFileOnInternalStorage(context, "Beacon", "\nno nearest beacon found\n");
        }
    }

    public void writeFileOnInternalStorage(Context mcoContext, String sFileName, String sBody){
        File dir = new File(mcoContext.getFilesDir(), "mydir");
        if(!dir.exists()){
            dir.mkdir();
        }

        try {
            File gpxfile = new File(dir, sFileName);
            FileWriter writer = new FileWriter(gpxfile, true);
            writer.append(sBody);
            writer.flush();
            writer.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public Optional<Location> getLocation(BeaconID nearestBeacon) {
        return uiConfig.getLocation(locationConfig.getRoomUID(nearestBeacon));
    }

    public void setObserver(BeaconObserverImplementation beaconObserverImplementation) {
        this.beaconObserver = beaconObserverImplementation;
    }

}
