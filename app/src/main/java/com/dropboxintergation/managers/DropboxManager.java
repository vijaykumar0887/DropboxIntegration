package com.dropboxintergation.managers;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.http.OkHttp3Requestor;
import com.dropbox.core.v2.DbxClientV2;

/**
 * Singleton instance of {@link DbxClientV2} and friends
 */
public class DropboxManager {

    private static DbxClientV2 sDbxClient;

    public static void configure(String accessToken) {
        if (sDbxClient == null) {
            DbxRequestConfig requestConfig = DbxRequestConfig.newBuilder("examples-v2-demo")
                .withHttpRequestor(OkHttp3Requestor.INSTANCE)
                .build();

            sDbxClient = new DbxClientV2(requestConfig, accessToken);
        }
    }

    public static DbxClientV2 getClient() {
        if (sDbxClient == null) {
            throw new IllegalStateException("Client not initialized.");
        }
        return sDbxClient;
    }
}
