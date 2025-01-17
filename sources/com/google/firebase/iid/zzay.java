package com.google.firebase.iid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.PowerManager;
import android.util.Log;
import com.google.android.gms.common.util.VisibleForTesting;
import java.io.IOException;

final class zzay implements Runnable {
    private final zzba zzav;
    private final long zzdm;
    private final PowerManager.WakeLock zzdn = ((PowerManager) getContext().getSystemService("power")).newWakeLock(1, "fiid-sync");
    private final FirebaseInstanceId zzdo;

    @VisibleForTesting
    zzay(FirebaseInstanceId firebaseInstanceId, zzan zzan, zzba zzba, long j) {
        this.zzdo = firebaseInstanceId;
        this.zzav = zzba;
        this.zzdm = j;
        this.zzdn.setReferenceCounted(false);
    }

    @SuppressLint({"Wakelock"})
    public final void run() {
        try {
            if (zzav.zzai().zzd(getContext())) {
                this.zzdn.acquire();
            }
            this.zzdo.zza(true);
            if (!this.zzdo.zzo()) {
                this.zzdo.zza(false);
            } else if (!zzav.zzai().zze(getContext()) || zzan()) {
                if (!zzam() || !this.zzav.zzc(this.zzdo)) {
                    this.zzdo.zza(this.zzdm);
                } else {
                    this.zzdo.zza(false);
                }
                if (zzav.zzai().zzd(getContext())) {
                    this.zzdn.release();
                }
            } else {
                new zzaz(this).zzao();
                if (zzav.zzai().zzd(getContext())) {
                    this.zzdn.release();
                }
            }
        } finally {
            if (zzav.zzai().zzd(getContext())) {
                this.zzdn.release();
            }
        }
    }

    @VisibleForTesting
    private final boolean zzam() {
        zzax zzk = this.zzdo.zzk();
        if (!this.zzdo.zzr() && !this.zzdo.zza(zzk)) {
            return true;
        }
        try {
            String zzl = this.zzdo.zzl();
            if (zzl == null) {
                Log.e("FirebaseInstanceId", "Token retrieval failed: null");
                return false;
            }
            if (Log.isLoggable("FirebaseInstanceId", 3)) {
                Log.d("FirebaseInstanceId", "Token successfully retrieved");
            }
            if (zzk == null || (zzk != null && !zzl.equals(zzk.zzbu))) {
                Context context = getContext();
                Intent intent = new Intent("com.google.firebase.messaging.NEW_TOKEN");
                intent.putExtra("token", zzl);
                zzav.zzc(context, intent);
                zzav.zzb(context, new Intent("com.google.firebase.iid.TOKEN_REFRESH"));
            }
            return true;
        } catch (IOException | SecurityException e) {
            String valueOf = String.valueOf(e.getMessage());
            Log.e("FirebaseInstanceId", valueOf.length() != 0 ? "Token retrieval failed: ".concat(valueOf) : new String("Token retrieval failed: "));
            return false;
        }
    }

    /* access modifiers changed from: package-private */
    public final Context getContext() {
        return this.zzdo.zzi().getApplicationContext();
    }

    /* access modifiers changed from: package-private */
    public final boolean zzan() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService("connectivity");
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
