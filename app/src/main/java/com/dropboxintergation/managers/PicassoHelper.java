package com.dropboxintergation.managers;

import android.content.Context;

import com.dropbox.core.v2.DbxClientV2;
import com.dropboxintergation.network.FileThumbnailRequestHandler;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Singleton instance of Picasso pre-configured
 */
public class PicassoHelper {
    private static Picasso sPicasso;

    public static void configure(Context context, DbxClientV2 dbxClient) {

        // Configure picasso to know about special thumbnail requests
        sPicasso = new Picasso.Builder(context)
                .downloader(new OkHttpDownloader(context))
                .addRequestHandler(new FileThumbnailRequestHandler(dbxClient))
                .build();
    }


    public static Picasso getPicasso() {
        return sPicasso;
    }
}
