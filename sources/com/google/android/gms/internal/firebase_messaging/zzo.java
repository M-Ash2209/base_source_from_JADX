package com.google.android.gms.internal.firebase_messaging;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.android.gms.common.util.PlatformVersion;

public final class zzo implements zzw {
    private final Context zzac;

    public zzo(Context context) {
        this.zzac = context;
    }

    @Nullable
    @TargetApi(26)
    public final String zzat() {
        if (!PlatformVersion.isAtLeastO()) {
            return null;
        }
        NotificationManager notificationManager = (NotificationManager) this.zzac.getSystemService(NotificationManager.class);
        boolean z = true;
        if (PlatformVersion.isAtLeastO() && (TextUtils.isEmpty("fcm_fallback_notification_channel") || notificationManager.getNotificationChannel("fcm_fallback_notification_channel") == null)) {
            z = false;
        }
        if (z) {
            return "fcm_fallback_notification_channel";
        }
        ((NotificationManager) this.zzac.getSystemService(NotificationManager.class)).createNotificationChannel(new NotificationChannel("fcm_fallback_notification_channel", this.zzac.getString(this.zzac.getResources().getIdentifier("fcm_fallback_notification_channel_label", "string", this.zzac.getPackageName())), 3));
        return "fcm_fallback_notification_channel";
    }
}
