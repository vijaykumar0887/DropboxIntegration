package com.dropboxintergation.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class NetworkCheckUtility {

    private static final String TAG = NetworkCheckUtility.class.getSimpleName();


    /**
     * Method to check network available or not
     * @param context
     * @return
     */
    public static Boolean isNetworkAvailable(Context context) {
        Boolean isConnected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null) {
                for (int index = 0; index < networkInfo.length; index++) {
                    if (networkInfo[index].isConnected()) {
                        isConnected = true;
                        break;
                    }
                }
            }
        }
        return isConnected;
    }



}
