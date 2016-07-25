package com.dropboxintergation.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.dropbox.core.android.Auth;
import com.dropboxintergation.R;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LoginActivity extends BaseActivity {


    private View mLoginBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
    }

    private void initViews() {
        mLoginBtn = findViewById(R.id.login_button);
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Auth.startOAuth2Authentication(LoginActivity.this, getString(R.string.app_key));


            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (hasToken()) {
            mLoginBtn.setVisibility(View.GONE);
        }
        else {
            mLoginBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void loadData() {
        startActivity(new Intent(this,HomeActivity.class));
        finish();
    }


}
