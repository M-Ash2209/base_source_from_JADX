package com.google.android.gms.common.internal;

import android.net.Uri;
import com.adobe.phonegap.push.PushConstants;
import com.google.android.gms.common.annotation.KeepForSdk;

@KeepForSdk
public final class ResourceUtils {
    private static final Uri zzet = new Uri.Builder().scheme("android.resource").authority("com.google.android.gms").appendPath(PushConstants.DRAWABLE).build();

    private ResourceUtils() {
    }
}
