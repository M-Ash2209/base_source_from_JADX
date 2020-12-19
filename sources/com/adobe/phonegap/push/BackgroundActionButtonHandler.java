package com.adobe.phonegap.push;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.p000v4.app.RemoteInput;
import android.util.Log;

public class BackgroundActionButtonHandler extends BroadcastReceiver implements PushConstants {
    private static String LOG_TAG = "Push_BGActionButton";

    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        String str = LOG_TAG;
        Log.d(str, "BackgroundActionButtonHandler = " + extras);
        int intExtra = intent.getIntExtra(PushConstants.NOT_ID, 0);
        String str2 = LOG_TAG;
        Log.d(str2, "not id = " + intExtra);
        ((NotificationManager) context.getSystemService("notification")).cancel(FCMService.getAppName(context), intExtra);
        new FCMService().setNotification(intExtra, "");
        if (extras != null) {
            Bundle bundle = extras.getBundle(PushConstants.PUSH_BUNDLE);
            bundle.putBoolean(PushConstants.FOREGROUND, false);
            bundle.putBoolean(PushConstants.COLDSTART, false);
            bundle.putString(PushConstants.ACTION_CALLBACK, extras.getString(PushConstants.CALLBACK));
            Bundle resultsFromIntent = RemoteInput.getResultsFromIntent(intent);
            if (resultsFromIntent != null) {
                String charSequence = resultsFromIntent.getCharSequence(PushConstants.INLINE_REPLY).toString();
                String str3 = LOG_TAG;
                Log.d(str3, "response: " + charSequence);
                bundle.putString(PushConstants.INLINE_REPLY, charSequence);
            }
            PushPlugin.sendExtras(bundle);
        }
    }
}
