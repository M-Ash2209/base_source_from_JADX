package com.google.firebase.messaging;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

final class zza {
    private final Context zzac;
    private final Bundle zzcl;

    public zza(Context context, Bundle bundle) {
        this.zzac = context.getApplicationContext();
        this.zzcl = bundle;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x006a A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x006b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final boolean zzar() {
        /*
            r11 = this;
            com.google.android.gms.internal.firebase_messaging.zzq r0 = new com.google.android.gms.internal.firebase_messaging.zzq
            java.lang.String r1 = "FirebaseMessaging"
            android.os.Bundle r2 = r11.zzcl
            r0.<init>(r1, r2)
            java.lang.String r1 = "1"
            java.lang.String r2 = "gcm.n.noui"
            java.lang.String r2 = r0.getString(r2)
            boolean r1 = r1.equals(r2)
            r2 = 1
            if (r1 == 0) goto L_0x0019
            return r2
        L_0x0019:
            android.content.Context r1 = r11.zzac
            java.lang.String r3 = "keyguard"
            java.lang.Object r1 = r1.getSystemService(r3)
            android.app.KeyguardManager r1 = (android.app.KeyguardManager) r1
            boolean r1 = r1.inKeyguardRestrictedInputMode()
            r3 = 0
            if (r1 != 0) goto L_0x0067
            boolean r1 = com.google.android.gms.common.util.PlatformVersion.isAtLeastLollipop()
            if (r1 != 0) goto L_0x0035
            r4 = 10
            android.os.SystemClock.sleep(r4)
        L_0x0035:
            int r1 = android.os.Process.myPid()
            android.content.Context r4 = r11.zzac
            java.lang.String r5 = "activity"
            java.lang.Object r4 = r4.getSystemService(r5)
            android.app.ActivityManager r4 = (android.app.ActivityManager) r4
            java.util.List r4 = r4.getRunningAppProcesses()
            if (r4 == 0) goto L_0x0067
            java.util.Iterator r4 = r4.iterator()
        L_0x004d:
            boolean r5 = r4.hasNext()
            if (r5 == 0) goto L_0x0067
            java.lang.Object r5 = r4.next()
            android.app.ActivityManager$RunningAppProcessInfo r5 = (android.app.ActivityManager.RunningAppProcessInfo) r5
            int r6 = r5.pid
            if (r6 != r1) goto L_0x004d
            int r1 = r5.importance
            r4 = 100
            if (r1 != r4) goto L_0x0065
            r1 = 1
            goto L_0x0068
        L_0x0065:
            r1 = 0
            goto L_0x0068
        L_0x0067:
            r1 = 0
        L_0x0068:
            if (r1 == 0) goto L_0x006b
            return r3
        L_0x006b:
            android.content.Context r1 = r11.zzac
            android.content.pm.PackageManager r1 = r1.getPackageManager()
            android.content.Context r4 = r11.zzac
            java.lang.String r4 = r4.getPackageName()
            android.content.Context r5 = r11.zzac
            android.content.pm.ApplicationInfo r5 = r5.getApplicationInfo()
            com.google.android.gms.internal.firebase_messaging.zzn r6 = new com.google.android.gms.internal.firebase_messaging.zzn
            android.content.Context r7 = r11.zzac
            r6.<init>(r7)
            com.google.android.gms.internal.firebase_messaging.zzo r7 = new com.google.android.gms.internal.firebase_messaging.zzo
            android.content.Context r8 = r11.zzac
            r7.<init>(r8)
            android.content.Context r8 = r11.zzac
            java.lang.String r9 = "notification"
            java.lang.Object r8 = r8.getSystemService(r9)
            android.app.NotificationManager r8 = (android.app.NotificationManager) r8
            com.google.android.gms.internal.firebase_messaging.zzu r9 = new com.google.android.gms.internal.firebase_messaging.zzu
            android.os.Bundle r10 = r11.zzcl
            r9.<init>(r10, r4)
            int r10 = r5.icon
            com.google.android.gms.internal.firebase_messaging.zzu r9 = r9.zzc((int) r10)
            java.lang.CharSequence r10 = r5.loadLabel(r1)
            com.google.android.gms.internal.firebase_messaging.zzu r9 = r9.zza((java.lang.CharSequence) r10)
            android.content.Intent r1 = r1.getLaunchIntentForPackage(r4)
            com.google.android.gms.internal.firebase_messaging.zzu r1 = r9.zzf((android.content.Intent) r1)
            com.google.android.gms.internal.firebase_messaging.zzu r1 = r1.zza((com.google.android.gms.internal.firebase_messaging.zzv) r6)
            com.google.android.gms.internal.firebase_messaging.zzu r1 = r1.zza((com.google.android.gms.internal.firebase_messaging.zzw) r7)
            com.google.firebase.messaging.zzb r4 = new com.google.firebase.messaging.zzb
            r4.<init>(r11, r8)
            com.google.android.gms.internal.firebase_messaging.zzu r1 = r1.zza((com.google.android.gms.internal.firebase_messaging.zzx) r4)
            java.lang.String r4 = "FCM-Notification"
            com.google.android.gms.internal.firebase_messaging.zzu r1 = r1.zzq(r4)
            android.os.Bundle r4 = r11.zzas()
            com.google.android.gms.internal.firebase_messaging.zzu r1 = r1.zzi((android.os.Bundle) r4)
            android.content.Context r4 = r11.zzac
            android.content.res.Resources r4 = r4.getResources()
            com.google.android.gms.internal.firebase_messaging.zzu r1 = r1.zza((android.content.res.Resources) r4)
            com.google.android.gms.internal.firebase_messaging.zzp r4 = new com.google.android.gms.internal.firebase_messaging.zzp
            android.content.Context r6 = r11.zzac
            android.os.Bundle r7 = r11.zzcl
            r4.<init>(r6, r7)
            com.google.android.gms.internal.firebase_messaging.zzu r1 = r1.zza((com.google.android.gms.internal.firebase_messaging.zzy) r4)
            int r4 = r5.targetSdkVersion
            com.google.android.gms.internal.firebase_messaging.zzu r1 = r1.zzd((int) r4)
            com.google.android.gms.internal.firebase_messaging.zzs r1 = r1.zzbe()
            com.google.android.gms.internal.firebase_messaging.zzaa r4 = new com.google.android.gms.internal.firebase_messaging.zzaa
            r4.<init>(r1, r0)
            com.google.android.gms.internal.firebase_messaging.zzr r0 = new com.google.android.gms.internal.firebase_messaging.zzr
            android.content.Context r1 = r11.zzac
            r0.<init>(r1, r4)
            com.google.android.gms.internal.firebase_messaging.zzab r0 = r0.zzax()
            java.lang.String r1 = "FirebaseMessaging"
            r4 = 3
            boolean r1 = android.util.Log.isLoggable(r1, r4)
            if (r1 == 0) goto L_0x0112
            java.lang.String r1 = "FirebaseMessaging"
            java.lang.String r4 = "Showing notification"
            android.util.Log.d(r1, r4)
        L_0x0112:
            android.content.Context r1 = r11.zzac
            java.lang.String r4 = "notification"
            java.lang.Object r1 = r1.getSystemService(r4)
            android.app.NotificationManager r1 = (android.app.NotificationManager) r1
            java.lang.String r4 = r0.tag
            android.support.v4.app.NotificationCompat$Builder r0 = r0.zzfd
            android.app.Notification r0 = r0.build()
            r1.notify(r4, r3, r0)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.firebase.messaging.zza.zzar():boolean");
    }

    private final Bundle zzas() {
        try {
            Bundle bundle = this.zzac.getPackageManager().getApplicationInfo(this.zzac.getPackageName(), 128).metaData;
            if (bundle != null) {
                return bundle;
            }
            return Bundle.EMPTY;
        } catch (PackageManager.NameNotFoundException unused) {
            return Bundle.EMPTY;
        }
    }
}
