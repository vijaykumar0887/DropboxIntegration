/*
 *  Copyright © 2015,
 * Written under contract by Robosoft Technologies Pvt. Ltd.
 */

package com.dropboxintergation.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;

import com.dropboxintergation.R;
import com.dropboxintergation.utils.DialogUtils;
import com.dropboxintergation.utils.NetworkCheckUtility;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LauncherActivity extends AppCompatActivity {

    private static final int DELAY_MILLIS = 3000;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_launcher);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(NetworkCheckUtility.isNetworkAvailable(LauncherActivity.this)) {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }else{
                    DialogUtils.showNoNetworkDialog(LauncherActivity.this,true);
                }
            }
        },DELAY_MILLIS);
    }

}
