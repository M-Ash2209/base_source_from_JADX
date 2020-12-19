package com.google.firebase.iid;

import android.os.Looper;
import android.os.Message;
import com.google.android.gms.internal.firebase_messaging.zzf;

final class zzau extends zzf {
    private final /* synthetic */ zzat zzda;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    zzau(zzat zzat, Looper looper) {
        super(looper);
        this.zzda = zzat;
    }

    public final void handleMessage(Message message) {
        this.zzda.zzb(message);
    }
}
