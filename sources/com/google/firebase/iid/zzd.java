package com.google.firebase.iid;

import android.content.BroadcastReceiver;
import android.content.Intent;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

final class zzd {
    final Intent intent;
    private final BroadcastReceiver.PendingResult zzu;
    private boolean zzv = false;
    private final ScheduledFuture<?> zzw;

    zzd(Intent intent2, BroadcastReceiver.PendingResult pendingResult, ScheduledExecutorService scheduledExecutorService) {
        this.intent = intent2;
        this.zzu = pendingResult;
        this.zzw = scheduledExecutorService.schedule(new zze(this, intent2), 9000, TimeUnit.MILLISECONDS);
    }

    /* access modifiers changed from: package-private */
    public final synchronized void finish() {
        if (!this.zzv) {
            this.zzu.finish();
            this.zzw.cancel(false);
            this.zzv = true;
        }
    }
}
