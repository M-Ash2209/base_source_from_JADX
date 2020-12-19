package p005de.appplant.cordova.plugin.localnotification;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.support.p000v4.app.NotificationCompat;
import android.util.Pair;
import com.adobe.phonegap.push.PushConstants;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.apache.cordova.globalization.Globalization;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import p005de.appplant.cordova.plugin.notification.Manager;
import p005de.appplant.cordova.plugin.notification.Notification;
import p005de.appplant.cordova.plugin.notification.Options;
import p005de.appplant.cordova.plugin.notification.Request;
import p005de.appplant.cordova.plugin.notification.action.ActionGroup;

/* renamed from: de.appplant.cordova.plugin.localnotification.LocalNotification */
public class LocalNotification extends CordovaPlugin {
    private static Boolean deviceready = false;
    private static ArrayList<String> eventQueue = new ArrayList<>();
    private static Pair<Integer, String> launchDetails;
    private static WeakReference<CordovaWebView> webView;

    public void initialize(CordovaInterface cordovaInterface, CordovaWebView cordovaWebView) {
        webView = new WeakReference<>(cordovaWebView);
    }

    public void onResume(boolean z) {
        super.onResume(z);
        deviceready();
    }

    public void onDestroy() {
        deviceready = false;
    }

    public boolean execute(final String str, final JSONArray jSONArray, final CallbackContext callbackContext) throws JSONException {
        if (str.equals("launch")) {
            launch(callbackContext);
            return true;
        }
        this.f59cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                if (str.equals("ready")) {
                    LocalNotification.deviceready();
                } else if (str.equals("check")) {
                    LocalNotification.this.check(callbackContext);
                } else if (str.equals("request")) {
                    LocalNotification.this.request(callbackContext);
                } else if (str.equals(PushConstants.ACTIONS)) {
                    LocalNotification.this.actions(jSONArray, callbackContext);
                } else if (str.equals("schedule")) {
                    LocalNotification.this.schedule(jSONArray, callbackContext);
                } else if (str.equals("update")) {
                    LocalNotification.this.update(jSONArray, callbackContext);
                } else if (str.equals("cancel")) {
                    LocalNotification.this.cancel(jSONArray, callbackContext);
                } else if (str.equals("cancelAll")) {
                    LocalNotification.this.cancelAll(callbackContext);
                } else if (str.equals("clear")) {
                    LocalNotification.this.clear(jSONArray, callbackContext);
                } else if (str.equals("clearAll")) {
                    LocalNotification.this.clearAll(callbackContext);
                } else if (str.equals(Globalization.TYPE)) {
                    LocalNotification.this.type(jSONArray, callbackContext);
                } else if (str.equals("ids")) {
                    LocalNotification.this.ids(jSONArray, callbackContext);
                } else if (str.equals("notification")) {
                    LocalNotification.this.notification(jSONArray, callbackContext);
                } else if (str.equals("notifications")) {
                    LocalNotification.this.notifications(jSONArray, callbackContext);
                }
            }
        });
        return true;
    }

    @SuppressLint({"DefaultLocale"})
    private void launch(CallbackContext callbackContext) {
        if (launchDetails != null) {
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put(PushConstants.CHANNEL_ID, launchDetails.first);
                jSONObject.put("action", launchDetails.second);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            callbackContext.success(jSONObject);
            launchDetails = null;
        }
    }

    /* access modifiers changed from: private */
    public void check(CallbackContext callbackContext) {
        success(callbackContext, getNotMgr().hasPermission());
    }

    /* access modifiers changed from: private */
    public void request(CallbackContext callbackContext) {
        check(callbackContext);
    }

    /* access modifiers changed from: private */
    public void actions(JSONArray jSONArray, CallbackContext callbackContext) {
        int optInt = jSONArray.optInt(0);
        String optString = jSONArray.optString(1);
        JSONArray optJSONArray = jSONArray.optJSONArray(2);
        Activity activity = this.f59cordova.getActivity();
        switch (optInt) {
            case 0:
                ActionGroup.register(ActionGroup.parse(activity, optString, optJSONArray));
                callbackContext.success();
                return;
            case 1:
                ActionGroup.unregister(optString);
                callbackContext.success();
                return;
            case 2:
                success(callbackContext, ActionGroup.isRegistered(optString));
                return;
            default:
                return;
        }
    }

    /* access modifiers changed from: private */
    public void schedule(JSONArray jSONArray, CallbackContext callbackContext) {
        Manager notMgr = getNotMgr();
        for (int i = 0; i < jSONArray.length(); i++) {
            Notification schedule = notMgr.schedule(new Request(new Options(jSONArray.optJSONObject(i))), TriggerReceiver.class);
            if (schedule != null) {
                fireEvent("add", schedule);
            }
        }
        check(callbackContext);
    }

    /* access modifiers changed from: private */
    public void update(JSONArray jSONArray, CallbackContext callbackContext) {
        Manager notMgr = getNotMgr();
        for (int i = 0; i < jSONArray.length(); i++) {
            JSONObject optJSONObject = jSONArray.optJSONObject(i);
            Notification update = notMgr.update(optJSONObject.optInt(PushConstants.CHANNEL_ID, 0), optJSONObject, TriggerReceiver.class);
            if (update != null) {
                fireEvent("update", update);
            }
        }
        check(callbackContext);
    }

    /* access modifiers changed from: private */
    public void cancel(JSONArray jSONArray, CallbackContext callbackContext) {
        Manager notMgr = getNotMgr();
        for (int i = 0; i < jSONArray.length(); i++) {
            Notification cancel = notMgr.cancel(jSONArray.optInt(i, 0));
            if (cancel != null) {
                fireEvent("cancel", cancel);
            }
        }
        callbackContext.success();
    }

    /* access modifiers changed from: private */
    public void cancelAll(CallbackContext callbackContext) {
        getNotMgr().cancelAll();
        fireEvent("cancelall");
        callbackContext.success();
    }

    /* access modifiers changed from: private */
    public void clear(JSONArray jSONArray, CallbackContext callbackContext) {
        Manager notMgr = getNotMgr();
        for (int i = 0; i < jSONArray.length(); i++) {
            Notification clear = notMgr.clear(jSONArray.optInt(i, 0));
            if (clear != null) {
                fireEvent("clear", clear);
            }
        }
        callbackContext.success();
    }

    /* access modifiers changed from: private */
    public void clearAll(CallbackContext callbackContext) {
        getNotMgr().clearAll();
        fireEvent("clearall");
        callbackContext.success();
    }

    /* access modifiers changed from: private */
    public void type(JSONArray jSONArray, CallbackContext callbackContext) {
        if (getNotMgr().get(jSONArray.optInt(0)) == null) {
            callbackContext.success("unknown");
            return;
        }
        switch (r2.getType()) {
            case SCHEDULED:
                callbackContext.success("scheduled");
                return;
            case TRIGGERED:
                callbackContext.success("triggered");
                return;
            default:
                callbackContext.success("unknown");
                return;
        }
    }

    /* access modifiers changed from: private */
    public void ids(JSONArray jSONArray, CallbackContext callbackContext) {
        Collection collection;
        int optInt = jSONArray.optInt(0);
        Manager notMgr = getNotMgr();
        switch (optInt) {
            case 0:
                collection = notMgr.getIds();
                break;
            case 1:
                collection = notMgr.getIdsByType(Notification.Type.SCHEDULED);
                break;
            case 2:
                collection = notMgr.getIdsByType(Notification.Type.TRIGGERED);
                break;
            default:
                collection = new ArrayList(0);
                break;
        }
        callbackContext.success(new JSONArray(collection));
    }

    /* access modifiers changed from: private */
    public void notification(JSONArray jSONArray, CallbackContext callbackContext) {
        Options options = getNotMgr().getOptions(jSONArray.optInt(0));
        if (options != null) {
            callbackContext.success(options.getDict());
        } else {
            callbackContext.success();
        }
    }

    /* access modifiers changed from: private */
    public void notifications(JSONArray jSONArray, CallbackContext callbackContext) {
        List<JSONObject> list;
        int optInt = jSONArray.optInt(0);
        JSONArray optJSONArray = jSONArray.optJSONArray(1);
        Manager notMgr = getNotMgr();
        switch (optInt) {
            case 0:
                list = notMgr.getOptions();
                break;
            case 1:
                list = notMgr.getOptionsByType(Notification.Type.SCHEDULED);
                break;
            case 2:
                list = notMgr.getOptionsByType(Notification.Type.TRIGGERED);
                break;
            case 3:
                list = notMgr.getOptionsById(toList(optJSONArray));
                break;
            default:
                list = new ArrayList<>(0);
                break;
        }
        callbackContext.success(new JSONArray(list));
    }

    /* access modifiers changed from: private */
    public static synchronized void deviceready() {
        synchronized (LocalNotification.class) {
            deviceready = true;
            Iterator<String> it = eventQueue.iterator();
            while (it.hasNext()) {
                sendJavascript(it.next());
            }
            eventQueue.clear();
        }
    }

    private void success(CallbackContext callbackContext, boolean z) {
        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, z));
    }

    private void fireEvent(String str) {
        fireEvent(str, (Notification) null, new JSONObject());
    }

    static void fireEvent(String str, Notification notification) {
        fireEvent(str, notification, new JSONObject());
    }

    static void fireEvent(String str, Notification notification, JSONObject jSONObject) {
        String str2;
        try {
            jSONObject.put(NotificationCompat.CATEGORY_EVENT, str);
            jSONObject.put(PushConstants.FOREGROUND, isInForeground());
            jSONObject.put("queued", !deviceready.booleanValue());
            if (notification != null) {
                jSONObject.put("notification", notification.getId());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (notification != null) {
            str2 = notification.toString() + "," + jSONObject.toString();
        } else {
            str2 = jSONObject.toString();
        }
        String str3 = "cordova.plugins.notification.local.fireEvent(\"" + str + "\"," + str2 + ")";
        if (launchDetails == null && !deviceready.booleanValue() && notification != null) {
            launchDetails = new Pair<>(Integer.valueOf(notification.getId()), str);
        }
        sendJavascript(str3);
    }

    private static synchronized void sendJavascript(final String str) {
        synchronized (LocalNotification.class) {
            if (deviceready.booleanValue()) {
                if (webView != null) {
                    final CordovaWebView cordovaWebView = (CordovaWebView) webView.get();
                    ((Activity) cordovaWebView.getContext()).runOnUiThread(new Runnable() {
                        public void run() {
                            CordovaWebView cordovaWebView = cordovaWebView;
                            cordovaWebView.loadUrl("javascript:" + str);
                        }
                    });
                    return;
                }
            }
            eventQueue.add(str);
        }
    }

    private static boolean isInForeground() {
        WeakReference<CordovaWebView> weakReference;
        if (!deviceready.booleanValue() || (weakReference = webView) == null) {
            return false;
        }
        CordovaWebView cordovaWebView = (CordovaWebView) weakReference.get();
        KeyguardManager keyguardManager = (KeyguardManager) cordovaWebView.getContext().getSystemService("keyguard");
        if ((keyguardManager == null || !keyguardManager.isKeyguardLocked()) && cordovaWebView.getView().getWindowVisibility() == 0) {
            return true;
        }
        return false;
    }

    static boolean isAppRunning() {
        return webView != null;
    }

    private List<Integer> toList(JSONArray jSONArray) {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < jSONArray.length(); i++) {
            arrayList.add(Integer.valueOf(jSONArray.optInt(i)));
        }
        return arrayList;
    }

    private Manager getNotMgr() {
        return Manager.getInstance(this.f59cordova.getActivity());
    }
}
