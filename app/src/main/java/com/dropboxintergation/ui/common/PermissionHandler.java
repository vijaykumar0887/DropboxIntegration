/*
 *  Copyright © 2015,
 * Written under contract by vijay
 */

package com.dropboxintergation.ui.common;

import android.Manifest;


public enum PermissionHandler {
    DOWNLOAD(Manifest.permission.WRITE_EXTERNAL_STORAGE),
    UPLOAD(Manifest.permission.READ_EXTERNAL_STORAGE);



    private static final PermissionHandler[] values = values();

    private final String [] permissions;

    PermissionHandler(String ... permissions) {
        this.permissions = permissions;
    }

    public int getCode() {
        return ordinal();
    }

    public String [] getPermissions() {
        return permissions;
    }

    public static PermissionHandler fromCode(int code) {
        if (code < 0 || code >= values.length) {
            throw new IllegalArgumentException("Invalid permission code: " + code);
        }
        return values[code];
    }
}