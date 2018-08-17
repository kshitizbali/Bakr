package com.kshitizbali.bakr.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SavePreferences {

    private static String preferencesName = "com.kshitizbali.bakr.udacity";

    public static void saveString(Context context, String key, String value) {
        SharedPreferences sharedPref = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String fetchString(Context context, String key, String value) {

        SharedPreferences sharedPref = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        return sharedPref.getString(key, value);
    }
}
