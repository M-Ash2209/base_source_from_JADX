package p005de.appplant.cordova.plugin.notification;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.support.p000v4.app.NotificationCompat;
import android.support.p000v4.util.ArraySet;
import android.support.p000v4.util.Pair;
import android.util.Log;
import android.util.SparseArray;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;

/* renamed from: de.appplant.cordova.plugin.notification.Notification */
public final class Notification {
    public static final String EXTRA_ID = "NOTIFICATION_ID";
    public static final String EXTRA_UPDATE = "NOTIFICATION_UPDATE";
    static final String PREF_KEY_ID = "NOTIFICATION_ID";
    private static final String PREF_KEY_PID = "NOTIFICATION_PID";
    private static SparseArray<NotificationCompat.Builder> cache;
    private final NotificationCompat.Builder builder;
    private final Context context;
    private final Options options;

    /* renamed from: de.appplant.cordova.plugin.notification.Notification$Type */
    public enum Type {
        ALL,
        SCHEDULED,
        TRIGGERED
    }

    Notification(Context context2, Options options2, NotificationCompat.Builder builder2) {
        this.context = context2;
        this.options = options2;
        this.builder = builder2;
    }

    public Notification(Context context2, Options options2) {
        this.context = context2;
        this.options = options2;
        this.builder = null;
    }

    public Context getContext() {
        return this.context;
    }

    public Options getOptions() {
        return this.options;
    }

    public int getId() {
        return this.options.getId().intValue();
    }

    public boolean isRepeating() {
        return getOptions().getTrigger().has("every");
    }

    public boolean isHighPrio() {
        return getOptions().getPrio() >= 1;
    }

    public Type getType() {
        StatusBarNotification[] activeNotifications = Manager.getInstance(this.context).getActiveNotifications();
        int id = getId();
        for (StatusBarNotification id2 : activeNotifications) {
            if (id2.getId() == id) {
                return Type.TRIGGERED;
            }
        }
        return Type.SCHEDULED;
    }

    /* access modifiers changed from: package-private */
    public void schedule(Request request, Class<?> cls) {
        ArrayList<Pair> arrayList = new ArrayList<>();
        ArraySet arraySet = new ArraySet();
        AlarmManager alarmMgr = getAlarmMgr();
        cancelScheduledAlarms();
        do {
            Date triggerDate = request.getTriggerDate();
            Log.d("local-notification", "Next trigger at: " + triggerDate);
            if (triggerDate != null) {
                Intent intent = new Intent(this.context, cls);
                Intent putExtra = intent.setAction("NOTIFICATION_ID" + request.getIdentifier()).putExtra("NOTIFICATION_ID", this.options.getId()).putExtra("NOTIFICATION_OCCURRENCE", request.getOccurrence());
                arraySet.add(putExtra.getAction());
                arrayList.add(new Pair(triggerDate, putExtra));
            }
        } while (request.moveNext());
        if (arrayList.isEmpty()) {
            unpersist();
            return;
        }
        persist(arraySet);
        if (!this.options.isInfiniteTrigger()) {
            ((Intent) ((Pair) arrayList.get(arrayList.size() - 1)).second).putExtra(Request.EXTRA_LAST, true);
        }
        for (Pair pair : arrayList) {
            Date date = (Date) pair.first;
            long time = date.getTime();
            Intent intent2 = (Intent) pair.second;
            if (date.after(new Date()) || !trigger(intent2, cls)) {
                PendingIntent broadcast = PendingIntent.getBroadcast(this.context, 0, intent2, 268435456);
                try {
                    int prio = this.options.getPrio();
                    if (prio == -2) {
                        alarmMgr.setExact(1, time, broadcast);
                    } else if (prio != 2) {
                        alarmMgr.setExact(0, time, broadcast);
                    } else if (Build.VERSION.SDK_INT >= 23) {
                        alarmMgr.setExactAndAllowWhileIdle(0, time, broadcast);
                    } else {
                        alarmMgr.setExact(1, time, broadcast);
                    }
                } catch (Exception unused) {
                }
            }
        }
    }

    private boolean trigger(Intent intent, Class<?> cls) {
        try {
            ((BroadcastReceiver) cls.newInstance()).onReceive(this.context, intent);
            return true;
        } catch (InstantiationException unused) {
            return false;
        } catch (IllegalAccessException unused2) {
            return false;
        }
    }

    public void clear() {
        getNotMgr().cancel(getId());
        if (!isRepeating()) {
            unpersist();
        }
    }

    public void cancel() {
        cancelScheduledAlarms();
        unpersist();
        getNotMgr().cancel(getId());
        clearCache();
    }

    private void cancelScheduledAlarms() {
        Set<String> stringSet = getPrefs(PREF_KEY_PID).getStringSet(this.options.getIdentifier(), (Set) null);
        if (stringSet != null) {
            for (String intent : stringSet) {
                PendingIntent broadcast = PendingIntent.getBroadcast(this.context, 0, new Intent(intent), 0);
                if (broadcast != null) {
                    getAlarmMgr().cancel(broadcast);
                }
            }
        }
    }

    public void show() {
        if (this.builder != null) {
            if (this.options.showChronometer()) {
                cacheBuilder();
            }
            grantPermissionToPlaySoundFromExternal();
            getNotMgr().notify(getAppName(), getId(), this.builder.build());
        }
    }

    private String getAppName() {
        return (String) this.context.getPackageManager().getApplicationLabel(this.context.getApplicationInfo());
    }

    /* access modifiers changed from: package-private */
    public void update(JSONObject jSONObject, Class<?> cls) {
        mergeJSONObjects(jSONObject);
        persist((Set<String>) null);
        if (getType() == Type.TRIGGERED) {
            Intent intent = new Intent(this.context, cls);
            trigger(intent.setAction("NOTIFICATION_ID" + this.options.getId()).putExtra("NOTIFICATION_ID", this.options.getId()).putExtra(EXTRA_UPDATE, true), cls);
        }
    }

    public String toString() {
        JSONObject dict = this.options.getDict();
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject = new JSONObject(dict.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jSONObject.toString();
    }

    private void persist(Set<String> set) {
        String identifier = this.options.getIdentifier();
        SharedPreferences.Editor edit = getPrefs("NOTIFICATION_ID").edit();
        edit.putString(identifier, this.options.toString());
        edit.apply();
        if (set != null) {
            SharedPreferences.Editor edit2 = getPrefs(PREF_KEY_PID).edit();
            edit2.putStringSet(identifier, set);
            edit2.apply();
        }
    }

    private void unpersist() {
        String[] strArr = {"NOTIFICATION_ID", PREF_KEY_PID};
        String identifier = this.options.getIdentifier();
        for (String prefs : strArr) {
            SharedPreferences.Editor edit = getPrefs(prefs).edit();
            edit.remove(identifier);
            edit.apply();
        }
    }

    private void grantPermissionToPlaySoundFromExternal() {
        NotificationCompat.Builder builder2 = this.builder;
        if (builder2 != null) {
            this.context.grantUriPermission("com.android.systemui", Uri.parse(builder2.getExtras().getString("NOTIFICATION_SOUND")), 1);
        }
    }

    private void mergeJSONObjects(JSONObject jSONObject) {
        JSONObject dict = this.options.getDict();
        Iterator<String> keys = jSONObject.keys();
        while (keys.hasNext()) {
            try {
                String next = keys.next();
                dict.put(next, jSONObject.opt(next));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void cacheBuilder() {
        if (cache == null) {
            cache = new SparseArray<>();
        }
        cache.put(getId(), this.builder);
    }

    static NotificationCompat.Builder getCachedBuilder(int i) {
        SparseArray<NotificationCompat.Builder> sparseArray = cache;
        if (sparseArray != null) {
            return sparseArray.get(i);
        }
        return null;
    }

    private void clearCache() {
        SparseArray<NotificationCompat.Builder> sparseArray = cache;
        if (sparseArray != null) {
            sparseArray.delete(getId());
        }
    }

    private SharedPreferences getPrefs(String str) {
        return this.context.getSharedPreferences(str, 0);
    }

    private NotificationManager getNotMgr() {
        return (NotificationManager) this.context.getSystemService("notification");
    }

    private AlarmManager getAlarmMgr() {
        return (AlarmManager) this.context.getSystemService(NotificationCompat.CATEGORY_ALARM);
    }
}
