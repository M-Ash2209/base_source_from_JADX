package com.adobe.phonegap.push;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.p000v4.app.NotificationManagerCompat;
import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import p007me.leolin.shortcutbadger.ShortcutBadger;

public class PushPlugin extends CordovaPlugin implements PushConstants {
    public static final String LOG_TAG = "Push_Plugin";
    /* access modifiers changed from: private */
    public static List<Bundle> gCachedExtras = Collections.synchronizedList(new ArrayList());
    private static boolean gForeground = false;
    private static CordovaWebView gWebView;
    /* access modifiers changed from: private */
    public static CallbackContext pushContext;
    /* access modifiers changed from: private */
    public static String registration_id = "";

    /* access modifiers changed from: private */
    public Context getApplicationContext() {
        return this.f59cordova.getActivity().getApplicationContext();
    }

    /* access modifiers changed from: private */
    @TargetApi(26)
    public JSONArray listChannels() throws JSONException {
        JSONArray jSONArray = new JSONArray();
        if (Build.VERSION.SDK_INT >= 26) {
            for (NotificationChannel next : ((NotificationManager) this.f59cordova.getActivity().getSystemService("notification")).getNotificationChannels()) {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put(PushConstants.CHANNEL_ID, next.getId());
                jSONObject.put(PushConstants.CHANNEL_DESCRIPTION, next.getDescription());
                jSONArray.put(jSONObject);
            }
        }
        return jSONArray;
    }

    /* access modifiers changed from: private */
    @TargetApi(26)
    public void deleteChannel(String str) {
        if (Build.VERSION.SDK_INT >= 26) {
            ((NotificationManager) this.f59cordova.getActivity().getSystemService("notification")).deleteNotificationChannel(str);
        }
    }

    /* access modifiers changed from: private */
    @TargetApi(26)
    public void createChannel(JSONObject jSONObject) throws JSONException {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager notificationManager = (NotificationManager) this.f59cordova.getActivity().getSystemService("notification");
            String packageName = getApplicationContext().getPackageName();
            NotificationChannel notificationChannel = new NotificationChannel(jSONObject.getString(PushConstants.CHANNEL_ID), jSONObject.optString(PushConstants.CHANNEL_DESCRIPTION, ""), jSONObject.optInt(PushConstants.CHANNEL_IMPORTANCE, 3));
            int optInt = jSONObject.optInt(PushConstants.CHANNEL_LIGHT_COLOR, -1);
            if (optInt != -1) {
                notificationChannel.setLightColor(optInt);
            }
            notificationChannel.setLockscreenVisibility(jSONObject.optInt(PushConstants.VISIBILITY, 1));
            notificationChannel.setShowBadge(jSONObject.optBoolean(PushConstants.BADGE, true));
            String optString = jSONObject.optString(PushConstants.SOUND, PushConstants.SOUND_DEFAULT);
            AudioAttributes build = new AudioAttributes.Builder().setContentType(4).setUsage(6).build();
            if (PushConstants.SOUND_RINGTONE.equals(optString)) {
                notificationChannel.setSound(Settings.System.DEFAULT_RINGTONE_URI, build);
            } else if (optString == null || optString.contentEquals(PushConstants.SOUND_DEFAULT)) {
                notificationChannel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, build);
            } else {
                notificationChannel.setSound(Uri.parse("android.resource://" + packageName + "/raw/" + optString), build);
            }
            JSONArray optJSONArray = jSONObject.optJSONArray(PushConstants.CHANNEL_VIBRATION);
            if (optJSONArray != null) {
                int length = optJSONArray.length();
                long[] jArr = new long[length];
                for (int i = 0; i < length; i++) {
                    jArr[i] = optJSONArray.optLong(i);
                }
                notificationChannel.setVibrationPattern(jArr);
            } else {
                notificationChannel.enableVibration(jSONObject.optBoolean(PushConstants.CHANNEL_VIBRATION, true));
            }
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    /* access modifiers changed from: private */
    @TargetApi(26)
    public void createDefaultNotificationChannelIfNeeded(JSONObject jSONObject) {
        if (Build.VERSION.SDK_INT >= 26) {
            List<NotificationChannel> notificationChannels = ((NotificationManager) this.f59cordova.getActivity().getSystemService("notification")).getNotificationChannels();
            int i = 0;
            while (i < notificationChannels.size()) {
                if (!notificationChannels.get(i).getId().equals(PushConstants.DEFAULT_CHANNEL_ID)) {
                    i++;
                } else {
                    return;
                }
            }
            try {
                jSONObject.put(PushConstants.CHANNEL_ID, PushConstants.DEFAULT_CHANNEL_ID);
                jSONObject.putOpt(PushConstants.CHANNEL_DESCRIPTION, "PhoneGap PushPlugin");
                createChannel(jSONObject);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "execute: Got JSON Exception " + e.getMessage());
            }
        }
    }

    public boolean execute(String str, final JSONArray jSONArray, final CallbackContext callbackContext) {
        Log.v(LOG_TAG, "execute: action=" + str);
        gWebView = this.webView;
        if (PushConstants.INITIALIZE.equals(str)) {
            this.f59cordova.getThreadPool().execute(new Runnable() {
                /* JADX WARNING: Removed duplicated region for block: B:45:0x01a3  */
                /* JADX WARNING: Removed duplicated region for block: B:61:0x0238  */
                /* JADX WARNING: Removed duplicated region for block: B:78:? A[ORIG_RETURN, RETURN, SYNTHETIC] */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void run() {
                    /*
                        r10 = this;
                        org.apache.cordova.CallbackContext r0 = r6
                        org.apache.cordova.CallbackContext unused = com.adobe.phonegap.push.PushPlugin.pushContext = r0
                        java.lang.String r0 = "Push_Plugin"
                        java.lang.StringBuilder r1 = new java.lang.StringBuilder
                        r1.<init>()
                        java.lang.String r2 = "execute: data="
                        r1.append(r2)
                        org.json.JSONArray r2 = r5
                        java.lang.String r2 = r2.toString()
                        r1.append(r2)
                        java.lang.String r1 = r1.toString()
                        android.util.Log.v(r0, r1)
                        com.adobe.phonegap.push.PushPlugin r0 = com.adobe.phonegap.push.PushPlugin.this
                        android.content.Context r0 = r0.getApplicationContext()
                        java.lang.String r1 = "com.adobe.phonegap.push"
                        r2 = 0
                        android.content.SharedPreferences r0 = r0.getSharedPreferences(r1, r2)
                        r1 = 0
                        org.json.JSONArray r3 = r5     // Catch:{ JSONException -> 0x017a, IOException -> 0x0152, NotFoundException -> 0x012a }
                        org.json.JSONObject r3 = r3.getJSONObject(r2)     // Catch:{ JSONException -> 0x017a, IOException -> 0x0152, NotFoundException -> 0x012a }
                        java.lang.String r4 = "android"
                        org.json.JSONObject r3 = r3.getJSONObject(r4)     // Catch:{ JSONException -> 0x017a, IOException -> 0x0152, NotFoundException -> 0x012a }
                        com.adobe.phonegap.push.PushPlugin r4 = com.adobe.phonegap.push.PushPlugin.this     // Catch:{ JSONException -> 0x0125, IOException -> 0x0120, NotFoundException -> 0x011b }
                        r4.createDefaultNotificationChannelIfNeeded(r3)     // Catch:{ JSONException -> 0x0125, IOException -> 0x0120, NotFoundException -> 0x011b }
                        java.lang.String r4 = "Push_Plugin"
                        java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x0125, IOException -> 0x0120, NotFoundException -> 0x011b }
                        r5.<init>()     // Catch:{ JSONException -> 0x0125, IOException -> 0x0120, NotFoundException -> 0x011b }
                        java.lang.String r6 = "execute: jo="
                        r5.append(r6)     // Catch:{ JSONException -> 0x0125, IOException -> 0x0120, NotFoundException -> 0x011b }
                        java.lang.String r6 = r3.toString()     // Catch:{ JSONException -> 0x0125, IOException -> 0x0120, NotFoundException -> 0x011b }
                        r5.append(r6)     // Catch:{ JSONException -> 0x0125, IOException -> 0x0120, NotFoundException -> 0x011b }
                        java.lang.String r5 = r5.toString()     // Catch:{ JSONException -> 0x0125, IOException -> 0x0120, NotFoundException -> 0x011b }
                        android.util.Log.v(r4, r5)     // Catch:{ JSONException -> 0x0125, IOException -> 0x0120, NotFoundException -> 0x011b }
                        com.adobe.phonegap.push.PushPlugin r4 = com.adobe.phonegap.push.PushPlugin.this     // Catch:{ JSONException -> 0x0125, IOException -> 0x0120, NotFoundException -> 0x011b }
                        java.lang.String r5 = "gcm_defaultSenderId"
                        java.lang.String r4 = r4.getStringResourceByName(r5)     // Catch:{ JSONException -> 0x0125, IOException -> 0x0120, NotFoundException -> 0x011b }
                        java.lang.String r5 = "Push_Plugin"
                        java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x0118, IOException -> 0x0116, NotFoundException -> 0x0114 }
                        r6.<init>()     // Catch:{ JSONException -> 0x0118, IOException -> 0x0116, NotFoundException -> 0x0114 }
                        java.lang.String r7 = "execute: senderID="
                        r6.append(r7)     // Catch:{ JSONException -> 0x0118, IOException -> 0x0116, NotFoundException -> 0x0114 }
                        r6.append(r4)     // Catch:{ JSONException -> 0x0118, IOException -> 0x0116, NotFoundException -> 0x0114 }
                        java.lang.String r6 = r6.toString()     // Catch:{ JSONException -> 0x0118, IOException -> 0x0116, NotFoundException -> 0x0114 }
                        android.util.Log.v(r5, r6)     // Catch:{ JSONException -> 0x0118, IOException -> 0x0116, NotFoundException -> 0x0114 }
                        com.google.firebase.iid.FirebaseInstanceId r5 = com.google.firebase.iid.FirebaseInstanceId.getInstance()     // Catch:{ IllegalStateException -> 0x0081 }
                        java.lang.String r1 = r5.getToken()     // Catch:{ IllegalStateException -> 0x0081 }
                        goto L_0x009c
                    L_0x0081:
                        r5 = move-exception
                        java.lang.String r6 = "Push_Plugin"
                        java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x0118, IOException -> 0x0116, NotFoundException -> 0x0114 }
                        r7.<init>()     // Catch:{ JSONException -> 0x0118, IOException -> 0x0116, NotFoundException -> 0x0114 }
                        java.lang.String r8 = "Exception raised while getting Firebase token "
                        r7.append(r8)     // Catch:{ JSONException -> 0x0118, IOException -> 0x0116, NotFoundException -> 0x0114 }
                        java.lang.String r5 = r5.getMessage()     // Catch:{ JSONException -> 0x0118, IOException -> 0x0116, NotFoundException -> 0x0114 }
                        r7.append(r5)     // Catch:{ JSONException -> 0x0118, IOException -> 0x0116, NotFoundException -> 0x0114 }
                        java.lang.String r5 = r7.toString()     // Catch:{ JSONException -> 0x0118, IOException -> 0x0116, NotFoundException -> 0x0114 }
                        android.util.Log.e(r6, r5)     // Catch:{ JSONException -> 0x0118, IOException -> 0x0116, NotFoundException -> 0x0114 }
                    L_0x009c:
                        if (r1 != 0) goto L_0x00c4
                        com.google.firebase.iid.FirebaseInstanceId r5 = com.google.firebase.iid.FirebaseInstanceId.getInstance()     // Catch:{ IllegalStateException -> 0x00a9 }
                        java.lang.String r6 = "FCM"
                        java.lang.String r1 = r5.getToken(r4, r6)     // Catch:{ IllegalStateException -> 0x00a9 }
                        goto L_0x00c4
                    L_0x00a9:
                        r5 = move-exception
                        java.lang.String r6 = "Push_Plugin"
                        java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x0118, IOException -> 0x0116, NotFoundException -> 0x0114 }
                        r7.<init>()     // Catch:{ JSONException -> 0x0118, IOException -> 0x0116, NotFoundException -> 0x0114 }
                        java.lang.String r8 = "Exception raised while getting Firebase token "
                        r7.append(r8)     // Catch:{ JSONException -> 0x0118, IOException -> 0x0116, NotFoundException -> 0x0114 }
                        java.lang.String r5 = r5.getMessage()     // Catch:{ JSONException -> 0x0118, IOException -> 0x0116, NotFoundException -> 0x0114 }
                        r7.append(r5)     // Catch:{ JSONException -> 0x0118, IOException -> 0x0116, NotFoundException -> 0x0114 }
                        java.lang.String r5 = r7.toString()     // Catch:{ JSONException -> 0x0118, IOException -> 0x0116, NotFoundException -> 0x0114 }
                        android.util.Log.e(r6, r5)     // Catch:{ JSONException -> 0x0118, IOException -> 0x0116, NotFoundException -> 0x0114 }
                    L_0x00c4:
                        java.lang.String r5 = ""
                        boolean r5 = r5.equals(r1)     // Catch:{ JSONException -> 0x0118, IOException -> 0x0116, NotFoundException -> 0x0114 }
                        if (r5 != 0) goto L_0x010c
                        org.json.JSONObject r5 = new org.json.JSONObject     // Catch:{ JSONException -> 0x0118, IOException -> 0x0116, NotFoundException -> 0x0114 }
                        r5.<init>()     // Catch:{ JSONException -> 0x0118, IOException -> 0x0116, NotFoundException -> 0x0114 }
                        java.lang.String r6 = "registrationId"
                        org.json.JSONObject r1 = r5.put(r6, r1)     // Catch:{ JSONException -> 0x0118, IOException -> 0x0116, NotFoundException -> 0x0114 }
                        java.lang.String r5 = "registrationType"
                        java.lang.String r6 = "FCM"
                        r1.put(r5, r6)     // Catch:{ JSONException -> 0x0118, IOException -> 0x0116, NotFoundException -> 0x0114 }
                        java.lang.String r5 = "Push_Plugin"
                        java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x0118, IOException -> 0x0116, NotFoundException -> 0x0114 }
                        r6.<init>()     // Catch:{ JSONException -> 0x0118, IOException -> 0x0116, NotFoundException -> 0x0114 }
                        java.lang.String r7 = "onRegistered: "
                        r6.append(r7)     // Catch:{ JSONException -> 0x0118, IOException -> 0x0116, NotFoundException -> 0x0114 }
                        java.lang.String r7 = r1.toString()     // Catch:{ JSONException -> 0x0118, IOException -> 0x0116, NotFoundException -> 0x0114 }
                        r6.append(r7)     // Catch:{ JSONException -> 0x0118, IOException -> 0x0116, NotFoundException -> 0x0114 }
                        java.lang.String r6 = r6.toString()     // Catch:{ JSONException -> 0x0118, IOException -> 0x0116, NotFoundException -> 0x0114 }
                        android.util.Log.v(r5, r6)     // Catch:{ JSONException -> 0x0118, IOException -> 0x0116, NotFoundException -> 0x0114 }
                        java.lang.String r5 = "topics"
                        org.json.JSONArray r5 = r3.optJSONArray(r5)     // Catch:{ JSONException -> 0x0118, IOException -> 0x0116, NotFoundException -> 0x0114 }
                        com.adobe.phonegap.push.PushPlugin r6 = com.adobe.phonegap.push.PushPlugin.this     // Catch:{ JSONException -> 0x0118, IOException -> 0x0116, NotFoundException -> 0x0114 }
                        java.lang.String r7 = com.adobe.phonegap.push.PushPlugin.registration_id     // Catch:{ JSONException -> 0x0118, IOException -> 0x0116, NotFoundException -> 0x0114 }
                        r6.subscribeToTopics(r5, r7)     // Catch:{ JSONException -> 0x0118, IOException -> 0x0116, NotFoundException -> 0x0114 }
                        com.adobe.phonegap.push.PushPlugin.sendEvent(r1)     // Catch:{ JSONException -> 0x0118, IOException -> 0x0116, NotFoundException -> 0x0114 }
                        goto L_0x01a1
                    L_0x010c:
                        org.apache.cordova.CallbackContext r1 = r6     // Catch:{ JSONException -> 0x0118, IOException -> 0x0116, NotFoundException -> 0x0114 }
                        java.lang.String r5 = "Empty registration ID received from FCM"
                        r1.error((java.lang.String) r5)     // Catch:{ JSONException -> 0x0118, IOException -> 0x0116, NotFoundException -> 0x0114 }
                        return
                    L_0x0114:
                        r1 = move-exception
                        goto L_0x012e
                    L_0x0116:
                        r1 = move-exception
                        goto L_0x0156
                    L_0x0118:
                        r1 = move-exception
                        goto L_0x017e
                    L_0x011b:
                        r4 = move-exception
                        r9 = r4
                        r4 = r1
                        r1 = r9
                        goto L_0x012e
                    L_0x0120:
                        r4 = move-exception
                        r9 = r4
                        r4 = r1
                        r1 = r9
                        goto L_0x0156
                    L_0x0125:
                        r4 = move-exception
                        r9 = r4
                        r4 = r1
                        r1 = r9
                        goto L_0x017e
                    L_0x012a:
                        r3 = move-exception
                        r4 = r1
                        r1 = r3
                        r3 = r4
                    L_0x012e:
                        java.lang.String r5 = "Push_Plugin"
                        java.lang.StringBuilder r6 = new java.lang.StringBuilder
                        r6.<init>()
                        java.lang.String r7 = "execute: Got Resources NotFoundException "
                        r6.append(r7)
                        java.lang.String r7 = r1.getMessage()
                        r6.append(r7)
                        java.lang.String r6 = r6.toString()
                        android.util.Log.e(r5, r6)
                        org.apache.cordova.CallbackContext r5 = r6
                        java.lang.String r1 = r1.getMessage()
                        r5.error((java.lang.String) r1)
                        goto L_0x01a1
                    L_0x0152:
                        r3 = move-exception
                        r4 = r1
                        r1 = r3
                        r3 = r4
                    L_0x0156:
                        java.lang.String r5 = "Push_Plugin"
                        java.lang.StringBuilder r6 = new java.lang.StringBuilder
                        r6.<init>()
                        java.lang.String r7 = "execute: Got IO Exception "
                        r6.append(r7)
                        java.lang.String r7 = r1.getMessage()
                        r6.append(r7)
                        java.lang.String r6 = r6.toString()
                        android.util.Log.e(r5, r6)
                        org.apache.cordova.CallbackContext r5 = r6
                        java.lang.String r1 = r1.getMessage()
                        r5.error((java.lang.String) r1)
                        goto L_0x01a1
                    L_0x017a:
                        r3 = move-exception
                        r4 = r1
                        r1 = r3
                        r3 = r4
                    L_0x017e:
                        java.lang.String r5 = "Push_Plugin"
                        java.lang.StringBuilder r6 = new java.lang.StringBuilder
                        r6.<init>()
                        java.lang.String r7 = "execute: Got JSON Exception "
                        r6.append(r7)
                        java.lang.String r7 = r1.getMessage()
                        r6.append(r7)
                        java.lang.String r6 = r6.toString()
                        android.util.Log.e(r5, r6)
                        org.apache.cordova.CallbackContext r5 = r6
                        java.lang.String r1 = r1.getMessage()
                        r5.error((java.lang.String) r1)
                    L_0x01a1:
                        if (r3 == 0) goto L_0x022e
                        android.content.SharedPreferences$Editor r0 = r0.edit()
                        java.lang.String r1 = "icon"
                        java.lang.String r5 = "icon"
                        java.lang.String r5 = r3.getString(r5)     // Catch:{ JSONException -> 0x01b3 }
                        r0.putString(r1, r5)     // Catch:{ JSONException -> 0x01b3 }
                        goto L_0x01ba
                    L_0x01b3:
                        java.lang.String r1 = "Push_Plugin"
                        java.lang.String r5 = "no icon option"
                        android.util.Log.d(r1, r5)
                    L_0x01ba:
                        java.lang.String r1 = "iconColor"
                        java.lang.String r5 = "iconColor"
                        java.lang.String r5 = r3.getString(r5)     // Catch:{ JSONException -> 0x01c6 }
                        r0.putString(r1, r5)     // Catch:{ JSONException -> 0x01c6 }
                        goto L_0x01cd
                    L_0x01c6:
                        java.lang.String r1 = "Push_Plugin"
                        java.lang.String r5 = "no iconColor option"
                        android.util.Log.d(r1, r5)
                    L_0x01cd:
                        java.lang.String r1 = "clearBadge"
                        boolean r1 = r3.optBoolean(r1, r2)
                        if (r1 == 0) goto L_0x01de
                        com.adobe.phonegap.push.PushPlugin r5 = com.adobe.phonegap.push.PushPlugin.this
                        android.content.Context r5 = r5.getApplicationContext()
                        com.adobe.phonegap.push.PushPlugin.setApplicationIconBadgeNumber(r5, r2)
                    L_0x01de:
                        java.lang.String r5 = "sound"
                        java.lang.String r6 = "sound"
                        r7 = 1
                        boolean r6 = r3.optBoolean(r6, r7)
                        r0.putBoolean(r5, r6)
                        java.lang.String r5 = "vibrate"
                        java.lang.String r6 = "vibrate"
                        boolean r6 = r3.optBoolean(r6, r7)
                        r0.putBoolean(r5, r6)
                        java.lang.String r5 = "clearBadge"
                        r0.putBoolean(r5, r1)
                        java.lang.String r1 = "clearNotifications"
                        java.lang.String r5 = "clearNotifications"
                        boolean r5 = r3.optBoolean(r5, r7)
                        r0.putBoolean(r1, r5)
                        java.lang.String r1 = "forceShow"
                        java.lang.String r5 = "forceShow"
                        boolean r2 = r3.optBoolean(r5, r2)
                        r0.putBoolean(r1, r2)
                        java.lang.String r1 = "senderID"
                        r0.putString(r1, r4)
                        java.lang.String r1 = "messageKey"
                        java.lang.String r2 = "messageKey"
                        java.lang.String r2 = r3.optString(r2)
                        r0.putString(r1, r2)
                        java.lang.String r1 = "titleKey"
                        java.lang.String r2 = "titleKey"
                        java.lang.String r2 = r3.optString(r2)
                        r0.putString(r1, r2)
                        r0.commit()
                    L_0x022e:
                        java.util.List r0 = com.adobe.phonegap.push.PushPlugin.gCachedExtras
                        boolean r0 = r0.isEmpty()
                        if (r0 != 0) goto L_0x0268
                        java.lang.String r0 = "Push_Plugin"
                        java.lang.String r1 = "sending cached extras"
                        android.util.Log.v(r0, r1)
                        java.util.List r0 = com.adobe.phonegap.push.PushPlugin.gCachedExtras
                        monitor-enter(r0)
                        java.util.List r1 = com.adobe.phonegap.push.PushPlugin.gCachedExtras     // Catch:{ all -> 0x0265 }
                        java.util.Iterator r1 = r1.iterator()     // Catch:{ all -> 0x0265 }
                    L_0x024c:
                        boolean r2 = r1.hasNext()     // Catch:{ all -> 0x0265 }
                        if (r2 == 0) goto L_0x025c
                        java.lang.Object r2 = r1.next()     // Catch:{ all -> 0x0265 }
                        android.os.Bundle r2 = (android.os.Bundle) r2     // Catch:{ all -> 0x0265 }
                        com.adobe.phonegap.push.PushPlugin.sendExtras(r2)     // Catch:{ all -> 0x0265 }
                        goto L_0x024c
                    L_0x025c:
                        monitor-exit(r0)     // Catch:{ all -> 0x0265 }
                        java.util.List r0 = com.adobe.phonegap.push.PushPlugin.gCachedExtras
                        r0.clear()
                        goto L_0x0268
                    L_0x0265:
                        r1 = move-exception
                        monitor-exit(r0)     // Catch:{ all -> 0x0265 }
                        throw r1
                    L_0x0268:
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.adobe.phonegap.push.PushPlugin.C03031.run():void");
                }
            });
            return true;
        } else if (PushConstants.UNREGISTER.equals(str)) {
            this.f59cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    try {
                        SharedPreferences sharedPreferences = PushPlugin.this.getApplicationContext().getSharedPreferences(PushConstants.COM_ADOBE_PHONEGAP_PUSH, 0);
                        JSONArray optJSONArray = jSONArray.optJSONArray(0);
                        if (optJSONArray == null || "".equals(PushPlugin.registration_id)) {
                            FirebaseInstanceId.getInstance().deleteInstanceId();
                            Log.v(PushPlugin.LOG_TAG, "UNREGISTER");
                            SharedPreferences.Editor edit = sharedPreferences.edit();
                            edit.remove(PushConstants.SOUND);
                            edit.remove(PushConstants.VIBRATE);
                            edit.remove(PushConstants.CLEAR_BADGE);
                            edit.remove(PushConstants.CLEAR_NOTIFICATIONS);
                            edit.remove(PushConstants.FORCE_SHOW);
                            edit.remove(PushConstants.SENDER_ID);
                            edit.commit();
                        } else {
                            PushPlugin.this.unsubscribeFromTopics(optJSONArray, PushPlugin.registration_id);
                        }
                        callbackContext.success();
                    } catch (IOException e) {
                        Log.e(PushPlugin.LOG_TAG, "execute: Got JSON Exception " + e.getMessage());
                        callbackContext.error(e.getMessage());
                    }
                }
            });
            return true;
        } else if (PushConstants.FINISH.equals(str)) {
            callbackContext.success();
            return true;
        } else if (PushConstants.HAS_PERMISSION.equals(str)) {
            this.f59cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    JSONObject jSONObject = new JSONObject();
                    try {
                        Log.d(PushPlugin.LOG_TAG, "has permission: " + NotificationManagerCompat.from(PushPlugin.this.getApplicationContext()).areNotificationsEnabled());
                        jSONObject.put("isEnabled", NotificationManagerCompat.from(PushPlugin.this.getApplicationContext()).areNotificationsEnabled());
                        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jSONObject);
                        pluginResult.setKeepCallback(true);
                        callbackContext.sendPluginResult(pluginResult);
                    } catch (UnknownError e) {
                        callbackContext.error(e.getMessage());
                    } catch (JSONException e2) {
                        callbackContext.error(e2.getMessage());
                    }
                }
            });
            return true;
        } else if (PushConstants.SET_APPLICATION_ICON_BADGE_NUMBER.equals(str)) {
            this.f59cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    Log.v(PushPlugin.LOG_TAG, "setApplicationIconBadgeNumber: data=" + jSONArray.toString());
                    try {
                        PushPlugin.setApplicationIconBadgeNumber(PushPlugin.this.getApplicationContext(), jSONArray.getJSONObject(0).getInt(PushConstants.BADGE));
                    } catch (JSONException e) {
                        callbackContext.error(e.getMessage());
                    }
                    callbackContext.success();
                }
            });
            return true;
        } else if (PushConstants.GET_APPLICATION_ICON_BADGE_NUMBER.equals(str)) {
            this.f59cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    Log.v(PushPlugin.LOG_TAG, PushConstants.GET_APPLICATION_ICON_BADGE_NUMBER);
                    callbackContext.success(PushPlugin.getApplicationIconBadgeNumber(PushPlugin.this.getApplicationContext()));
                }
            });
            return true;
        } else if (PushConstants.CLEAR_ALL_NOTIFICATIONS.equals(str)) {
            this.f59cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    Log.v(PushPlugin.LOG_TAG, PushConstants.CLEAR_ALL_NOTIFICATIONS);
                    PushPlugin.this.clearAllNotifications();
                    callbackContext.success();
                }
            });
            return true;
        } else if (PushConstants.SUBSCRIBE.equals(str)) {
            this.f59cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    try {
                        PushPlugin.this.subscribeToTopic(jSONArray.getString(0), PushPlugin.registration_id);
                        callbackContext.success();
                    } catch (JSONException e) {
                        callbackContext.error(e.getMessage());
                    }
                }
            });
            return true;
        } else if (PushConstants.UNSUBSCRIBE.equals(str)) {
            this.f59cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    try {
                        PushPlugin.this.unsubscribeFromTopic(jSONArray.getString(0), PushPlugin.registration_id);
                        callbackContext.success();
                    } catch (JSONException e) {
                        callbackContext.error(e.getMessage());
                    }
                }
            });
            return true;
        } else if (PushConstants.CREATE_CHANNEL.equals(str)) {
            this.f59cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    try {
                        PushPlugin.this.createChannel(jSONArray.getJSONObject(0));
                        callbackContext.success();
                    } catch (JSONException e) {
                        callbackContext.error(e.getMessage());
                    }
                }
            });
            return true;
        } else if (PushConstants.DELETE_CHANNEL.equals(str)) {
            this.f59cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    try {
                        PushPlugin.this.deleteChannel(jSONArray.getString(0));
                        callbackContext.success();
                    } catch (JSONException e) {
                        callbackContext.error(e.getMessage());
                    }
                }
            });
            return true;
        } else if (PushConstants.LIST_CHANNELS.equals(str)) {
            this.f59cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    try {
                        callbackContext.success(PushPlugin.this.listChannels());
                    } catch (JSONException e) {
                        callbackContext.error(e.getMessage());
                    }
                }
            });
            return true;
        } else if (PushConstants.CLEAR_NOTIFICATION.equals(str)) {
            this.f59cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    try {
                        Log.v(PushPlugin.LOG_TAG, PushConstants.CLEAR_NOTIFICATION);
                        PushPlugin.this.clearNotification(jSONArray.getInt(0));
                        callbackContext.success();
                    } catch (JSONException e) {
                        callbackContext.error(e.getMessage());
                    }
                }
            });
            return true;
        } else {
            Log.e(LOG_TAG, "Invalid action : " + str);
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.INVALID_ACTION));
            return false;
        }
    }

    public static void sendEvent(JSONObject jSONObject) {
        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jSONObject);
        pluginResult.setKeepCallback(true);
        CallbackContext callbackContext = pushContext;
        if (callbackContext != null) {
            callbackContext.sendPluginResult(pluginResult);
        }
    }

    public static void sendError(String str) {
        PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, str);
        pluginResult.setKeepCallback(true);
        CallbackContext callbackContext = pushContext;
        if (callbackContext != null) {
            callbackContext.sendPluginResult(pluginResult);
        }
    }

    public static void sendExtras(Bundle bundle) {
        if (bundle != null) {
            String string = bundle.getString(PushConstants.NO_CACHE);
            if (gWebView != null) {
                sendEvent(convertBundleToJson(bundle));
            } else if (!"1".equals(string)) {
                Log.v(LOG_TAG, "sendExtras: caching extras to send at a later time.");
                gCachedExtras.add(bundle);
            }
        }
    }

    public static int getApplicationIconBadgeNumber(Context context) {
        return context.getSharedPreferences(PushConstants.BADGE, 0).getInt(PushConstants.BADGE, 0);
    }

    public static void setApplicationIconBadgeNumber(Context context, int i) {
        if (i > 0) {
            ShortcutBadger.applyCount(context, i);
        } else {
            ShortcutBadger.removeCount(context);
        }
        SharedPreferences.Editor edit = context.getSharedPreferences(PushConstants.BADGE, 0).edit();
        edit.putInt(PushConstants.BADGE, Math.max(i, 0));
        edit.apply();
    }

    public void initialize(CordovaInterface cordovaInterface, CordovaWebView cordovaWebView) {
        super.initialize(cordovaInterface, cordovaWebView);
        gForeground = true;
    }

    public void onPause(boolean z) {
        super.onPause(z);
        gForeground = false;
        if (getApplicationContext().getSharedPreferences(PushConstants.COM_ADOBE_PHONEGAP_PUSH, 0).getBoolean(PushConstants.CLEAR_NOTIFICATIONS, true)) {
            clearAllNotifications();
        }
    }

    public void onResume(boolean z) {
        super.onResume(z);
        gForeground = true;
    }

    public void onDestroy() {
        super.onDestroy();
        gForeground = false;
        gWebView = null;
    }

    /* access modifiers changed from: private */
    public void clearAllNotifications() {
        ((NotificationManager) this.f59cordova.getActivity().getSystemService("notification")).cancelAll();
    }

    /* access modifiers changed from: private */
    public void clearNotification(int i) {
        ((NotificationManager) this.f59cordova.getActivity().getSystemService("notification")).cancel((String) this.f59cordova.getActivity().getPackageManager().getApplicationLabel(this.f59cordova.getActivity().getApplicationInfo()), i);
    }

    /* access modifiers changed from: private */
    public void subscribeToTopics(JSONArray jSONArray, String str) {
        if (jSONArray != null) {
            for (int i = 0; i < jSONArray.length(); i++) {
                subscribeToTopic(jSONArray.optString(i, (String) null), str);
            }
        }
    }

    /* access modifiers changed from: private */
    public void subscribeToTopic(String str, String str2) {
        if (str != null) {
            Log.d(LOG_TAG, "Subscribing to topic: " + str);
            FirebaseMessaging.getInstance().subscribeToTopic(str);
        }
    }

    /* access modifiers changed from: private */
    public void unsubscribeFromTopics(JSONArray jSONArray, String str) {
        if (jSONArray != null) {
            for (int i = 0; i < jSONArray.length(); i++) {
                unsubscribeFromTopic(jSONArray.optString(i, (String) null), str);
            }
        }
    }

    /* access modifiers changed from: private */
    public void unsubscribeFromTopic(String str, String str2) {
        if (str != null) {
            Log.d(LOG_TAG, "Unsubscribing to topic: " + str);
            FirebaseMessaging.getInstance().unsubscribeFromTopic(str);
        }
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(3:29|30|46) */
    /* JADX WARNING: Code restructure failed: missing block: B:30:?, code lost:
        r1.put(r4, r5);
     */
    /* JADX WARNING: Missing exception handler attribute for start block: B:29:0x00c4 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static org.json.JSONObject convertBundleToJson(android.os.Bundle r9) {
        /*
            java.lang.String r0 = "Push_Plugin"
            java.lang.String r1 = "convert extras to json"
            android.util.Log.d(r0, r1)
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ JSONException -> 0x00e9 }
            r0.<init>()     // Catch:{ JSONException -> 0x00e9 }
            org.json.JSONObject r1 = new org.json.JSONObject     // Catch:{ JSONException -> 0x00e9 }
            r1.<init>()     // Catch:{ JSONException -> 0x00e9 }
            java.util.HashSet r2 = new java.util.HashSet     // Catch:{ JSONException -> 0x00e9 }
            r2.<init>()     // Catch:{ JSONException -> 0x00e9 }
            java.lang.String r3 = "title"
            java.lang.String r4 = "message"
            java.lang.String r5 = "count"
            java.lang.String r6 = "sound"
            java.lang.String r7 = "image"
            java.lang.String[] r3 = new java.lang.String[]{r3, r4, r5, r6, r7}     // Catch:{ JSONException -> 0x00e9 }
            java.util.Collections.addAll(r2, r3)     // Catch:{ JSONException -> 0x00e9 }
            java.util.Set r3 = r9.keySet()     // Catch:{ JSONException -> 0x00e9 }
            java.util.Iterator r3 = r3.iterator()     // Catch:{ JSONException -> 0x00e9 }
        L_0x002f:
            boolean r4 = r3.hasNext()     // Catch:{ JSONException -> 0x00e9 }
            if (r4 == 0) goto L_0x00c9
            java.lang.Object r4 = r3.next()     // Catch:{ JSONException -> 0x00e9 }
            java.lang.String r4 = (java.lang.String) r4     // Catch:{ JSONException -> 0x00e9 }
            java.lang.Object r5 = r9.get(r4)     // Catch:{ JSONException -> 0x00e9 }
            java.lang.String r6 = "Push_Plugin"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x00e9 }
            r7.<init>()     // Catch:{ JSONException -> 0x00e9 }
            java.lang.String r8 = "key = "
            r7.append(r8)     // Catch:{ JSONException -> 0x00e9 }
            r7.append(r4)     // Catch:{ JSONException -> 0x00e9 }
            java.lang.String r7 = r7.toString()     // Catch:{ JSONException -> 0x00e9 }
            android.util.Log.d(r6, r7)     // Catch:{ JSONException -> 0x00e9 }
            boolean r6 = r2.contains(r4)     // Catch:{ JSONException -> 0x00e9 }
            if (r6 == 0) goto L_0x005f
            r0.put(r4, r5)     // Catch:{ JSONException -> 0x00e9 }
            goto L_0x002f
        L_0x005f:
            java.lang.String r6 = "coldstart"
            boolean r6 = r4.equals(r6)     // Catch:{ JSONException -> 0x00e9 }
            if (r6 == 0) goto L_0x0071
            java.lang.String r5 = "coldstart"
            boolean r5 = r9.getBoolean(r5)     // Catch:{ JSONException -> 0x00e9 }
            r1.put(r4, r5)     // Catch:{ JSONException -> 0x00e9 }
            goto L_0x002f
        L_0x0071:
            java.lang.String r6 = "foreground"
            boolean r6 = r4.equals(r6)     // Catch:{ JSONException -> 0x00e9 }
            if (r6 == 0) goto L_0x0083
            java.lang.String r5 = "foreground"
            boolean r5 = r9.getBoolean(r5)     // Catch:{ JSONException -> 0x00e9 }
            r1.put(r4, r5)     // Catch:{ JSONException -> 0x00e9 }
            goto L_0x002f
        L_0x0083:
            java.lang.String r6 = "dismissed"
            boolean r6 = r4.equals(r6)     // Catch:{ JSONException -> 0x00e9 }
            if (r6 == 0) goto L_0x0095
            java.lang.String r5 = "dismissed"
            boolean r5 = r9.getBoolean(r5)     // Catch:{ JSONException -> 0x00e9 }
            r1.put(r4, r5)     // Catch:{ JSONException -> 0x00e9 }
            goto L_0x002f
        L_0x0095:
            boolean r6 = r5 instanceof java.lang.String     // Catch:{ JSONException -> 0x00e9 }
            if (r6 == 0) goto L_0x002f
            r6 = r5
            java.lang.String r6 = (java.lang.String) r6     // Catch:{ JSONException -> 0x00e9 }
            java.lang.String r7 = "{"
            boolean r7 = r6.startsWith(r7)     // Catch:{ Exception -> 0x00c4 }
            if (r7 == 0) goto L_0x00ad
            org.json.JSONObject r7 = new org.json.JSONObject     // Catch:{ Exception -> 0x00c4 }
            r7.<init>(r6)     // Catch:{ Exception -> 0x00c4 }
            r1.put(r4, r7)     // Catch:{ Exception -> 0x00c4 }
            goto L_0x002f
        L_0x00ad:
            java.lang.String r7 = "["
            boolean r7 = r6.startsWith(r7)     // Catch:{ Exception -> 0x00c4 }
            if (r7 == 0) goto L_0x00bf
            org.json.JSONArray r7 = new org.json.JSONArray     // Catch:{ Exception -> 0x00c4 }
            r7.<init>(r6)     // Catch:{ Exception -> 0x00c4 }
            r1.put(r4, r7)     // Catch:{ Exception -> 0x00c4 }
            goto L_0x002f
        L_0x00bf:
            r1.put(r4, r5)     // Catch:{ Exception -> 0x00c4 }
            goto L_0x002f
        L_0x00c4:
            r1.put(r4, r5)     // Catch:{ JSONException -> 0x00e9 }
            goto L_0x002f
        L_0x00c9:
            java.lang.String r9 = "additionalData"
            r0.put(r9, r1)     // Catch:{ JSONException -> 0x00e9 }
            java.lang.String r9 = "Push_Plugin"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x00e9 }
            r1.<init>()     // Catch:{ JSONException -> 0x00e9 }
            java.lang.String r2 = "extrasToJSON: "
            r1.append(r2)     // Catch:{ JSONException -> 0x00e9 }
            java.lang.String r2 = r0.toString()     // Catch:{ JSONException -> 0x00e9 }
            r1.append(r2)     // Catch:{ JSONException -> 0x00e9 }
            java.lang.String r1 = r1.toString()     // Catch:{ JSONException -> 0x00e9 }
            android.util.Log.v(r9, r1)     // Catch:{ JSONException -> 0x00e9 }
            return r0
        L_0x00e9:
            java.lang.String r9 = "Push_Plugin"
            java.lang.String r0 = "extrasToJSON: JSON exception"
            android.util.Log.e(r9, r0)
            r9 = 0
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.adobe.phonegap.push.PushPlugin.convertBundleToJson(android.os.Bundle):org.json.JSONObject");
    }

    /* access modifiers changed from: private */
    public String getStringResourceByName(String str) {
        Activity activity = this.f59cordova.getActivity();
        return activity.getString(activity.getResources().getIdentifier(str, "string", activity.getPackageName()));
    }

    public static boolean isInForeground() {
        return gForeground;
    }

    public static boolean isActive() {
        return gWebView != null;
    }

    protected static void setRegistrationID(String str) {
        registration_id = str;
    }
}
