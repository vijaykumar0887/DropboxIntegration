/*
 *  Copyright © 2015,
 * Written under contract by Robosoft Technologies Pvt. Ltd.
 */

package com.dropboxintergation.ui.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.users.FullAccount;
import com.dropboxintergation.R;
import com.dropboxintergation.ui.adapters.FileStructureRvAdapter;
import com.dropboxintergation.ui.common.PermissionHandler;
import com.dropboxintergation.managers.DropboxManager;
import com.dropboxintergation.managers.PicassoHelper;
import com.dropboxintergation.network.CreateFolderTask;
import com.dropboxintergation.network.DownloadFileTask;
import com.dropboxintergation.network.GetCurrentAccountTask;
import com.dropboxintergation.network.ListFolderTask;
import com.dropboxintergation.network.UploadFileTask;
import com.dropboxintergation.utils.DialogUtils;
import com.dropboxintergation.utils.FolderNameSelectionCallback;
import com.dropboxintergation.utils.LogUtils;
import com.dropboxintergation.utils.PreferenceHelper;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public final static String EXTRA_PATH = "FilesActivity_Path";
    public static final int CREATE_FOLDER = 0;
    private static final int PICKFILE_REQUEST_CODE = 1;
    private static final String TAG = HomeActivity.class.getSimpleName();
    public static final int UPLOAD_DOC = 1;

    private String mFilePath;
    private FileStructureRvAdapter mFileStructureRvAdapter;
    private FileMetadata mSelectedFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initViews(toolbar);
        String path = getIntent().getStringExtra(EXTRA_PATH);
        mFilePath = path == null ? "" : path;

        setAppBarTitle(mFilePath);
    }

    private void initViews(Toolbar toolbar) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_dropbox_files);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] actions = {getString(R.string.create_folder_text),getString(R.string.upload_docs_text)};
                    DialogUtils.showAlertDialog(HomeActivity.this, actions, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LogUtils.LOGD(TAG," dialog item pos : "+which);
                            switch (which){
                                case  CREATE_FOLDER:
                                    dialog.dismiss();
                                    createFolderDialog();
                                    break;

                                case UPLOAD_DOC:
                                    performWithPermissions(PermissionHandler.UPLOAD);
                                    break;
                            }

                        }
                    });
                }
            });
        }

        mFileStructureRvAdapter = new FileStructureRvAdapter(PicassoHelper.getPicasso(), new FileStructureRvAdapter.Callback() {
            @Override
            public void onFolderClicked(FolderMetadata folder) {
                startActivity(getIntent(HomeActivity.this, folder.getPathLower()));

            }

            @Override
            public void onFileClicked(final FileMetadata file) {
                mSelectedFile = file;
                performWithPermissions(PermissionHandler.DOWNLOAD);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mFileStructureRvAdapter);

        mSelectedFile = null;
    }


    public  static Intent getIntent(Context context, String path) {
        Intent filesIntent = new Intent(context, HomeActivity.class);
        filesIntent.putExtra(HomeActivity.EXTRA_PATH, path);
        return filesIntent;
    }
    private void createFolderDialog() {
        DialogUtils.showFolderCreationDialog(HomeActivity.this, new FolderNameSelectionCallback() {
            @Override
            public void onDialogNameSelected(String name) {
               LogUtils.LOGD(TAG," folder name : "+name);
                DialogUtils.showProgressDialog(HomeActivity.this,getString(R.string.createing_folder_text));
                new CreateFolderTask(DropboxManager.getClient(), new CreateFolderTask.Callback() {

                    @Override
                    public void onDataLoaded(FolderMetadata result) {
                        DialogUtils.dismissProgress();

                        mFileStructureRvAdapter.add(result);

                    }

                    @Override
                    public void onError(Exception e) {
                        DialogUtils.dismissProgress();

                        LogUtils.LOGE(TAG, "Failed to list folder."+e.getMessage());
                        showToast(getString(R.string.network_error));
                    }
                }).execute(mFilePath.concat("/").concat(name));

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
         if (id == R.id.nav_signout) {
             PreferenceHelper.clearPreferences(this);
             startActivity(new Intent(this,LoginActivity.class));
             finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void loadData() {
        new GetCurrentAccountTask(DropboxManager.getClient(), new GetCurrentAccountTask.Callback() {
            @Override
            public void onComplete(FullAccount result) {

                if (findViewById(R.id.tv_username) != null) {
                    ((TextView) findViewById(R.id.tv_username)).setText(result.getName().getDisplayName());
                }
                if ( findViewById(R.id.tv_user_emailId) != null) {
                    ((TextView) findViewById(R.id.tv_user_emailId)).setText(result.getEmail());
                }
                if ( findViewById(R.id.iv_profile_pic) != null) {
                    Picasso.with(getApplicationContext())
                            .load(result.getProfilePhotoUrl())
                            .error(R.drawable.ic_profile)
                            .placeholder(R.drawable.ic_profile)
                            .into((ImageView) findViewById(R.id.iv_profile_pic));
                }
            }

            @Override
            public void onError(Exception e) {
                LogUtils.LOGE(TAG, "Failed to get account details." + e.getMessage());
            }
        }).execute();


        DialogUtils.showProgressDialog(this, getString(R.string.loading_text));

        new ListFolderTask(DropboxManager.getClient(), new ListFolderTask.Callback() {
            @Override
            public void onDataLoaded(ListFolderResult result) {
                DialogUtils.dismissProgress();

                mFileStructureRvAdapter.setFiles(result.getEntries());
                setAppBarTitle(mFilePath);
            }

            @Override
            public void onError(Exception e) {
                DialogUtils.dismissProgress();

                LogUtils.LOGE(TAG, "Failed to list folder."+e.getMessage());
                showToast(getString(R.string.network_error));
            }
        }).execute(mFilePath);
    }

    private void showToast(String message) {
        Toast.makeText(HomeActivity.this,message,Toast.LENGTH_SHORT).show();
    }


    private void launchFilePicker() {
        // Launch intent to pick file for upload
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, PICKFILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICKFILE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // This is the result of a call to launchFilePicker
                uploadFile(data.getData().toString());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int actionCode, @NonNull String [] permissions, @NonNull int [] grantResults) {
        PermissionHandler action = PermissionHandler.fromCode(actionCode);

        boolean granted = true;
        for (int i = 0; i < grantResults.length; ++i) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                Log.w(TAG, "User denied " + permissions[i] +
                        " permission to perform file action: " + action);
                granted = false;
                break;
            }
        }

        if (granted) {
            performAction(action);
        }
    }

    private void performAction(PermissionHandler action) {
        switch(action) {
            case UPLOAD:
                launchFilePicker();
                break;
            case DOWNLOAD:
                if (mSelectedFile != null) {
                    downloadFile(mSelectedFile);
                } else {
                    LogUtils.LOGE(TAG, "No file selected to download.");
                }
                break;
            default:
                LogUtils.LOGE(TAG, "Can't perform unhandled file action: " + action);
        }
    }

    private void setAppBarTitle(String title){
        if(title != null && !title.isEmpty()){
            setTitle(title);
        }else{
            setTitle(getString(R.string.app_name));
        }
    }

    private void downloadFile(FileMetadata file) {

        DialogUtils.showProgressDialog(this, getString(R.string.downloading_text));

        new DownloadFileTask(HomeActivity.this, DropboxManager.getClient(), new DownloadFileTask.Callback() {
            @Override
            public void onDownloadComplete(File result) {
                DialogUtils.dismissProgress();

                if (result != null) {
                    viewFileInExternalApp(result);
                }
            }

            @Override
            public void onError(Exception e) {
                DialogUtils.dismissProgress();
                LogUtils.LOGE(TAG, "Failed to download file."+ e.getMessage());
                showToast(getString(R.string.network_error));
            }
        }).execute(file);

    }

    private void viewFileInExternalApp(File result) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String ext = result.getName().substring(result.getName().indexOf(".") + 1);
        String type = mime.getMimeTypeFromExtension(ext);

        intent.setDataAndType(Uri.fromFile(result), type);

        // Check for a handler first to avoid a crash
        PackageManager manager = getPackageManager();
        List<ResolveInfo> resolveInfo = manager.queryIntentActivities(intent, 0);
        if (resolveInfo.size() > 0) {
            startActivity(intent);
        }
    }

    private void uploadFile(String fileUri) {

        DialogUtils.showProgressDialog(this, getString(R.string.uploading_text));
        new UploadFileTask(this, DropboxManager.getClient(), new UploadFileTask.Callback() {
            @Override
            public void onUploadComplete(FileMetadata result) {
                DialogUtils.dismissProgress();
                String message = result.getName() + " uploaded successfully.";

                showToast(message);
                // Reload the folder
                loadData();
            }

            @Override
            public void onError(Exception e) {
                DialogUtils.dismissProgress();
                LogUtils.LOGE(TAG, "Failed to upload file."+e.getMessage());

                showToast(getString(R.string.network_error));
            }
        }).execute(fileUri, mFilePath);
    }

    private void performWithPermissions(final PermissionHandler action) {
        if (hasPermissionsForAction(action)) {
            performAction(action);
            return;
        }

        if (shouldDisplayRationaleForAction(action)) {

            DialogUtils.showDefaultAlertDialog(this, null, getString(R.string.storage_permission_request_message),
                    getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            requestPermissionsForAction(action);
                        }
                    }, getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }, false);
        } else {
            requestPermissionsForAction(action);
        }
    }

    private boolean hasPermissionsForAction(PermissionHandler action) {
        for (String permission : action.getPermissions()) {
            int result = ContextCompat.checkSelfPermission(this, permission);
            if (result == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    private boolean shouldDisplayRationaleForAction(PermissionHandler action) {
        for (String permission : action.getPermissions()) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                return true;
            }
        }
        return false;
    }

    private void requestPermissionsForAction(PermissionHandler action) {
        ActivityCompat.requestPermissions(
                this,
                action.getPermissions(),
                action.getCode()
        );
    }

}
