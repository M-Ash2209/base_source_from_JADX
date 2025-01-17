package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

@SafeParcelable.Class(creator = "ResolveAccountRequestCreator")
public class ResolveAccountRequest extends AbstractSafeParcelable {
    public static final Parcelable.Creator<ResolveAccountRequest> CREATOR = new zam();
    @SafeParcelable.VersionField(mo6944id = 1)
    private final int zale;
    @SafeParcelable.Field(getter = "getSessionId", mo6938id = 3)
    private final int zaoz;
    @SafeParcelable.Field(getter = "getSignInAccountHint", mo6938id = 4)
    private final GoogleSignInAccount zapa;
    @SafeParcelable.Field(getter = "getAccount", mo6938id = 2)
    private final Account zax;

    @SafeParcelable.Constructor
    ResolveAccountRequest(@SafeParcelable.Param(mo6941id = 1) int i, @SafeParcelable.Param(mo6941id = 2) Account account, @SafeParcelable.Param(mo6941id = 3) int i2, @SafeParcelable.Param(mo6941id = 4) GoogleSignInAccount googleSignInAccount) {
        this.zale = i;
        this.zax = account;
        this.zaoz = i2;
        this.zapa = googleSignInAccount;
    }

    public ResolveAccountRequest(Account account, int i, GoogleSignInAccount googleSignInAccount) {
        this(2, account, i, googleSignInAccount);
    }

    public Account getAccount() {
        return this.zax;
    }

    public int getSessionId() {
        return this.zaoz;
    }

    @Nullable
    public GoogleSignInAccount getSignInAccountHint() {
        return this.zapa;
    }

    public void writeToParcel(Parcel parcel, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
        SafeParcelWriter.writeInt(parcel, 1, this.zale);
        SafeParcelWriter.writeParcelable(parcel, 2, getAccount(), i, false);
        SafeParcelWriter.writeInt(parcel, 3, getSessionId());
        SafeParcelWriter.writeParcelable(parcel, 4, getSignInAccountHint(), i, false);
        SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
    }
}
