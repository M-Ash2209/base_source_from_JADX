package p005de.appplant.cordova.plugin.badge;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONException;
import org.json.JSONObject;
import p007me.leolin.shortcutbadger.ShortcutBadger;

/* renamed from: de.appplant.cordova.plugin.badge.BadgeImpl */
public final class BadgeImpl {
    private static final String BADGE_KEY = "badge";
    private static final String CONFIG_KEY = "badge.config";
    private final Context ctx;
    private final boolean isSupported;

    public BadgeImpl(Context context) {
        if (ShortcutBadger.isBadgeCounterSupported(context)) {
            this.ctx = context;
            this.isSupported = true;
        } else {
            this.ctx = context.getApplicationContext();
            this.isSupported = ShortcutBadger.isBadgeCounterSupported(this.ctx);
        }
        ShortcutBadger.applyCount(this.ctx, getBadge());
    }

    public void clearBadge() {
        saveBadge(0);
        ShortcutBadger.removeCount(this.ctx);
    }

    public int getBadge() {
        return getPrefs().getInt("badge", 0);
    }

    public boolean isSupported() {
        return this.isSupported;
    }

    public void setBadge(int i) {
        saveBadge(i);
        ShortcutBadger.applyCount(this.ctx, i);
    }

    public JSONObject loadConfig() {
        try {
            return new JSONObject(getPrefs().getString(CONFIG_KEY, "{}"));
        } catch (JSONException unused) {
            return new JSONObject();
        }
    }

    public void saveConfig(JSONObject jSONObject) {
        SharedPreferences.Editor edit = getPrefs().edit();
        edit.putString(CONFIG_KEY, jSONObject.toString());
        edit.apply();
    }

    private void saveBadge(int i) {
        SharedPreferences.Editor edit = getPrefs().edit();
        edit.putInt("badge", i);
        edit.apply();
    }

    private SharedPreferences getPrefs() {
        return this.ctx.getSharedPreferences("badge", 0);
    }
}
