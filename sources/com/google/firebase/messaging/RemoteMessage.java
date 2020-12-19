package com.google.firebase.messaging;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.p000v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import com.adobe.phonegap.push.PushConstants;
import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.internal.firebase_messaging.zzac;
import com.google.android.gms.internal.firebase_messaging.zzq;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;

@SafeParcelable.Class(creator = "RemoteMessageCreator")
@SafeParcelable.Reserved({1})
public final class RemoteMessage extends AbstractSafeParcelable {
    public static final Parcelable.Creator<RemoteMessage> CREATOR = new zzc();
    public static final int PRIORITY_HIGH = 1;
    public static final int PRIORITY_NORMAL = 2;
    public static final int PRIORITY_UNKNOWN = 0;
    @SafeParcelable.Field(mo6938id = 2)
    Bundle zzdw;
    private Map<String, String> zzdx;
    private Notification zzdy;

    @Retention(RetentionPolicy.SOURCE)
    public @interface MessagePriority {
    }

    @SafeParcelable.Constructor
    public RemoteMessage(@SafeParcelable.Param(mo6941id = 2) Bundle bundle) {
        this.zzdw = bundle;
    }

    public static class Notification {
        private final String tag;
        private final String zzdz;
        private final String zzea;
        private final String[] zzeb;
        private final String zzec;
        private final String zzed;
        private final String[] zzee;
        private final String zzef;
        private final String zzeg;
        private final String zzeh;
        private final String zzei;
        private final String zzej;
        private final Uri zzek;

        /* access modifiers changed from: private */
        @Nullable
        public static Notification zzf(Bundle bundle) {
            if (zzac.zzj(bundle)) {
                return new Notification(new zzq("FirebaseMessaging", bundle));
            }
            return null;
        }

        private Notification(zzq zzq) {
            this.zzdz = zzq.getString("gcm.n.title");
            this.zzea = zzq.zzp("gcm.n.title");
            this.zzeb = zzq.zzn("gcm.n.title");
            this.zzec = zzq.getString("gcm.n.body");
            this.zzed = zzq.zzp("gcm.n.body");
            this.zzee = zzq.zzn("gcm.n.body");
            this.zzef = zzq.getString("gcm.n.icon");
            this.zzeg = zzq.zzav();
            this.tag = zzq.getString("gcm.n.tag");
            this.zzeh = zzq.getString("gcm.n.color");
            this.zzei = zzq.getString("gcm.n.click_action");
            this.zzej = zzq.getString("gcm.n.android_channel_id");
            this.zzek = zzq.zzaw();
        }

        @Nullable
        public String getTitle() {
            return this.zzdz;
        }

        @Nullable
        public String getTitleLocalizationKey() {
            return this.zzea;
        }

        @Nullable
        public String[] getTitleLocalizationArgs() {
            return this.zzeb;
        }

        @Nullable
        public String getBody() {
            return this.zzec;
        }

        @Nullable
        public String getBodyLocalizationKey() {
            return this.zzed;
        }

        @Nullable
        public String[] getBodyLocalizationArgs() {
            return this.zzee;
        }

        @Nullable
        public String getIcon() {
            return this.zzef;
        }

        @Nullable
        public String getSound() {
            return this.zzeg;
        }

        @Nullable
        public String getTag() {
            return this.tag;
        }

        @Nullable
        public String getColor() {
            return this.zzeh;
        }

        @Nullable
        public String getClickAction() {
            return this.zzei;
        }

        @Nullable
        public String getChannelId() {
            return this.zzej;
        }

        @Nullable
        public Uri getLink() {
            return this.zzek;
        }
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
        SafeParcelWriter.writeBundle(parcel, 2, this.zzdw, false);
        SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
    }

    public static class Builder {
        private final Bundle zzdw = new Bundle();
        private final Map<String, String> zzdx = new ArrayMap();

        public Builder(String str) {
            if (TextUtils.isEmpty(str)) {
                String valueOf = String.valueOf(str);
                throw new IllegalArgumentException(valueOf.length() != 0 ? "Invalid to: ".concat(valueOf) : new String("Invalid to: "));
            } else {
                this.zzdw.putString("google.to", str);
            }
        }

        public RemoteMessage build() {
            Bundle bundle = new Bundle();
            for (Map.Entry next : this.zzdx.entrySet()) {
                bundle.putString((String) next.getKey(), (String) next.getValue());
            }
            bundle.putAll(this.zzdw);
            this.zzdw.remove(PushConstants.FROM);
            return new RemoteMessage(bundle);
        }

        public Builder addData(String str, String str2) {
            this.zzdx.put(str, str2);
            return this;
        }

        public Builder setData(Map<String, String> map) {
            this.zzdx.clear();
            this.zzdx.putAll(map);
            return this;
        }

        public Builder clearData() {
            this.zzdx.clear();
            return this;
        }

        public Builder setMessageId(String str) {
            this.zzdw.putString("google.message_id", str);
            return this;
        }

        public Builder setMessageType(String str) {
            this.zzdw.putString("message_type", str);
            return this;
        }

        public Builder setTtl(@IntRange(from = 0, mo107to = 86400) int i) {
            this.zzdw.putString("google.ttl", String.valueOf(i));
            return this;
        }

        public Builder setCollapseKey(String str) {
            this.zzdw.putString(PushConstants.COLLAPSE_KEY, str);
            return this;
        }
    }

    @Nullable
    public final String getFrom() {
        return this.zzdw.getString(PushConstants.FROM);
    }

    @Nullable
    public final String getTo() {
        return this.zzdw.getString("google.to");
    }

    public final Map<String, String> getData() {
        if (this.zzdx == null) {
            Bundle bundle = this.zzdw;
            ArrayMap arrayMap = new ArrayMap();
            for (String str : bundle.keySet()) {
                Object obj = bundle.get(str);
                if (obj instanceof String) {
                    String str2 = (String) obj;
                    if (!str.startsWith("google.") && !str.startsWith("gcm.") && !str.equals(PushConstants.FROM) && !str.equals("message_type") && !str.equals(PushConstants.COLLAPSE_KEY)) {
                        arrayMap.put(str, str2);
                    }
                }
            }
            this.zzdx = arrayMap;
        }
        return this.zzdx;
    }

    @Nullable
    public final String getCollapseKey() {
        return this.zzdw.getString(PushConstants.COLLAPSE_KEY);
    }

    @Nullable
    public final String getMessageId() {
        String string = this.zzdw.getString("google.message_id");
        return string == null ? this.zzdw.getString("message_id") : string;
    }

    @Nullable
    public final String getMessageType() {
        return this.zzdw.getString("message_type");
    }

    public final long getSentTime() {
        Object obj = this.zzdw.get("google.sent_time");
        if (obj instanceof Long) {
            return ((Long) obj).longValue();
        }
        if (!(obj instanceof String)) {
            return 0;
        }
        try {
            return Long.parseLong((String) obj);
        } catch (NumberFormatException unused) {
            String valueOf = String.valueOf(obj);
            StringBuilder sb = new StringBuilder(String.valueOf(valueOf).length() + 19);
            sb.append("Invalid sent time: ");
            sb.append(valueOf);
            Log.w("FirebaseMessaging", sb.toString());
            return 0;
        }
    }

    public final int getTtl() {
        Object obj = this.zzdw.get("google.ttl");
        if (obj instanceof Integer) {
            return ((Integer) obj).intValue();
        }
        if (!(obj instanceof String)) {
            return 0;
        }
        try {
            return Integer.parseInt((String) obj);
        } catch (NumberFormatException unused) {
            String valueOf = String.valueOf(obj);
            StringBuilder sb = new StringBuilder(String.valueOf(valueOf).length() + 13);
            sb.append("Invalid TTL: ");
            sb.append(valueOf);
            Log.w("FirebaseMessaging", sb.toString());
            return 0;
        }
    }

    public final int getOriginalPriority() {
        String string = this.zzdw.getString("google.original_priority");
        if (string == null) {
            string = this.zzdw.getString("google.priority");
        }
        return zzm(string);
    }

    public final int getPriority() {
        String string = this.zzdw.getString("google.delivered_priority");
        if (string == null) {
            if ("1".equals(this.zzdw.getString("google.priority_reduced"))) {
                return 2;
            }
            string = this.zzdw.getString("google.priority");
        }
        return zzm(string);
    }

    private static int zzm(String str) {
        if ("high".equals(str)) {
            return 1;
        }
        return "normal".equals(str) ? 2 : 0;
    }

    @Nullable
    public final Notification getNotification() {
        if (this.zzdy == null) {
            this.zzdy = Notification.zzf(this.zzdw);
        }
        return this.zzdy;
    }

    @KeepForSdk
    public final Intent toIntent() {
        Intent intent = new Intent();
        intent.putExtras(this.zzdw);
        return intent;
    }
}
