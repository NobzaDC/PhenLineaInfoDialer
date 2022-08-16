package com.example.phenlineadialer.Helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {

    public static final String CODIGO_PH = "bdCode";

    private SharedPreferences preferences;
    private Context context;

    public MySharedPreferences(Context context) {
        String PREFERENCES_FILE_NAME = "PhEnLineaDialerPreferences";
        preferences = context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        this.context = context;
    }

    public SharedPreferences getSharedPreferences(){
        return preferences;
    }

    public String getPreferencesString(String name){
        return preferences.getString(name, "");
    }

    public void editPreferencesString(String name, String value){
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(name, value);

        editor.apply();
    }
}
