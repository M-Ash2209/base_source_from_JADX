package com.google.firebase.iid;

import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;

public class zzl implements Parcelable {
    public static final Parcelable.Creator<zzl> CREATOR = new zzm();
    private Messenger zzal;
    private zzv zzam;

    public static final class zza extends ClassLoader {
        /* access modifiers changed from: protected */
        public final Class<?> loadClass(String str, boolean z) throws ClassNotFoundException {
            if (!"com.google.android.gms.iid.MessengerCompat".equals(str)) {
                return super.loadClass(str, z);
            }
            if (!FirebaseInstanceId.zzm()) {
                return zzl.class;
            }
            Log.d("FirebaseInstanceId", "Using renamed FirebaseIidMessengerCompat class");
            return zzl.class;
        }
    }

    public zzl(IBinder iBinder) {
        if (Build.VERSION.SDK_INT >= 21) {
            this.zzal = new Messenger(iBinder);
        } else {
            this.zzam = new zzw(iBinder);
        }
    }

    public int describeContents() {
        return 0;
    }

    public final void send(Message message) throws RemoteException {
        Messenger messenger = this.zzal;
        if (messenger != null) {
            messenger.send(message);
        } else {
            this.zzam.send(message);
        }
    }

    private final IBinder getBinder() {
        Messenger messenger = this.zzal;
        return messenger != null ? messenger.getBinder() : this.zzam.asBinder();
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        try {
            return getBinder().equals(((zzl) obj).getBinder());
        } catch (ClassCastException unused) {
            return false;
        }
    }

    public int hashCode() {
        return getBinder().hashCode();
    }

    public void writeToParcel(Parcel parcel, int i) {
        Messenger messenger = this.zzal;
        if (messenger != null) {
            parcel.writeStrongBinder(messenger.getBinder());
        } else {
            parcel.writeStrongBinder(this.zzam.asBinder());
        }
    }
}
