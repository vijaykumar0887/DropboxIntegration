/*
 *  Copyright © 2015,
 * Written under contract by Robosoft Technologies Pvt. Ltd.
 */

package com.dropboxintergation.utils;

import android.util.Log;

/**
 * Wrapper class for general logs.
 */

public class LogUtils {

    private static final String TAG = LogUtils.class.getSimpleName();
    public static final boolean ENABLE_LOG = true;

    public static void LOGD(final String tag, String message) {
        if (ENABLE_LOG) {
            Log.d(tag, message);
        }
    }

    public static void LOGV(final String tag, String message) {
        if (ENABLE_LOG) {
            Log.v(tag, message);
        }
    }

    public static void LOGI(final String tag, String message) {
        if (ENABLE_LOG) {
            Log.i(tag, message);
        }
    }

    public static void LOGW(final String tag, String message) {
            Log.w(tag, message);
    }

    public static void LOGE(final String tag, String message) {
            Log.e(tag, message);
    }

}
