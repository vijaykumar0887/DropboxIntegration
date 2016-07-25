package com.dropboxintergation.network;

import android.os.AsyncTask;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FolderMetadata;

/**
 * Async task to list items in a folder
 */
public class CreateFolderTask extends AsyncTask<String, Void, FolderMetadata> {

    private final DbxClientV2 mDbxClient;
    private final Callback mCallback;
    private Exception mException;

    public interface Callback {
        void onDataLoaded(FolderMetadata result);

        void onError(Exception e);
    }

    public CreateFolderTask(DbxClientV2 dbxClient, Callback callback) {
        mDbxClient = dbxClient;
        mCallback = callback;
    }

    @Override
    protected void onPostExecute(FolderMetadata result) {
        super.onPostExecute(result);

        if (mException != null) {
            mCallback.onError(mException);
        } else {
            mCallback.onDataLoaded(result);
        }
    }

    @Override
    protected FolderMetadata doInBackground(String... params) {
        try {

            return mDbxClient.files().createFolder(params[0]);

        } catch (DbxException e) {
            mException = e;
        }

        return null;
    }
}
