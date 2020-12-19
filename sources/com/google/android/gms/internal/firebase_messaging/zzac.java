package com.google.android.gms.internal.firebase_messaging;

import android.os.Bundle;
import com.adobe.phonegap.push.PushConstants;

public final class zzac {
    public static boolean zzj(Bundle bundle) {
        return "1".equals(zza(bundle, "gcm.n.e")) || zza(bundle, "gcm.n.icon") != null;
    }

    static String zza(Bundle bundle, String str) {
        String string = bundle.getString(str);
        return string == null ? bundle.getString(str.replace(PushConstants.GCM_N, "gcm.notification.")) : string;
    }
}
