package com.google.android.gms.internal.firebase_messaging;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.adobe.phonegap.push.PushConstants;
import com.google.android.gms.common.internal.Preconditions;

public final class zzu {
    /* access modifiers changed from: private */
    public int targetSdkVersion = -1;
    /* access modifiers changed from: private */
    @NonNull
    public final Bundle zzcl;
    /* access modifiers changed from: private */
    @NonNull
    public final String zzeq;
    /* access modifiers changed from: private */
    public int zzer;
    /* access modifiers changed from: private */
    public CharSequence zzes;
    /* access modifiers changed from: private */
    public Intent zzet;
    /* access modifiers changed from: private */
    @NonNull
    public Bundle zzeu = Bundle.EMPTY;
    /* access modifiers changed from: private */
    public Resources zzev;
    /* access modifiers changed from: private */
    public String zzew;
    /* access modifiers changed from: private */
    public zzx zzex;
    /* access modifiers changed from: private */
    public zzv zzey;
    /* access modifiers changed from: private */
    public zzy zzez;
    /* access modifiers changed from: private */
    public zzw zzfa;

    public zzu(@NonNull Bundle bundle, @NonNull String str) {
        this.zzcl = (Bundle) Preconditions.checkNotNull(bundle);
        this.zzeq = (String) Preconditions.checkNotNull(str);
    }

    public final zzu zza(@NonNull Resources resources) {
        this.zzev = (Resources) Preconditions.checkNotNull(resources);
        return this;
    }

    public final zzu zzi(@NonNull Bundle bundle) {
        this.zzeu = (Bundle) Preconditions.checkNotNull(bundle);
        return this;
    }

    public final zzu zza(@NonNull CharSequence charSequence) {
        this.zzes = (CharSequence) Preconditions.checkNotNull(charSequence);
        return this;
    }

    public final zzu zzc(int i) {
        this.zzer = i;
        return this;
    }

    public final zzu zzf(@Nullable Intent intent) {
        this.zzet = intent;
        return this;
    }

    public final zzu zzd(int i) {
        boolean z = i > 0;
        StringBuilder sb = new StringBuilder(36);
        sb.append("Invalid targetSdkVersion ");
        sb.append(i);
        Preconditions.checkArgument(z, sb.toString());
        this.targetSdkVersion = i;
        return this;
    }

    public final zzu zza(@NonNull zzv zzv) {
        this.zzey = (zzv) Preconditions.checkNotNull(zzv);
        return this;
    }

    public final zzu zza(@NonNull zzy zzy) {
        this.zzez = (zzy) Preconditions.checkNotNull(zzy);
        return this;
    }

    public final zzu zza(@NonNull zzw zzw) {
        this.zzfa = (zzw) Preconditions.checkNotNull(zzw);
        return this;
    }

    public final zzu zzq(@NonNull String str) {
        this.zzew = Preconditions.checkNotEmpty(str);
        return this;
    }

    public final zzu zza(zzx zzx) {
        this.zzex = zzx;
        return this;
    }

    public final zzs zzbe() {
        Preconditions.checkNotNull(this.zzcl, PushConstants.PARSE_COM_DATA);
        Preconditions.checkNotNull(this.zzeq, "pkgName");
        Preconditions.checkNotNull(this.zzes, "appLabel");
        Preconditions.checkNotNull(this.zzeu, "pkgMetadata");
        Preconditions.checkNotNull(this.zzev, "pkgResources");
        Preconditions.checkNotNull(this.zzey, "colorGetter");
        Preconditions.checkNotNull(this.zzfa, "notificationChannelFallbackProvider");
        Preconditions.checkNotNull(this.zzez, "pendingIntentFactory");
        Preconditions.checkNotNull(this.zzex, "notificationChannelValidator");
        Preconditions.checkArgument(this.targetSdkVersion >= 0);
        return new zzs(this);
    }
}
