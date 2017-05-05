package com.apps.darkstorm.swrpg.assistant.sw.stuff;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import com.apps.darkstorm.swrpg.assistant.R;

import java.util.ArrayList;

public class WeapChar{
    //Version 1 (0-2)
    public String name = "";
    public int val; //Depreciated
    public int adv;
    //Version 1 end
    public WeapChar clone(){
        WeapChar tmp = new WeapChar();
        tmp.name = name;
        tmp.val = val;
        tmp.adv = adv;
        return tmp;
    }
    public Object serialObject(){
        ArrayList<Object> tmp = new ArrayList<>();
        tmp.add(name);
        tmp.add(val);
        tmp.add(adv);
        return tmp.toArray();
    }
    public void loadFromObject(Object obj){
        Object[] tmp = (Object[])obj;
        switch (tmp.length){
            case 3:
                name = (String)tmp[0];
                val = (int)tmp[1];
                adv = (int)tmp[2];
        }
    }
    public boolean equals(Object obj){
        if (!(obj instanceof WeapChar))
            return false;
        WeapChar in = (WeapChar)obj;
        return in.name.equals(name) && in.val == val && in.adv == adv;
    }
    public static void editWeapChar(final Activity ac, final Weapon c, final int pos, final boolean newChar, final Skill.onSave os){
        AlertDialog.Builder b = new AlertDialog.Builder(ac);
        View ed = ac.getLayoutInflater().inflate(R.layout.dialog_two_strings,null);
        b.setView(ed);
        ((TextInputLayout)ed.findViewById(R.id.first_lay)).setHint(ac.getString(R.string.name_text));
        ((TextInputLayout)ed.findViewById(R.id.second_lay)).setHint(ac.getString(R.string.advantage_needed_text));
        final EditText name = (EditText)ed.findViewById(R.id.first_edit);
        name.setText(c.chars.get(pos).name);
        name.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS|InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
        final EditText val = (EditText)ed.findViewById(R.id.second_edit);
        val.setText(String.valueOf(c.chars.get(pos).adv));
        val.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_SIGNED);
        b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                c.chars.get(pos).name = name.getText().toString();
                c.chars.get(pos).adv = Integer.parseInt(val.getText().toString());
                os.save();
                dialog.cancel();
            }
        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                os.cancel();
                dialog.cancel();
            }
        });
        if(!newChar) {
            b.setNeutralButton(R.string.delete_text, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    os.delete();
                    dialog.cancel();
                }
            });
        }
        b.show();
    }
}