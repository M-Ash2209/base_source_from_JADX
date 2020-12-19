package com.google.firebase.iid;

import android.os.Bundle;
import com.adobe.phonegap.push.PushConstants;

final class zzam extends zzak<Bundle> {
    zzam(int i, int i2, Bundle bundle) {
        super(i, 1, bundle);
    }

    /* access modifiers changed from: package-private */
    public final boolean zzab() {
        return false;
    }

    /* access modifiers changed from: package-private */
    public final void zzb(Bundle bundle) {
        Bundle bundle2 = bundle.getBundle(PushConstants.PARSE_COM_DATA);
        if (bundle2 == null) {
            bundle2 = Bundle.EMPTY;
        }
        finish(bundle2);
    }
}
