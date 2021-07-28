package de.smarthome.app.utility;

import android.content.Context;

import java.io.File;
import java.io.FileWriter;

public class InternalStorageWriter {
    public static void writeFileOnInternalStorage(Context mcoContext, String sFileName, String sBody){
        if(mcoContext == null){
            return;
        }
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
}
