package com.google.android.gms.common.server.response;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.ShowFirstParty;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.common.server.response.FastJsonResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@ShowFirstParty
@SafeParcelable.Class(creator = "FieldMappingDictionaryCreator")
public final class zak extends AbstractSafeParcelable {
    public static final Parcelable.Creator<zak> CREATOR = new zan();
    @SafeParcelable.VersionField(mo6944id = 1)
    private final int zale;
    private final HashMap<String, Map<String, FastJsonResponse.Field<?, ?>>> zaqu;
    @SafeParcelable.Field(getter = "getSerializedDictionary", mo6938id = 2)
    private final ArrayList<zal> zaqv;
    @SafeParcelable.Field(getter = "getRootClassName", mo6938id = 3)
    private final String zaqw;

    @SafeParcelable.Constructor
    zak(@SafeParcelable.Param(mo6941id = 1) int i, @SafeParcelable.Param(mo6941id = 2) ArrayList<zal> arrayList, @SafeParcelable.Param(mo6941id = 3) String str) {
        this.zale = i;
        this.zaqv = null;
        HashMap<String, Map<String, FastJsonResponse.Field<?, ?>>> hashMap = new HashMap<>();
        int size = arrayList.size();
        for (int i2 = 0; i2 < size; i2++) {
            zal zal = arrayList.get(i2);
            String str2 = zal.className;
            HashMap hashMap2 = new HashMap();
            int size2 = zal.zaqx.size();
            for (int i3 = 0; i3 < size2; i3++) {
                zam zam = zal.zaqx.get(i3);
                hashMap2.put(zam.zaqy, zam.zaqz);
            }
            hashMap.put(str2, hashMap2);
        }
        this.zaqu = hashMap;
        this.zaqw = (String) Preconditions.checkNotNull(str);
        zacr();
    }

    public final void zacr() {
        for (String str : this.zaqu.keySet()) {
            Map map = this.zaqu.get(str);
            for (String str2 : map.keySet()) {
                ((FastJsonResponse.Field) map.get(str2)).zaa(this);
            }
        }
    }

    public final void zacs() {
        for (String next : this.zaqu.keySet()) {
            Map map = this.zaqu.get(next);
            HashMap hashMap = new HashMap();
            for (String str : map.keySet()) {
                hashMap.put(str, ((FastJsonResponse.Field) map.get(str)).zacl());
            }
            this.zaqu.put(next, hashMap);
        }
    }

    public zak(Class<? extends FastJsonResponse> cls) {
        this.zale = 1;
        this.zaqv = null;
        this.zaqu = new HashMap<>();
        this.zaqw = cls.getCanonicalName();
    }

    public final void zaa(Class<? extends FastJsonResponse> cls, Map<String, FastJsonResponse.Field<?, ?>> map) {
        this.zaqu.put(cls.getCanonicalName(), map);
    }

    public final Map<String, FastJsonResponse.Field<?, ?>> zai(String str) {
        return this.zaqu.get(str);
    }

    public final boolean zaa(Class<? extends FastJsonResponse> cls) {
        return this.zaqu.containsKey(cls.getCanonicalName());
    }

    public final String zact() {
        return this.zaqw;
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder();
        for (String next : this.zaqu.keySet()) {
            sb.append(next);
            sb.append(":\n");
            Map map = this.zaqu.get(next);
            for (String str : map.keySet()) {
                sb.append("  ");
                sb.append(str);
                sb.append(": ");
                sb.append(map.get(str));
            }
        }
        return sb.toString();
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
        SafeParcelWriter.writeInt(parcel, 1, this.zale);
        ArrayList arrayList = new ArrayList();
        for (String next : this.zaqu.keySet()) {
            arrayList.add(new zal(next, this.zaqu.get(next)));
        }
        SafeParcelWriter.writeTypedList(parcel, 2, arrayList, false);
        SafeParcelWriter.writeString(parcel, 3, this.zaqw, false);
        SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
    }
}
