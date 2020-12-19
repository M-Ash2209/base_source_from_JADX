package com.adobe.phonegap.push;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.support.p000v4.app.NotificationCompat;
import android.support.p000v4.app.RemoteInput;
import android.support.p000v4.internal.view.SupportMenu;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint({"NewApi"})
public class FCMService extends FirebaseMessagingService implements PushConstants {
    private static final String LOG_TAG = "Push_FCMService";
    private static HashMap<Integer, ArrayList<String>> messageMap = new HashMap<>();

    public void setNotification(int i, String str) {
        ArrayList arrayList = messageMap.get(Integer.valueOf(i));
        if (arrayList == null) {
            arrayList = new ArrayList();
            messageMap.put(Integer.valueOf(i), arrayList);
        }
        if (str.isEmpty()) {
            arrayList.clear();
        } else {
            arrayList.add(str);
        }
    }

    public void onMessageReceived(RemoteMessage remoteMessage) {
        String from = remoteMessage.getFrom();
        Log.d(LOG_TAG, "onMessage - from: " + from);
        Bundle bundle = new Bundle();
        if (remoteMessage.getNotification() != null) {
            bundle.putString(PushConstants.TITLE, remoteMessage.getNotification().getTitle());
            bundle.putString(PushConstants.MESSAGE, remoteMessage.getNotification().getBody());
            bundle.putString(PushConstants.SOUND, remoteMessage.getNotification().getSound());
            bundle.putString(PushConstants.ICON, remoteMessage.getNotification().getIcon());
            bundle.putString(PushConstants.COLOR, remoteMessage.getNotification().getColor());
        }
        for (Map.Entry next : remoteMessage.getData().entrySet()) {
            bundle.putString((String) next.getKey(), (String) next.getValue());
        }
        if (isAvailableSender(from)) {
            Context applicationContext = getApplicationContext();
            SharedPreferences sharedPreferences = applicationContext.getSharedPreferences(PushConstants.COM_ADOBE_PHONEGAP_PUSH, 0);
            boolean z = sharedPreferences.getBoolean(PushConstants.FORCE_SHOW, false);
            boolean z2 = sharedPreferences.getBoolean(PushConstants.CLEAR_BADGE, false);
            Bundle normalizeExtras = normalizeExtras(applicationContext, bundle, sharedPreferences.getString(PushConstants.MESSAGE_KEY, PushConstants.MESSAGE), sharedPreferences.getString(PushConstants.TITLE_KEY, PushConstants.TITLE));
            if (z2) {
                PushPlugin.setApplicationIconBadgeNumber(getApplicationContext(), 0);
            }
            if (!z && PushPlugin.isInForeground()) {
                Log.d(LOG_TAG, PushConstants.FOREGROUND);
                normalizeExtras.putBoolean(PushConstants.FOREGROUND, true);
                normalizeExtras.putBoolean(PushConstants.COLDSTART, false);
                PushPlugin.sendExtras(normalizeExtras);
            } else if (!z || !PushPlugin.isInForeground()) {
                Log.d(LOG_TAG, "background");
                normalizeExtras.putBoolean(PushConstants.FOREGROUND, false);
                normalizeExtras.putBoolean(PushConstants.COLDSTART, PushPlugin.isActive());
                showNotificationIfPossible(applicationContext, normalizeExtras);
            } else {
                Log.d(LOG_TAG, "foreground force");
                normalizeExtras.putBoolean(PushConstants.FOREGROUND, true);
                normalizeExtras.putBoolean(PushConstants.COLDSTART, false);
                showNotificationIfPossible(applicationContext, normalizeExtras);
            }
        }
    }

    private void replaceKey(Context context, String str, String str2, Bundle bundle, Bundle bundle2) {
        Object obj = bundle.get(str);
        if (obj == null) {
            return;
        }
        if (obj instanceof String) {
            bundle2.putString(str2, localizeKey(context, str2, (String) obj));
        } else if (obj instanceof Boolean) {
            bundle2.putBoolean(str2, ((Boolean) obj).booleanValue());
        } else if (obj instanceof Number) {
            bundle2.putDouble(str2, ((Number) obj).doubleValue());
        } else {
            bundle2.putString(str2, String.valueOf(obj));
        }
    }

    private String localizeKey(Context context, String str, String str2) {
        if (!str.equals(PushConstants.TITLE) && !str.equals(PushConstants.MESSAGE) && !str.equals(PushConstants.SUMMARY_TEXT)) {
            return str2;
        }
        try {
            JSONObject jSONObject = new JSONObject(str2);
            String string = jSONObject.getString(PushConstants.LOC_KEY);
            ArrayList arrayList = new ArrayList();
            if (!jSONObject.isNull(PushConstants.LOC_DATA)) {
                JSONArray jSONArray = new JSONArray(jSONObject.getString(PushConstants.LOC_DATA));
                for (int i = 0; i < jSONArray.length(); i++) {
                    arrayList.add(jSONArray.getString(i));
                }
            }
            String packageName = context.getPackageName();
            Resources resources = context.getResources();
            int identifier = resources.getIdentifier(string, "string", packageName);
            if (identifier != 0) {
                return resources.getString(identifier, arrayList.toArray());
            }
            Log.d(LOG_TAG, "can't find resource for locale key = " + string);
            return str2;
        } catch (JSONException e) {
            Log.d(LOG_TAG, "no locale found for key = " + str + ", error " + e.getMessage());
            return str2;
        }
    }

    private String normalizeKey(String str, String str2, String str3, Bundle bundle) {
        if (str.equals(PushConstants.BODY) || str.equals(PushConstants.ALERT) || str.equals(PushConstants.MP_MESSAGE) || str.equals(PushConstants.GCM_NOTIFICATION_BODY) || str.equals(PushConstants.TWILIO_BODY) || str.equals(str2) || str.equals(PushConstants.AWS_PINPOINT_BODY)) {
            return PushConstants.MESSAGE;
        }
        if (str.equals(PushConstants.TWILIO_TITLE) || str.equals(PushConstants.SUBJECT) || str.equals(str3)) {
            return PushConstants.TITLE;
        }
        if (str.equals(PushConstants.MSGCNT) || str.equals(PushConstants.BADGE)) {
            return "count";
        }
        if (str.equals(PushConstants.SOUNDNAME) || str.equals(PushConstants.TWILIO_SOUND)) {
            return PushConstants.SOUND;
        }
        if (str.equals(PushConstants.AWS_PINPOINT_PICTURE)) {
            bundle.putString(PushConstants.STYLE, "picture");
            return "picture";
        } else if (str.startsWith(PushConstants.GCM_NOTIFICATION)) {
            return str.substring(17, str.length());
        } else {
            if (str.startsWith(PushConstants.GCM_N)) {
                return str.substring(7, str.length());
            }
            if (str.startsWith(PushConstants.UA_PREFIX)) {
                return str.substring(22, str.length()).toLowerCase();
            }
            return str.startsWith(PushConstants.AWS_PINPOINT_PREFIX) ? str.substring(22, str.length()) : str;
        }
    }

    private Bundle normalizeExtras(Context context, Bundle bundle, String str, String str2) {
        Log.d(LOG_TAG, "normalize extras");
        Bundle bundle2 = new Bundle();
        for (String str3 : bundle.keySet()) {
            Log.d(LOG_TAG, "key = " + str3);
            if (str3.equals(PushConstants.PARSE_COM_DATA) || str3.equals(PushConstants.MESSAGE) || str3.equals(str)) {
                Object obj = bundle.get(str3);
                if (!(obj instanceof String) || !((String) obj).startsWith("{")) {
                    String normalizeKey = normalizeKey(str3, str, str2, bundle2);
                    Log.d(LOG_TAG, "replace key " + str3 + " with " + normalizeKey);
                    replaceKey(context, str3, normalizeKey, bundle, bundle2);
                } else {
                    Log.d(LOG_TAG, "extracting nested message data from key = " + str3);
                    try {
                        JSONObject jSONObject = new JSONObject((String) obj);
                        if (!jSONObject.has(PushConstants.ALERT) && !jSONObject.has(PushConstants.MESSAGE) && !jSONObject.has(PushConstants.BODY) && !jSONObject.has(PushConstants.TITLE) && !jSONObject.has(str)) {
                            if (!jSONObject.has(str2)) {
                                if (jSONObject.has(PushConstants.LOC_KEY) || jSONObject.has(PushConstants.LOC_DATA)) {
                                    String normalizeKey2 = normalizeKey(str3, str, str2, bundle2);
                                    Log.d(LOG_TAG, "replace key " + str3 + " with " + normalizeKey2);
                                    replaceKey(context, str3, normalizeKey2, bundle, bundle2);
                                }
                            }
                        }
                        Iterator<String> keys = jSONObject.keys();
                        while (keys.hasNext()) {
                            String next = keys.next();
                            Log.d(LOG_TAG, "key = data/" + next);
                            String string = jSONObject.getString(next);
                            String normalizeKey3 = normalizeKey(next, str, str2, bundle2);
                            bundle2.putString(normalizeKey3, localizeKey(context, normalizeKey3, string));
                        }
                    } catch (JSONException unused) {
                        Log.e(LOG_TAG, "normalizeExtras: JSON exception");
                    }
                }
            } else if (str3.equals("notification")) {
                Bundle bundle3 = bundle.getBundle(str3);
                for (String str4 : bundle3.keySet()) {
                    Log.d(LOG_TAG, "notifkey = " + str4);
                    String normalizeKey4 = normalizeKey(str4, str, str2, bundle2);
                    Log.d(LOG_TAG, "replace key " + str4 + " with " + normalizeKey4);
                    bundle2.putString(normalizeKey4, localizeKey(context, normalizeKey4, bundle3.getString(str4)));
                }
            } else {
                String normalizeKey5 = normalizeKey(str3, str, str2, bundle2);
                Log.d(LOG_TAG, "replace key " + str3 + " with " + normalizeKey5);
                replaceKey(context, str3, normalizeKey5, bundle, bundle2);
            }
        }
        return bundle2;
    }

    private int extractBadgeCount(Bundle bundle) {
        String string = bundle.getString("count");
        if (string == null) {
            return -1;
        }
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            Log.e(LOG_TAG, e.getLocalizedMessage(), e);
            return -1;
        }
    }

    private void showNotificationIfPossible(Context context, Bundle bundle) {
        String string = bundle.getString(PushConstants.MESSAGE);
        String string2 = bundle.getString(PushConstants.TITLE);
        String string3 = bundle.getString(PushConstants.CONTENT_AVAILABLE);
        String string4 = bundle.getString(PushConstants.FORCE_START);
        int extractBadgeCount = extractBadgeCount(bundle);
        if (extractBadgeCount >= 0) {
            Log.d(LOG_TAG, "count =[" + extractBadgeCount + "]");
            PushPlugin.setApplicationIconBadgeNumber(context, extractBadgeCount);
        }
        if (extractBadgeCount == 0) {
            ((NotificationManager) getSystemService("notification")).cancelAll();
        }
        Log.d(LOG_TAG, "message =[" + string + "]");
        Log.d(LOG_TAG, "title =[" + string2 + "]");
        Log.d(LOG_TAG, "contentAvailable =[" + string3 + "]");
        Log.d(LOG_TAG, "forceStart =[" + string4 + "]");
        if (!((string == null || string.length() == 0) && (string2 == null || string2.length() == 0))) {
            Log.d(LOG_TAG, "create notification");
            if (string2 == null || string2.isEmpty()) {
                bundle.putString(PushConstants.TITLE, getAppName(this));
            }
            createNotification(context, bundle);
        }
        if (!PushPlugin.isActive() && "1".equals(string4)) {
            Log.d(LOG_TAG, "app is not running but we should start it and put in background");
            Intent intent = new Intent(this, PushHandlerActivity.class);
            intent.addFlags(268435456);
            intent.putExtra(PushConstants.PUSH_BUNDLE, bundle);
            intent.putExtra(PushConstants.START_IN_BACKGROUND, true);
            intent.putExtra(PushConstants.FOREGROUND, false);
            startActivity(intent);
        } else if ("1".equals(string3)) {
            Log.d(LOG_TAG, "app is not running and content available true");
            Log.d(LOG_TAG, "send notification event");
            PushPlugin.sendExtras(bundle);
        }
    }

    public void createNotification(Context context, Bundle bundle) {
        NotificationCompat.Builder builder;
        NotificationCompat.Builder builder2;
        String str;
        Context context2 = context;
        Bundle bundle2 = bundle;
        NotificationManager notificationManager = (NotificationManager) getSystemService("notification");
        String appName = getAppName(this);
        String packageName = context.getPackageName();
        Resources resources = context.getResources();
        int parseInt = parseInt(PushConstants.NOT_ID, bundle2);
        Intent intent = new Intent(this, PushHandlerActivity.class);
        intent.addFlags(603979776);
        intent.putExtra(PushConstants.PUSH_BUNDLE, bundle2);
        intent.putExtra(PushConstants.NOT_ID, parseInt);
        SecureRandom secureRandom = new SecureRandom();
        PendingIntent activity = PendingIntent.getActivity(this, secureRandom.nextInt(), intent, 134217728);
        Intent intent2 = new Intent(this, PushDismissedHandler.class);
        intent2.putExtra(PushConstants.PUSH_BUNDLE, bundle2);
        intent2.putExtra(PushConstants.NOT_ID, parseInt);
        intent2.putExtra(PushConstants.DISMISSED, true);
        intent2.setAction(PushConstants.PUSH_DISMISSED);
        PendingIntent broadcast = PendingIntent.getBroadcast(this, secureRandom.nextInt(), intent2, 268435456);
        if (Build.VERSION.SDK_INT >= 26) {
            String string = bundle2.getString(PushConstants.ANDROID_CHANNEL_ID);
            if (string != null) {
                builder2 = new NotificationCompat.Builder(context2, string);
            } else {
                List<NotificationChannel> notificationChannels = notificationManager.getNotificationChannels();
                if (notificationChannels.size() == 1) {
                    str = notificationChannels.get(0).getId();
                } else {
                    str = bundle2.getString(PushConstants.ANDROID_CHANNEL_ID, PushConstants.DEFAULT_CHANNEL_ID);
                }
                Log.d(LOG_TAG, "Using channel ID = " + str);
                builder2 = new NotificationCompat.Builder(context2, str);
            }
            builder = builder2;
        } else {
            builder = new NotificationCompat.Builder(context2);
        }
        builder.setWhen(System.currentTimeMillis()).setContentTitle(fromHtml(bundle2.getString(PushConstants.TITLE))).setTicker(fromHtml(bundle2.getString(PushConstants.TITLE))).setContentIntent(activity).setDeleteIntent(broadcast).setAutoCancel(true);
        SharedPreferences sharedPreferences = context2.getSharedPreferences(PushConstants.COM_ADOBE_PHONEGAP_PUSH, 0);
        String string2 = sharedPreferences.getString(PushConstants.ICON, (String) null);
        String string3 = sharedPreferences.getString(PushConstants.ICON_COLOR, (String) null);
        boolean z = sharedPreferences.getBoolean(PushConstants.SOUND, true);
        boolean z2 = sharedPreferences.getBoolean(PushConstants.VIBRATE, true);
        Log.d(LOG_TAG, "stored icon=" + string2);
        Log.d(LOG_TAG, "stored iconColor=" + string3);
        Log.d(LOG_TAG, "stored sound=" + z);
        Log.d(LOG_TAG, "stored vibrate=" + z2);
        setNotificationVibration(bundle2, Boolean.valueOf(z2), builder);
        setNotificationIconColor(bundle2.getString(PushConstants.COLOR), builder, string3);
        boolean z3 = z;
        setNotificationSmallIcon(context, bundle, packageName, resources, builder, string2);
        setNotificationLargeIcon(bundle2, packageName, resources, builder);
        if (z3) {
            setNotificationSound(context2, bundle2, builder);
        }
        setNotificationLedColor(bundle2, builder);
        setNotificationPriority(bundle2, builder);
        setNotificationMessage(parseInt, bundle2, builder);
        setNotificationCount(context2, bundle2, builder);
        setNotificationOngoing(bundle2, builder);
        setVisibility(context2, bundle2, builder);
        createActions(bundle, builder, resources, packageName, parseInt);
        notificationManager.notify(appName, parseInt, builder.build());
    }

    private void updateIntent(Intent intent, String str, Bundle bundle, boolean z, int i) {
        intent.putExtra(PushConstants.CALLBACK, str);
        intent.putExtra(PushConstants.PUSH_BUNDLE, bundle);
        intent.putExtra(PushConstants.FOREGROUND, z);
        intent.putExtra(PushConstants.NOT_ID, i);
    }

    private void createActions(Bundle bundle, NotificationCompat.Builder builder, Resources resources, String str, int i) {
        JSONObject jSONObject;
        PendingIntent pendingIntent;
        Intent intent;
        NotificationCompat.Builder builder2 = builder;
        Resources resources2 = resources;
        String str2 = str;
        int i2 = i;
        Log.d(LOG_TAG, "create actions: with in-line");
        String string = bundle.getString(PushConstants.ACTIONS);
        if (string != null) {
            try {
                JSONArray jSONArray = new JSONArray(string);
                ArrayList arrayList = new ArrayList();
                boolean z = false;
                int i3 = 0;
                while (i3 < jSONArray.length()) {
                    int nextInt = new SecureRandom().nextInt(2000000000) + 1;
                    Log.d(LOG_TAG, "adding action");
                    JSONObject jSONObject2 = jSONArray.getJSONObject(i3);
                    Log.d(LOG_TAG, "adding callback = " + jSONObject2.getString(PushConstants.CALLBACK));
                    boolean optBoolean = jSONObject2.optBoolean(PushConstants.FOREGROUND, true);
                    boolean optBoolean2 = jSONObject2.optBoolean("inline", z);
                    if (optBoolean2) {
                        Log.d(LOG_TAG, "Version: " + Build.VERSION.SDK_INT + " = " + 23);
                        if (Build.VERSION.SDK_INT <= 23) {
                            Log.d(LOG_TAG, "push activity");
                            intent = new Intent(this, PushHandlerActivity.class);
                        } else {
                            Log.d(LOG_TAG, "push receiver");
                            intent = new Intent(this, BackgroundActionButtonHandler.class);
                        }
                        Intent intent2 = intent;
                        JSONObject jSONObject3 = jSONObject2;
                        int i4 = nextInt;
                        updateIntent(intent, jSONObject2.getString(PushConstants.CALLBACK), bundle, optBoolean, i);
                        if (Build.VERSION.SDK_INT <= 23) {
                            Log.d(LOG_TAG, "push activity for notId " + i2);
                            pendingIntent = PendingIntent.getActivity(this, i4, intent2, 1073741824);
                            jSONObject = jSONObject3;
                        } else {
                            Log.d(LOG_TAG, "push receiver for notId " + i2);
                            pendingIntent = PendingIntent.getBroadcast(this, i4, intent2, 1073741824);
                            jSONObject = jSONObject3;
                        }
                    } else {
                        JSONObject jSONObject4 = jSONObject2;
                        int i5 = nextInt;
                        if (optBoolean) {
                            Intent intent3 = new Intent(this, PushHandlerActivity.class);
                            JSONObject jSONObject5 = jSONObject4;
                            updateIntent(intent3, jSONObject5.getString(PushConstants.CALLBACK), bundle, optBoolean, i);
                            pendingIntent = PendingIntent.getActivity(this, i5, intent3, 134217728);
                            jSONObject = jSONObject5;
                        } else {
                            Intent intent4 = new Intent(this, BackgroundActionButtonHandler.class);
                            JSONObject jSONObject6 = jSONObject4;
                            jSONObject = jSONObject6;
                            updateIntent(intent4, jSONObject6.getString(PushConstants.CALLBACK), bundle, optBoolean, i);
                            pendingIntent = PendingIntent.getBroadcast(this, i5, intent4, 134217728);
                        }
                    }
                    NotificationCompat.Action.Builder builder3 = new NotificationCompat.Action.Builder(getImageId(resources2, jSONObject.optString(PushConstants.ICON, ""), str2), jSONObject.getString(PushConstants.TITLE), pendingIntent);
                    if (optBoolean2) {
                        Log.d(LOG_TAG, "create remote input");
                        builder3.addRemoteInput(new RemoteInput.Builder(PushConstants.INLINE_REPLY).setLabel(jSONObject.optString(PushConstants.INLINE_REPLY_LABEL, "Enter your reply here")).build());
                    }
                    NotificationCompat.Action build = builder3.build();
                    arrayList.add(builder3.build());
                    if (optBoolean2) {
                        builder2.addAction(build);
                    } else {
                        builder2.addAction(getImageId(resources2, jSONObject.optString(PushConstants.ICON, ""), str2), jSONObject.getString(PushConstants.TITLE), pendingIntent);
                    }
                    i3++;
                    i2 = i;
                    Bundle bundle2 = bundle;
                    z = false;
                }
                builder2.extend(new NotificationCompat.WearableExtender().addActions(arrayList));
                arrayList.clear();
            } catch (JSONException unused) {
            }
        }
    }

    private void setNotificationCount(Context context, Bundle bundle, NotificationCompat.Builder builder) {
        int extractBadgeCount = extractBadgeCount(bundle);
        if (extractBadgeCount >= 0) {
            Log.d(LOG_TAG, "count =[" + extractBadgeCount + "]");
            builder.setNumber(extractBadgeCount);
        }
    }

    private void setVisibility(Context context, Bundle bundle, NotificationCompat.Builder builder) {
        String string = bundle.getString(PushConstants.VISIBILITY);
        if (string != null) {
            try {
                Integer valueOf = Integer.valueOf(Integer.parseInt(string));
                if (valueOf.intValue() < -1 || valueOf.intValue() > 1) {
                    Log.e(LOG_TAG, "Visibility parameter must be between -1 and 1");
                } else {
                    builder.setVisibility(valueOf.intValue());
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    private void setNotificationVibration(Bundle bundle, Boolean bool, NotificationCompat.Builder builder) {
        String string = bundle.getString(PushConstants.VIBRATION_PATTERN);
        if (string != null) {
            String[] split = string.replaceAll("\\[", "").replaceAll("\\]", "").split(",");
            long[] jArr = new long[split.length];
            for (int i = 0; i < split.length; i++) {
                try {
                    jArr[i] = Long.parseLong(split[i].trim());
                } catch (NumberFormatException unused) {
                }
            }
            builder.setVibrate(jArr);
        } else if (bool.booleanValue()) {
            builder.setDefaults(2);
        }
    }

    private void setNotificationOngoing(Bundle bundle, NotificationCompat.Builder builder) {
        builder.setOngoing(Boolean.parseBoolean(bundle.getString(PushConstants.ONGOING, "false")));
    }

    private void setNotificationMessage(int i, Bundle bundle, NotificationCompat.Builder builder) {
        NotificationCompat.MessagingStyle messagingStyle;
        String string = bundle.getString(PushConstants.MESSAGE);
        String string2 = bundle.getString(PushConstants.STYLE, PushConstants.STYLE_TEXT);
        if (PushConstants.STYLE_MESSAGING.equals(string2)) {
            String string3 = bundle.getString(PushConstants.TITLE);
            Notification findActiveNotification = findActiveNotification(Integer.valueOf(i));
            if (findActiveNotification != null) {
                messagingStyle = NotificationCompat.MessagingStyle.extractMessagingStyleFromNotification(findActiveNotification);
            } else {
                messagingStyle = new NotificationCompat.MessagingStyle("");
            }
            messagingStyle.addMessage(string, System.currentTimeMillis(), bundle.getString(PushConstants.SENDER, ""));
            Integer valueOf = Integer.valueOf(messagingStyle.getMessages().size());
            if (valueOf.intValue() > 1) {
                String str = "(" + valueOf + ")";
                if (bundle.getString(PushConstants.SUMMARY_TEXT) != null) {
                    str = bundle.getString(PushConstants.SUMMARY_TEXT).replace("%n%", "" + valueOf);
                }
                if (!str.trim().equals("")) {
                    string3 = string3 + " " + str;
                }
            }
            messagingStyle.setConversationTitle(string3);
            builder.setStyle(messagingStyle);
        } else if (PushConstants.STYLE_INBOX.equals(string2)) {
            setNotification(i, string);
            builder.setContentText(fromHtml(string));
            ArrayList arrayList = messageMap.get(Integer.valueOf(i));
            Integer valueOf2 = Integer.valueOf(arrayList.size());
            if (valueOf2.intValue() > 1) {
                String num = valueOf2.toString();
                String str2 = valueOf2 + " more";
                if (bundle.getString(PushConstants.SUMMARY_TEXT) != null) {
                    str2 = bundle.getString(PushConstants.SUMMARY_TEXT).replace("%n%", num);
                }
                NotificationCompat.InboxStyle summaryText = new NotificationCompat.InboxStyle().setBigContentTitle(fromHtml(bundle.getString(PushConstants.TITLE))).setSummaryText(fromHtml(str2));
                if (PushConstants.ORDER_ASC.equals(bundle.getString(PushConstants.INBOX_ORDER, PushConstants.ORDER_DESC))) {
                    for (int max = Math.max(0, arrayList.size() - 4); max < arrayList.size(); max++) {
                        summaryText.addLine(fromHtml((String) arrayList.get(max)));
                    }
                } else {
                    for (int size = arrayList.size() - 1; size >= 0; size--) {
                        summaryText.addLine(fromHtml((String) arrayList.get(size)));
                    }
                }
                builder.setStyle(summaryText);
                return;
            }
            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            if (string != null) {
                bigTextStyle.bigText(fromHtml(string));
                bigTextStyle.setBigContentTitle(fromHtml(bundle.getString(PushConstants.TITLE)));
                builder.setStyle(bigTextStyle);
            }
        } else if ("picture".equals(string2)) {
            setNotification(i, "");
            NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
            bigPictureStyle.bigPicture(getBitmapFromURL(bundle.getString("picture")));
            bigPictureStyle.setBigContentTitle(fromHtml(bundle.getString(PushConstants.TITLE)));
            bigPictureStyle.setSummaryText(fromHtml(bundle.getString(PushConstants.SUMMARY_TEXT)));
            builder.setContentTitle(fromHtml(bundle.getString(PushConstants.TITLE)));
            builder.setContentText(fromHtml(string));
            builder.setStyle(bigPictureStyle);
        } else {
            setNotification(i, "");
            NotificationCompat.BigTextStyle bigTextStyle2 = new NotificationCompat.BigTextStyle();
            if (string != null) {
                builder.setContentText(fromHtml(string));
                bigTextStyle2.bigText(fromHtml(string));
                bigTextStyle2.setBigContentTitle(fromHtml(bundle.getString(PushConstants.TITLE)));
                String string4 = bundle.getString(PushConstants.SUMMARY_TEXT);
                if (string4 != null) {
                    bigTextStyle2.setSummaryText(fromHtml(string4));
                }
                builder.setStyle(bigTextStyle2);
            }
        }
    }

    private Notification findActiveNotification(Integer num) {
        if (Build.VERSION.SDK_INT < 23) {
            return null;
        }
        StatusBarNotification[] activeNotifications = ((NotificationManager) getSystemService("notification")).getActiveNotifications();
        for (int i = 0; i < activeNotifications.length; i++) {
            if (activeNotifications[i].getId() == num.intValue()) {
                return activeNotifications[i].getNotification();
            }
        }
        return null;
    }

    private void setNotificationSound(Context context, Bundle bundle, NotificationCompat.Builder builder) {
        String string = bundle.getString(PushConstants.SOUNDNAME);
        if (string == null) {
            string = bundle.getString(PushConstants.SOUND);
        }
        if (PushConstants.SOUND_RINGTONE.equals(string)) {
            builder.setSound(Settings.System.DEFAULT_RINGTONE_URI);
        } else if (string == null || string.contentEquals(PushConstants.SOUND_DEFAULT)) {
            builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        } else {
            Uri parse = Uri.parse("android.resource://" + context.getPackageName() + "/raw/" + string);
            Log.d(LOG_TAG, parse.toString());
            builder.setSound(parse);
        }
    }

    private void setNotificationLedColor(Bundle bundle, NotificationCompat.Builder builder) {
        String string = bundle.getString(PushConstants.LED_COLOR);
        if (string != null) {
            String[] split = string.replaceAll("\\[", "").replaceAll("\\]", "").split(",");
            int[] iArr = new int[split.length];
            for (int i = 0; i < split.length; i++) {
                try {
                    iArr[i] = Integer.parseInt(split[i].trim());
                } catch (NumberFormatException unused) {
                }
            }
            if (iArr.length == 4) {
                builder.setLights(Color.argb(iArr[0], iArr[1], iArr[2], iArr[3]), 500, 500);
            } else {
                Log.e(LOG_TAG, "ledColor parameter must be an array of length == 4 (ARGB)");
            }
        }
    }

    private void setNotificationPriority(Bundle bundle, NotificationCompat.Builder builder) {
        String string = bundle.getString(PushConstants.PRIORITY);
        if (string != null) {
            try {
                Integer valueOf = Integer.valueOf(Integer.parseInt(string));
                if (valueOf.intValue() < -2 || valueOf.intValue() > 2) {
                    Log.e(LOG_TAG, "Priority parameter must be between -2 and 2");
                } else {
                    builder.setPriority(valueOf.intValue());
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap getCircleBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(SupportMenu.CATEGORY_MASK);
        float width = (float) (bitmap.getWidth() / 2);
        float height = (float) (bitmap.getHeight() / 2);
        canvas.drawCircle(width, height, width < height ? width : height, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        bitmap.recycle();
        return createBitmap;
    }

    private void setNotificationLargeIcon(Bundle bundle, String str, Resources resources, NotificationCompat.Builder builder) {
        String string = bundle.getString(PushConstants.IMAGE);
        String string2 = bundle.getString(PushConstants.IMAGE_TYPE, PushConstants.IMAGE_TYPE_SQUARE);
        if (string != null && !"".equals(string)) {
            if (string.startsWith("http://") || string.startsWith("https://")) {
                Bitmap bitmapFromURL = getBitmapFromURL(string);
                if (PushConstants.IMAGE_TYPE_SQUARE.equalsIgnoreCase(string2)) {
                    builder.setLargeIcon(bitmapFromURL);
                } else {
                    builder.setLargeIcon(getCircleBitmap(bitmapFromURL));
                }
                Log.d(LOG_TAG, "using remote large-icon from gcm");
                return;
            }
            try {
                Bitmap decodeStream = BitmapFactory.decodeStream(getAssets().open(string));
                if (PushConstants.IMAGE_TYPE_SQUARE.equalsIgnoreCase(string2)) {
                    builder.setLargeIcon(decodeStream);
                } else {
                    builder.setLargeIcon(getCircleBitmap(decodeStream));
                }
                Log.d(LOG_TAG, "using assets large-icon from gcm");
            } catch (IOException unused) {
                int imageId = getImageId(resources, string, str);
                if (imageId != 0) {
                    builder.setLargeIcon(BitmapFactory.decodeResource(resources, imageId));
                    Log.d(LOG_TAG, "using resources large-icon from gcm");
                    return;
                }
                Log.d(LOG_TAG, "Not setting large icon");
            }
        }
    }

    private int getImageId(Resources resources, String str, String str2) {
        int identifier = resources.getIdentifier(str, PushConstants.DRAWABLE, str2);
        return identifier == 0 ? resources.getIdentifier(str, "mipmap", str2) : identifier;
    }

    private void setNotificationSmallIcon(Context context, Bundle bundle, String str, Resources resources, NotificationCompat.Builder builder, String str2) {
        int i;
        String string = bundle.getString(PushConstants.ICON);
        if (string != null && !"".equals(string)) {
            i = getImageId(resources, string, str);
            Log.d(LOG_TAG, "using icon from plugin options");
        } else if (str2 == null || "".equals(str2)) {
            i = 0;
        } else {
            i = getImageId(resources, str2, str);
            Log.d(LOG_TAG, "using icon from plugin options");
        }
        if (i == 0) {
            Log.d(LOG_TAG, "no icon resource found - using application icon");
            i = context.getApplicationInfo().icon;
        }
        builder.setSmallIcon(i);
    }

    /* JADX WARNING: Removed duplicated region for block: B:16:0x0030  */
    /* JADX WARNING: Removed duplicated region for block: B:18:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setNotificationIconColor(java.lang.String r2, android.support.p000v4.app.NotificationCompat.Builder r3, java.lang.String r4) {
        /*
            r1 = this;
            if (r2 == 0) goto L_0x0017
            java.lang.String r0 = ""
            boolean r0 = r0.equals(r2)
            if (r0 != 0) goto L_0x0017
            int r2 = android.graphics.Color.parseColor(r2)     // Catch:{ IllegalArgumentException -> 0x000f }
            goto L_0x002e
        L_0x000f:
            java.lang.String r2 = "Push_FCMService"
            java.lang.String r4 = "couldn't parse color from android options"
            android.util.Log.e(r2, r4)
            goto L_0x002d
        L_0x0017:
            if (r4 == 0) goto L_0x002d
            java.lang.String r2 = ""
            boolean r2 = r2.equals(r4)
            if (r2 != 0) goto L_0x002d
            int r2 = android.graphics.Color.parseColor(r4)     // Catch:{ IllegalArgumentException -> 0x0026 }
            goto L_0x002e
        L_0x0026:
            java.lang.String r2 = "Push_FCMService"
            java.lang.String r4 = "couldn't parse color from android options"
            android.util.Log.e(r2, r4)
        L_0x002d:
            r2 = 0
        L_0x002e:
            if (r2 == 0) goto L_0x0033
            r3.setColor(r2)
        L_0x0033:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.adobe.phonegap.push.FCMService.setNotificationIconColor(java.lang.String, android.support.v4.app.NotificationCompat$Builder, java.lang.String):void");
    }

    public Bitmap getBitmapFromURL(String str) {
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();
            return BitmapFactory.decodeStream(httpURLConnection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getAppName(Context context) {
        return (String) context.getPackageManager().getApplicationLabel(context.getApplicationInfo());
    }

    private int parseInt(String str, Bundle bundle) {
        try {
            return Integer.parseInt(bundle.getString(str));
        } catch (NumberFormatException e) {
            Log.e(LOG_TAG, "Number format exception - Error parsing " + str + ": " + e.getMessage());
            return 0;
        } catch (Exception e2) {
            Log.e(LOG_TAG, "Number format exception - Error parsing " + str + ": " + e2.getMessage());
            return 0;
        }
    }

    private Spanned fromHtml(String str) {
        if (str != null) {
            return Html.fromHtml(str);
        }
        return null;
    }

    private boolean isAvailableSender(String str) {
        String string = getApplicationContext().getSharedPreferences(PushConstants.COM_ADOBE_PHONEGAP_PUSH, 0).getString(PushConstants.SENDER_ID, "");
        Log.d(LOG_TAG, "sender id = " + string);
        if (str.equals(string) || str.startsWith("/topics/")) {
            return true;
        }
        return false;
    }
}
