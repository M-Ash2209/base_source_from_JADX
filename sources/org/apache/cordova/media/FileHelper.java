package org.apache.cordova.media;

import android.net.Uri;

public class FileHelper {
    public static String stripFileProtocol(String str) {
        return str.startsWith("file://") ? Uri.parse(str).getPath() : str;
    }
}
