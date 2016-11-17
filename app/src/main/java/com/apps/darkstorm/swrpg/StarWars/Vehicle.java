package com.apps.darkstorm.swrpg.StarWars;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.View;

import com.apps.darkstorm.swrpg.CustVars.DriveSaveLoad;
import com.apps.darkstorm.swrpg.CustVars.SaveLoad;
import com.apps.darkstorm.swrpg.R;
import com.apps.darkstorm.swrpg.StarWars.CharStuff.CriticalInjuries;
import com.apps.darkstorm.swrpg.StarWars.CharStuff.Weapons;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.File;
import java.util.Arrays;

public class Vehicle {
    //
    //  |  Version 1  |
    //  |             |
    //  V     0-19    V
    //
    public int ID;
    public String name = "";
    public int silhouette;
    public int speed;
    public int handling;
    public int armor;
    //0-Fore,1-Port,2-Starboard,3-Aft;
    public int[] defense = new int[4];
    public int totalDefense;
    public int hullTramaThresh;
    public int hullTramaCur;
    public int sysStressThresh;
    public int sysStressCur;
    public int encumCapacity;
    public int passengerCapacity;
    public int hp;
    public Weapons weapons = new Weapons();
    public CriticalInjuries crits = new CriticalInjuries();
    private boolean[] showCards = new boolean[0];
    public String desc = "";
    public String model = "";
    //
    //  ^                 ^
    //  |  Version 1 End  |
    //  |                 |
    //

    private boolean editing = false;
    private boolean saving = false;


    public Vehicle(){
        defense[1] = -1;
        defense[2] = -1;
    }
    public Vehicle(int ID){
        this.ID = ID;
        defense[1] = -1;
        defense[2] = -1;
    }
    public void setSilhouette(int sil){
        silhouette = sil;
        if (sil>4){
            if(defense[1]==-1)
                defense[1]=0;
            if(defense[2]==-1)
                defense[2] = 0;
        }else{
            if(defense[1]!=-1)
                defense[1] = -1;
            if(defense[2]!=-1)
                defense[2]=-1;
        }
    }
    public void stopEditing(){
        editing=false;
    }
    //TBI
    public void showHideCards(final View top){
        //TBI
    }
    public Vehicle clone(){
        Vehicle tmp = new Vehicle();
        tmp.ID = ID;
        tmp.name = name;
        tmp.silhouette = silhouette;
        tmp.speed = speed;
        tmp.handling = handling;
        tmp.armor = armor;
        tmp.defense = defense.clone();
        tmp.totalDefense = totalDefense;
        tmp.hullTramaCur = hullTramaCur;
        tmp.hullTramaThresh = hullTramaThresh;
        tmp.sysStressCur = sysStressCur;
        tmp.sysStressThresh = sysStressThresh;
        tmp.encumCapacity = encumCapacity;
        tmp.passengerCapacity = passengerCapacity;
        tmp.hp = hp;
        tmp.weapons = weapons.clone();
        tmp.crits = crits.clone();
        tmp.showCards = showCards.clone();
        tmp.desc = desc;
        tmp.model = model;
        return tmp;
    }
    public void startEditing(final Context main, final GoogleApiClient gac, final DriveId fold){
        if (!editing){
            AsyncTask<Void,Void,Void> async = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    Vehicle old = Vehicle.this.clone();
                    Vehicle.this.cloudSave(gac,getFileId(gac,fold),false);
                    Vehicle.this.save(getFileLocation(main));
                    do{
                        if (!saving) {
                            saving = true;
                            if (!Vehicle.this.equals(old)) {
                                Vehicle.this.cloudSave(gac, getFileId(gac, fold), false);
                                Vehicle.this.save(getFileLocation(main));
                                old = Vehicle.this.clone();
                            }
                            saving = false;
                        }
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }while(editing);
                    if (!saving) {
                        saving = true;
                        if (!Vehicle.this.equals(old)) {
                            Vehicle.this.cloudSave(gac, getFileId(gac, fold), false);
                            Vehicle.this.save(getFileLocation(main));
                        }
                        saving = false;
                    }
                    return null;
                }
            };
            async.execute();
        }
    }
    public void startEditing(final Context main){if (!editing){
        AsyncTask<Void,Void,Void> async = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Vehicle old = Vehicle.this.clone();
                Vehicle.this.save(getFileLocation(main));
                do{
                    if (!saving) {
                        saving = true;
                        if (!Vehicle.this.equals(old)) {
                            Vehicle.this.save(getFileLocation(main));
                            old = Vehicle.this.clone();
                        }
                        saving = false;
                    }
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }while(editing);
                if (!saving) {
                    saving = true;
                    if (!Vehicle.this.equals(old)) {
                        Vehicle.this.save(getFileLocation(main));
                    }
                    saving = false;
                }
                return null;
            }
        };
        async.execute();
    }
    }
    void save(String filename){
        SaveLoad sl = new SaveLoad(filename);
        sl.addSave(ID);
        sl.addSave(name);
        sl.addSave(silhouette);
        sl.addSave(speed);
        sl.addSave(handling);
        sl.addSave(armor);
        sl.addSave(defense);
        sl.addSave(totalDefense);
        sl.addSave(hullTramaCur);
        sl.addSave(hullTramaThresh);
        sl.addSave(sysStressCur);
        sl.addSave(encumCapacity);
        sl.addSave(passengerCapacity);
        sl.addSave(hp);
        sl.addSave(weapons.serialObject());
        sl.addSave(crits.serialObject());
        sl.addSave(showCards);
        sl.addSave(desc);
        sl.addSave(model);
        sl.save();
    }
    void cloudSave(GoogleApiClient gac,DriveId fil, boolean async){
        if(fil != null){
            DriveSaveLoad sl = new DriveSaveLoad(fil);
            sl.addSave(ID);
            sl.addSave(name);
            sl.addSave(silhouette);
            sl.addSave(speed);
            sl.addSave(handling);
            sl.addSave(armor);
            sl.addSave(defense);
            sl.addSave(totalDefense);
            sl.addSave(hullTramaCur);
            sl.addSave(hullTramaThresh);
            sl.addSave(sysStressCur);
            sl.addSave(sysStressThresh);
            sl.addSave(encumCapacity);
            sl.addSave(passengerCapacity);
            sl.addSave(hp);
            sl.addSave(weapons.serialObject());
            sl.addSave(crits.serialObject());
            sl.addSave(showCards);
            sl.addSave(desc);
            sl.addSave(model);
            sl.save(gac,async);
        }
    }
    public void reLoad(String filename){
        SaveLoad sl = new SaveLoad(filename);
        Object[] val = sl.load();
        switch (val.length){
            case 20:
                model = (String)val[19];
                desc = (String)val[18];
                showCards = (boolean[])val[17];
                crits.loadFromObject(val[16]);
                weapons.loadFromObject(val[15]);
                hp = (int)val[14];
                passengerCapacity = (int)val[13];
                encumCapacity = (int)val[12];
                sysStressThresh = (int)val[11];
                sysStressCur = (int)val[10];
                hullTramaThresh = (int)val[9];
                hullTramaCur = (int)val[8];
                totalDefense = (int)val[7];
                defense = (int[])val[6];
                armor = (int)val[5];
                handling = (int)val[4];
                speed = (int)val[3];
                silhouette = (int)val[2];
                name = (String)val[1];
                String title = filename.substring(filename.lastIndexOf("/")+1);
                if (title.substring(0,title.indexOf(".")).equals(""))
                    ID = (int)val[0];
                else
                    ID = Integer.parseInt(title.substring(0,title.indexOf(".")));
        }
    }
    void reLoad(GoogleApiClient gac,DriveId fil){
        DriveSaveLoad sl = new DriveSaveLoad(fil);
        Object[] val = sl.load(gac);
        switch (val.length){
            case 20:
                model = (String)val[19];
                desc = (String)val[18];
                showCards = (boolean[])val[17];
                crits.loadFromObject(val[16]);
                weapons.loadFromObject(val[15]);
                hp = (int)val[14];
                passengerCapacity = (int)val[13];
                encumCapacity = (int)val[12];
                sysStressThresh = (int)val[11];
                sysStressCur = (int)val[10];
                hullTramaThresh = (int)val[9];
                hullTramaCur = (int)val[8];
                totalDefense = (int)val[7];
                defense = (int[])val[6];
                armor = (int)val[5];
                handling = (int)val[4];
                speed = (int)val[3];
                silhouette = (int)val[2];
                name = (String)val[1];
                String title = fil.asDriveFile().getMetadata(gac).await().getMetadata().getTitle();
                if (title.substring(0,title.indexOf(".")).equals(""))
                    ID = (int)val[0];
                else
                    ID = Integer.parseInt(title.substring(0,title.indexOf(".")));
        }
    }
    public String getFileLocation(Context main){
        File location;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            File tmp = Environment.getExternalStorageDirectory();
            location = new File(tmp.getAbsolutePath() + "/SWChars");
            if (!location.exists()){
                if (!location.mkdir()){
                    return "";
                }
            }
        }else{
            File tmp = main.getFilesDir();
            location = new File(tmp.getAbsolutePath() + "/SWChars");
            if (!location.exists()){
                if (!location.mkdir()){
                    return "";
                }
            }
        }
        SharedPreferences pref = main.getSharedPreferences("prefs",Context.MODE_PRIVATE);
        String def = location.getAbsolutePath();
        String loc = pref.getString(main.getString(R.string.local_location_key),def);
        location = new File(loc);
        if (!location.exists()){
            if (!location.mkdir()){
                return "";
            }
        }
        location = new File(location.getAbsolutePath() + "/SWShips");
        if (!location.exists()){
            if (!location.mkdir()){
                return "";
            }
        }
        return location.getAbsolutePath() + "/" + Integer.toString(ID) + ".vhcl";
    }
    public DriveId getFileId(GoogleApiClient gac,DriveId fold){
        String name = Integer.toString(ID) + ".vhcl";
        DriveFolder folder = fold.asDriveFolder();
        DriveId fi = null;
        DriveApi.MetadataBufferResult res = folder.listChildren(gac).await();
        for (Metadata met:res.getMetadataBuffer()){
            if (!met.isFolder() && met.getTitle().equals(name) && !met.isTrashed()){
                fi = met.getDriveId();
                break;
            }
        }
        res.release();
        if (fi == null){
            fi = folder.createFile(gac,new MetadataChangeSet.Builder().setTitle(name).build(),null).await()
                    .getDriveFile().getDriveId();
        }
        return fi;
    }
    public boolean equals(Object obj){
        if (!(obj instanceof Vehicle))
            return false;
        Vehicle in = (Vehicle)obj;
        return in.ID == ID && in.name.equals(name) && in.silhouette == silhouette && in.speed == speed && in.handling == handling
                && in.armor == armor && Arrays.equals(in.defense,defense) && totalDefense == in.totalDefense && in.hullTramaCur == hullTramaCur
                && in.hullTramaThresh == hullTramaThresh && in.sysStressCur == sysStressCur && in.sysStressThresh == sysStressThresh
                && in.encumCapacity == encumCapacity && in.passengerCapacity == passengerCapacity && in.hp == hp && in.weapons.equals(weapons)
                && in.crits.equals(crits) && Arrays.equals(in.showCards,showCards) && in.desc.equals(desc) && in.model.equals(model);
    }
}
