/*
 *  Copyright © 2015,
 * Written under contract by Robosoft Technologies Pvt. Ltd.
 */

package com.dropboxintergation.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Helper class to store preference values
 */
public class PreferenceHelper {


    private static final String PREF_NAME = "DropBoxDemo";

    public static void storePrefString(Context context, String key, String value){
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(key,value);
        edit.apply();
    }


    public static String fetchPreferenceString(Context context,String key){
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        return preferences.getString(key,null);
    }

    public static void clearPreferences(Context context){
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        preferences.edit().clear().apply();

    }
}
