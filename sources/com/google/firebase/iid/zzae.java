package com.google.firebase.iid;

import android.os.Handler;
import android.os.Message;

final /* synthetic */ class zzae implements Handler.Callback {
    private final zzad zzcg;

    zzae(zzad zzad) {
        this.zzcg = zzad;
    }

    public final boolean handleMessage(Message message) {
        return this.zzcg.zza(message);
    }
}
