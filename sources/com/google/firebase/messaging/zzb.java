package com.google.firebase.messaging;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import com.google.android.gms.common.util.PlatformVersion;
import com.google.android.gms.internal.firebase_messaging.zzx;

final class zzb implements zzx {
    private final /* synthetic */ NotificationManager zzds;

    zzb(zza zza, NotificationManager notificationManager) {
        this.zzds = notificationManager;
    }

    @TargetApi(26)
    public final boolean zzl(String str) {
        if (PlatformVersion.isAtLeastO() && this.zzds.getNotificationChannel(str) == null) {
            return false;
        }
        return true;
    }
}
