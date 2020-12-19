package p005de.appplant.cordova.plugin.notification;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.p000v4.app.NotificationCompat;
import android.support.p000v4.media.session.MediaSessionCompat;
import android.support.p000v4.view.ViewCompat;
import com.adobe.phonegap.push.PushConstants;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.cordova.globalization.Globalization;
import org.json.JSONArray;
import org.json.JSONObject;
import p005de.appplant.cordova.plugin.notification.action.Action;
import p005de.appplant.cordova.plugin.notification.action.ActionGroup;
import p005de.appplant.cordova.plugin.notification.util.AssetUtil;

/* renamed from: de.appplant.cordova.plugin.notification.Options */
public final class Options {
    private static final String DEFAULT_ICON = "res://icon";
    private static final String DEFAULT_ICON_TYPE = "square";
    public static final String EXTRA_LAUNCH = "NOTIFICATION_LAUNCH";
    static final String EXTRA_SOUND = "NOTIFICATION_SOUND";
    private final AssetUtil assets;
    private final Context context;
    private final JSONObject options;

    public Options(JSONObject jSONObject) {
        this.options = jSONObject;
        this.context = null;
        this.assets = null;
    }

    public Options(Context context2, JSONObject jSONObject) {
        this.context = context2;
        this.options = jSONObject;
        this.assets = AssetUtil.getInstance(context2);
    }

    public Context getContext() {
        return this.context;
    }

    public JSONObject getDict() {
        return this.options;
    }

    public String toString() {
        return this.options.toString();
    }

    public Integer getId() {
        return Integer.valueOf(this.options.optInt(PushConstants.CHANNEL_ID, 0));
    }

    /* access modifiers changed from: package-private */
    public String getIdentifier() {
        return getId().toString();
    }

    public int getBadgeNumber() {
        return this.options.optInt(PushConstants.BADGE, 0);
    }

    public int getNumber() {
        return this.options.optInt(Globalization.NUMBER, 0);
    }

    public Boolean isSticky() {
        return Boolean.valueOf(this.options.optBoolean("sticky", false));
    }

    /* access modifiers changed from: package-private */
    public Boolean isAutoClear() {
        return Boolean.valueOf(this.options.optBoolean("autoClear", false));
    }

    public JSONObject getTrigger() {
        return this.options.optJSONObject("trigger");
    }

    /* access modifiers changed from: package-private */
    public boolean isSilent() {
        return this.options.optBoolean("silent", false);
    }

    /* access modifiers changed from: package-private */
    public String getGroup() {
        return this.options.optString("group", (String) null);
    }

    /* access modifiers changed from: package-private */
    public boolean isLaunchingApp() {
        return this.options.optBoolean("launch", true);
    }

    public boolean shallWakeUp() {
        return this.options.optBoolean("wakeup", true);
    }

    /* access modifiers changed from: package-private */
    public long getTimeout() {
        return this.options.optLong("timeoutAfter");
    }

    /* access modifiers changed from: package-private */
    public String getChannel() {
        return this.options.optString("channel", "default-channel-id");
    }

    /* access modifiers changed from: package-private */
    public boolean getGroupSummary() {
        return this.options.optBoolean("groupSummary", false);
    }

    public String getText() {
        Object opt = this.options.opt(PushConstants.STYLE_TEXT);
        return opt instanceof String ? (String) opt : "";
    }

    public String getTitle() {
        String optString = this.options.optString(PushConstants.TITLE, "");
        return optString.isEmpty() ? this.context.getApplicationInfo().loadLabel(this.context.getPackageManager()).toString() : optString;
    }

    /* access modifiers changed from: package-private */
    public int getLedColor() {
        String str;
        Object opt = this.options.opt("led");
        if (opt instanceof String) {
            str = this.options.optString("led");
        } else if (opt instanceof JSONArray) {
            str = this.options.optJSONArray("led").optString(0);
        } else {
            str = opt instanceof JSONObject ? this.options.optJSONObject("led").optString(PushConstants.COLOR) : null;
        }
        if (str == null) {
            return 0;
        }
        try {
            return Integer.parseInt(stripHex(str), 16) + ViewCompat.MEASURED_STATE_MASK;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /* access modifiers changed from: package-private */
    public int getLedOn() {
        Object opt = this.options.opt("led");
        if (opt instanceof JSONArray) {
            return this.options.optJSONArray("led").optInt(1, 1000);
        }
        if (opt instanceof JSONObject) {
            return this.options.optJSONObject("led").optInt("on", 1000);
        }
        return 1000;
    }

    /* access modifiers changed from: package-private */
    public int getLedOff() {
        Object opt = this.options.opt("led");
        if (opt instanceof JSONArray) {
            return this.options.optJSONArray("led").optInt(2, 1000);
        }
        if (opt instanceof JSONObject) {
            return this.options.optJSONObject("led").optInt("off", 1000);
        }
        return 1000;
    }

    public int getColor() {
        String optString = this.options.optString(PushConstants.COLOR, (String) null);
        if (optString == null) {
            return 0;
        }
        try {
            String stripHex = stripHex(optString);
            if (stripHex.matches("[^0-9]*")) {
                return Color.class.getDeclaredField(stripHex.toUpperCase()).getInt((Object) null);
            }
            return Integer.parseInt(stripHex, 16) + ViewCompat.MEASURED_STATE_MASK;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        } catch (NoSuchFieldException e2) {
            e2.printStackTrace();
            return 0;
        } catch (IllegalAccessException e3) {
            e3.printStackTrace();
            return 0;
        }
    }

    /* access modifiers changed from: package-private */
    public Uri getSound() {
        return this.assets.parse(this.options.optString(PushConstants.SOUND, (String) null));
    }

    /* access modifiers changed from: package-private */
    public boolean hasLargeIcon() {
        return this.options.optString(PushConstants.ICON, (String) null) != null;
    }

    /* access modifiers changed from: package-private */
    public Bitmap getLargeIcon() {
        try {
            return this.assets.getIconFromUri(this.assets.parse(this.options.optString(PushConstants.ICON, (String) null)));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /* access modifiers changed from: package-private */
    public String getLargeIconType() {
        return this.options.optString("iconType", "square");
    }

    /* access modifiers changed from: package-private */
    public int getSmallIcon() {
        int resId = this.assets.getResId(this.options.optString("smallIcon", DEFAULT_ICON));
        if (resId == 0) {
            resId = this.assets.getResId(DEFAULT_ICON);
        }
        if (resId == 0) {
            return 17301598;
        }
        return resId;
    }

    private boolean isWithVibration() {
        return this.options.optBoolean(PushConstants.VIBRATE, true);
    }

    private boolean isWithoutSound() {
        Object opt = this.options.opt(PushConstants.SOUND);
        if (opt == null || opt.equals(false)) {
            return true;
        }
        return false;
    }

    private boolean isWithDefaultSound() {
        Object opt = this.options.opt(PushConstants.SOUND);
        if (opt == null || !opt.equals(true)) {
            return false;
        }
        return true;
    }

    private boolean isWithoutLights() {
        Object opt = this.options.opt("led");
        if (opt == null || opt.equals(false)) {
            return true;
        }
        return false;
    }

    private boolean isWithDefaultLights() {
        Object opt = this.options.opt("led");
        if (opt == null || !opt.equals(true)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public int getDefaults() {
        int optInt = this.options.optInt("defaults", 0);
        int i = isWithVibration() ? optInt | 2 : optInt & 2;
        if (isWithDefaultSound()) {
            i |= 1;
        } else if (isWithoutSound()) {
            i &= 1;
        }
        if (isWithDefaultLights()) {
            return i | 4;
        }
        return isWithoutLights() ? i & 4 : i;
    }

    /* access modifiers changed from: package-private */
    public int getVisibility() {
        return this.options.optBoolean("lockscreen", true) ? 1 : -1;
    }

    /* access modifiers changed from: package-private */
    public int getPrio() {
        return Math.min(Math.max(this.options.optInt(PushConstants.PRIORITY), -2), 2);
    }

    /* access modifiers changed from: package-private */
    public boolean showClock() {
        Object opt = this.options.opt("clock");
        if (opt instanceof Boolean) {
            return ((Boolean) opt).booleanValue();
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean showChronometer() {
        Object opt = this.options.opt("clock");
        return (opt instanceof String) && opt.equals("chronometer");
    }

    /* access modifiers changed from: package-private */
    public boolean isWithProgressBar() {
        return this.options.optJSONObject("progressBar").optBoolean("enabled", false);
    }

    /* access modifiers changed from: package-private */
    public int getProgressValue() {
        return this.options.optJSONObject("progressBar").optInt("value", 0);
    }

    /* access modifiers changed from: package-private */
    public int getProgressMaxValue() {
        return this.options.optJSONObject("progressBar").optInt("maxValue", 100);
    }

    /* access modifiers changed from: package-private */
    public boolean isIndeterminateProgress() {
        return this.options.optJSONObject("progressBar").optBoolean("indeterminate", false);
    }

    public boolean isInfiniteTrigger() {
        JSONObject optJSONObject = this.options.optJSONObject("trigger");
        return optJSONObject.has("every") && optJSONObject.optInt("count", -1) < 0;
    }

    /* access modifiers changed from: package-private */
    public String getSummary() {
        return this.options.optString("summary", (String) null);
    }

    /* access modifiers changed from: package-private */
    public List<Bitmap> getAttachments() {
        JSONArray optJSONArray = this.options.optJSONArray("attachments");
        ArrayList arrayList = new ArrayList();
        if (optJSONArray == null) {
            return arrayList;
        }
        for (int i = 0; i < optJSONArray.length(); i++) {
            Uri parse = this.assets.parse(optJSONArray.optString(i));
            if (parse != Uri.EMPTY) {
                try {
                    arrayList.add(this.assets.getIconFromUri(parse));
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return arrayList;
    }

    /* access modifiers changed from: package-private */
    public Action[] getActions() {
        JSONArray jSONArray;
        String str;
        ActionGroup actionGroup;
        Object opt = this.options.opt(PushConstants.ACTIONS);
        if (opt instanceof String) {
            str = (String) opt;
            jSONArray = null;
        } else if (opt instanceof JSONArray) {
            jSONArray = (JSONArray) opt;
            str = null;
        } else {
            str = null;
            jSONArray = null;
        }
        if (str != null) {
            actionGroup = ActionGroup.lookup(str);
        } else {
            actionGroup = (jSONArray == null || jSONArray.length() <= 0) ? null : ActionGroup.parse(this.context, jSONArray);
        }
        if (actionGroup != null) {
            return actionGroup.getActions();
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public NotificationCompat.MessagingStyle.Message[] getMessages() {
        Object opt = this.options.opt(PushConstants.STYLE_TEXT);
        if (opt == null || (opt instanceof String)) {
            return null;
        }
        JSONArray jSONArray = (JSONArray) opt;
        if (jSONArray.length() == 0) {
            return null;
        }
        NotificationCompat.MessagingStyle.Message[] messageArr = new NotificationCompat.MessagingStyle.Message[jSONArray.length()];
        long time = new Date().getTime();
        for (int i = 0; i < messageArr.length; i++) {
            JSONObject optJSONObject = jSONArray.optJSONObject(i);
            messageArr[i] = new NotificationCompat.MessagingStyle.Message(optJSONObject.optString(PushConstants.MESSAGE), optJSONObject.optLong(Globalization.DATE, time), optJSONObject.optString("person", (String) null));
        }
        return messageArr;
    }

    /* access modifiers changed from: package-private */
    public String getTitleCount() {
        return this.options.optString("titleCount", (String) null);
    }

    /* access modifiers changed from: package-private */
    public MediaSessionCompat.Token getMediaSessionToken() {
        String optString = this.options.optString("mediaSession", (String) null);
        if (optString == null) {
            return null;
        }
        return new MediaSessionCompat(this.context, optString).getSessionToken();
    }

    private String stripHex(String str) {
        return str.charAt(0) == '#' ? str.substring(1) : str;
    }
}
