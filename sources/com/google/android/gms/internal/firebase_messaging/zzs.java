package com.google.android.gms.internal.firebase_messaging;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

public final class zzs {
    private final int targetSdkVersion;
    @NonNull
    private final Bundle zzcl;
    @NonNull
    private final String zzeq;
    @IdRes
    private final int zzer;
    private final CharSequence zzes;
    @Nullable
    private final Intent zzet;
    @NonNull
    private final Bundle zzeu;
    private final Resources zzev;
    @NonNull
    private final String zzew;
    private final zzx zzex;
    private final zzv zzey;
    private final zzy zzez;
    private final zzw zzfa;

    @NonNull
    public final Bundle getData() {
        return this.zzcl;
    }

    @NonNull
    public final String getPackageName() {
        return this.zzeq;
    }

    @NonNull
    public final Resources zzay() {
        return this.zzev;
    }

    @NonNull
    public final Bundle zzaz() {
        return this.zzeu;
    }

    @NonNull
    public final CharSequence getAppLabel() {
        return this.zzes;
    }

    @Nullable
    public final Intent zzba() {
        return this.zzet;
    }

    @IdRes
    public final int zzbb() {
        return this.zzer;
    }

    public final int zzbc() {
        return this.targetSdkVersion;
    }

    @NonNull
    public final String zzbd() {
        return this.zzew;
    }

    public final boolean zzl(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        return this.zzex.zzl(str);
    }

    @Nullable
    public final Integer zzb(@ColorRes int i) {
        return this.zzey.zzb(i);
    }

    /* access modifiers changed from: package-private */
    @Nullable
    public final PendingIntent zze(Intent intent) {
        return this.zzez.zze(intent);
    }

    /* access modifiers changed from: package-private */
    @Nullable
    public final PendingIntent zzau() {
        return this.zzez.zzau();
    }

    /* access modifiers changed from: package-private */
    @Nullable
    public final String zzat() {
        return this.zzfa.zzat();
    }

    private zzs(zzu zzu) {
        this.zzcl = zzu.zzcl;
        this.zzeq = zzu.zzeq;
        this.zzer = zzu.zzer;
        this.targetSdkVersion = zzu.targetSdkVersion;
        this.zzes = zzu.zzes;
        this.zzet = zzu.zzet;
        this.zzeu = zzu.zzeu;
        this.zzev = zzu.zzev;
        this.zzey = zzu.zzey;
        this.zzfa = zzu.zzfa;
        this.zzez = zzu.zzez;
        this.zzew = zzu.zzew;
        this.zzex = zzu.zzex;
    }
}
