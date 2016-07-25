package com.dropboxintergation.ui.activities;

import android.support.v7.app.AppCompatActivity;

import com.dropbox.core.android.Auth;
import com.dropboxintergation.managers.DropboxManager;
import com.dropboxintergation.managers.PicassoHelper;
import com.dropboxintergation.utils.Constants;
import com.dropboxintergation.utils.PreferenceHelper;

/**
 * class hold common modules of activity
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();


        String accessToken = PreferenceHelper.fetchPreferenceString(this, Constants.ACCESS_TOKEN);
        if (accessToken == null) {
            accessToken = Auth.getOAuth2Token();
            if (accessToken != null) {
                PreferenceHelper.storePrefString(this,Constants.ACCESS_TOKEN,accessToken);
                initAndLoadData(accessToken);
            }
        } else {
            initAndLoadData(accessToken);
        }
    }

    private void initAndLoadData(String accessToken) {
        DropboxManager.configure(accessToken);
        PicassoHelper.configure(getApplicationContext(), DropboxManager.getClient());
        loadData();
    }

    protected abstract void loadData();

    protected boolean hasToken() {
        String accessToken = PreferenceHelper.fetchPreferenceString(this, Constants.ACCESS_TOKEN);
        return accessToken != null;
    }

}
