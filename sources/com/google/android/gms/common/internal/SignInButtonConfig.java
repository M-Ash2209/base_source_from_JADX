package com.google.android.gms.common.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

@SafeParcelable.Class(creator = "SignInButtonConfigCreator")
public class SignInButtonConfig extends AbstractSafeParcelable {
    public static final Parcelable.Creator<SignInButtonConfig> CREATOR = new zao();
    @SafeParcelable.VersionField(mo6944id = 1)
    private final int zale;
    @SafeParcelable.Field(getter = "getScopes", mo6938id = 4)
    @Deprecated
    private final Scope[] zanx;
    @SafeParcelable.Field(getter = "getButtonSize", mo6938id = 2)
    private final int zapc;
    @SafeParcelable.Field(getter = "getColorScheme", mo6938id = 3)
    private final int zapd;

    @SafeParcelable.Constructor
    SignInButtonConfig(@SafeParcelable.Param(mo6941id = 1) int i, @SafeParcelable.Param(mo6941id = 2) int i2, @SafeParcelable.Param(mo6941id = 3) int i3, @SafeParcelable.Param(mo6941id = 4) Scope[] scopeArr) {
        this.zale = i;
        this.zapc = i2;
        this.zapd = i3;
        this.zanx = scopeArr;
    }

    public SignInButtonConfig(int i, int i2, Scope[] scopeArr) {
        this(1, i, i2, (Scope[]) null);
    }

    public int getButtonSize() {
        return this.zapc;
    }

    public int getColorScheme() {
        return this.zapd;
    }

    @Deprecated
    public Scope[] getScopes() {
        return this.zanx;
    }

    public void writeToParcel(Parcel parcel, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
        SafeParcelWriter.writeInt(parcel, 1, this.zale);
        SafeParcelWriter.writeInt(parcel, 2, getButtonSize());
        SafeParcelWriter.writeInt(parcel, 3, getColorScheme());
        SafeParcelWriter.writeTypedArray(parcel, 4, getScopes(), i, false);
        SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
    }
}
