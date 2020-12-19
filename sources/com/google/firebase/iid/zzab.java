package com.google.firebase.iid;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import com.google.android.gms.common.util.concurrent.NamedThreadFactory;
import com.google.android.gms.internal.firebase_messaging.zzb;
import com.google.android.gms.internal.firebase_messaging.zze;
import com.google.android.gms.tasks.Task;
import java.util.concurrent.ScheduledExecutorService;
import javax.annotation.concurrent.GuardedBy;

public final class zzab {
    @GuardedBy("MessengerIpcClient.class")
    private static zzab zzbx;
    /* access modifiers changed from: private */
    public final Context zzac;
    /* access modifiers changed from: private */
    public final ScheduledExecutorService zzby;
    @GuardedBy("this")
    private zzad zzbz = new zzad(this);
    @GuardedBy("this")
    private int zzca = 1;

    public static synchronized zzab zzc(Context context) {
        zzab zzab;
        synchronized (zzab.class) {
            if (zzbx == null) {
                zzbx = new zzab(context, zzb.zza().zza(1, new NamedThreadFactory("MessengerIpcClient"), zze.zzd));
            }
            zzab = zzbx;
        }
        return zzab;
    }

    @VisibleForTesting
    private zzab(Context context, ScheduledExecutorService scheduledExecutorService) {
        this.zzby = scheduledExecutorService;
        this.zzac = context.getApplicationContext();
    }

    public final Task<Void> zza(int i, Bundle bundle) {
        return zza(new zzaj(zzx(), 2, bundle));
    }

    public final Task<Bundle> zzb(int i, Bundle bundle) {
        return zza(new zzam(zzx(), 1, bundle));
    }

    private final synchronized <T> Task<T> zza(zzak<T> zzak) {
        if (Log.isLoggable("MessengerIpcClient", 3)) {
            String valueOf = String.valueOf(zzak);
            StringBuilder sb = new StringBuilder(String.valueOf(valueOf).length() + 9);
            sb.append("Queueing ");
            sb.append(valueOf);
            Log.d("MessengerIpcClient", sb.toString());
        }
        if (!this.zzbz.zzb(zzak)) {
            this.zzbz = new zzad(this);
            this.zzbz.zzb(zzak);
        }
        return zzak.zzck.getTask();
    }

    private final synchronized int zzx() {
        int i;
        i = this.zzca;
        this.zzca = i + 1;
        return i;
    }
}
