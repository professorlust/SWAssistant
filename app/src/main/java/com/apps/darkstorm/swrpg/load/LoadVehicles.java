package com.apps.darkstorm.swrpg.load;

import android.app.Activity;

import com.apps.darkstorm.swrpg.R;
import com.apps.darkstorm.swrpg.SWrpg;
import com.apps.darkstorm.swrpg.sw.Vehicle;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Date;

public class LoadVehicles {
    public ArrayList<Vehicle> vehicles;
    public ArrayList<Date> lastMod;
    public LoadVehicles(Activity main){
        vehicles = new ArrayList<>();
        lastMod = new ArrayList<>();
        File fold = new File(((SWrpg)main.getApplication()).prefs.getString(main.getString(R.string.local_location_key),
                ((SWrpg)main.getApplication()).defaultLoc));
        if(!fold.exists()){
            if (!fold.mkdir()){
                return;
            }
        }
        fold = new File(fold.getAbsolutePath()+"/SWShips");
        if(!fold.exists()){
            if (!fold.mkdir()){
                return;
            }
        }
        File[] chars = fold.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if(name.endsWith(".vhcl")){
                    return true;
                }
                return false;
            }
        });
        for(File fil:chars){
            Vehicle tmp = new Vehicle();
            tmp.reLoad(fil.getAbsolutePath());
            vehicles.add(tmp);
            lastMod.add(new Date(fil.lastModified()));
        }
    }
}
