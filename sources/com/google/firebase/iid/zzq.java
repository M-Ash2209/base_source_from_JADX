package com.google.firebase.iid;

import com.google.firebase.events.Event;
import com.google.firebase.events.EventHandler;
import com.google.firebase.iid.FirebaseInstanceId;

final /* synthetic */ class zzq implements EventHandler {
    private final FirebaseInstanceId.zza zzbi;

    zzq(FirebaseInstanceId.zza zza) {
        this.zzbi = zza;
    }

    public final void handle(Event event) {
        FirebaseInstanceId.zza zza = this.zzbi;
        synchronized (zza) {
            if (zza.isEnabled()) {
                FirebaseInstanceId.this.zzh();
            }
        }
    }
}
