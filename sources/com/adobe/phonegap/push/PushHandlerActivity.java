package com.adobe.phonegap.push;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.p000v4.app.RemoteInput;
import android.util.Log;

public class PushHandlerActivity extends Activity implements PushConstants {
    private static String LOG_TAG = "Push_HandlerActivity";

    public void onCreate(Bundle bundle) {
        FCMService fCMService = new FCMService();
        Intent intent = getIntent();
        int i = intent.getExtras().getInt(PushConstants.NOT_ID, 0);
        String str = LOG_TAG;
        Log.d(str, "not id = " + i);
        fCMService.setNotification(i, "");
        super.onCreate(bundle);
        Log.v(LOG_TAG, "onCreate");
        String string = getIntent().getExtras().getString(PushConstants.CALLBACK);
        String str2 = LOG_TAG;
        Log.d(str2, "callback = " + string);
        boolean z = getIntent().getExtras().getBoolean(PushConstants.FOREGROUND, true);
        boolean z2 = getIntent().getExtras().getBoolean(PushConstants.START_IN_BACKGROUND, false);
        boolean z3 = getIntent().getExtras().getBoolean(PushConstants.DISMISSED, false);
        String str3 = LOG_TAG;
        Log.d(str3, "dismissed = " + z3);
        if (!z2) {
            ((NotificationManager) getSystemService("notification")).cancel(FCMService.getAppName(this), i);
        }
        boolean isActive = PushPlugin.isActive();
        boolean processPushBundle = processPushBundle(isActive, intent);
        if (processPushBundle && Build.VERSION.SDK_INT < 24 && !z2) {
            z = true;
        }
        String str4 = LOG_TAG;
        Log.d(str4, "bringToForeground = " + z);
        finish();
        if (!z3) {
            String str5 = LOG_TAG;
            Log.d(str5, "isPushPluginActive = " + isActive);
            if (!isActive && z && processPushBundle) {
                Log.d(LOG_TAG, "forceMainActivityReload");
                forceMainActivityReload(false);
            } else if (z2) {
                Log.d(LOG_TAG, "startOnBackgroundTrue");
                forceMainActivityReload(true);
            } else {
                Log.d(LOG_TAG, "don't want main activity");
            }
        }
    }

    private boolean processPushBundle(boolean z, Intent intent) {
        Bundle bundle;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Bundle bundle2 = extras.getBundle(PushConstants.PUSH_BUNDLE);
            bundle2.putBoolean(PushConstants.FOREGROUND, false);
            bundle2.putBoolean(PushConstants.COLDSTART, !z);
            bundle2.putBoolean(PushConstants.DISMISSED, extras.getBoolean(PushConstants.DISMISSED));
            bundle2.putString(PushConstants.ACTION_CALLBACK, extras.getString(PushConstants.CALLBACK));
            bundle2.remove(PushConstants.NO_CACHE);
            bundle = RemoteInput.getResultsFromIntent(intent);
            if (bundle != null) {
                String charSequence = bundle.getCharSequence(PushConstants.INLINE_REPLY).toString();
                String str = LOG_TAG;
                Log.d(str, "response: " + charSequence);
                bundle2.putString(PushConstants.INLINE_REPLY, charSequence);
            }
            PushPlugin.sendExtras(bundle2);
        } else {
            bundle = null;
        }
        if (bundle == null) {
            return true;
        }
        return false;
    }

    private void forceMainActivityReload(boolean z) {
        Intent launchIntentForPackage = getPackageManager().getLaunchIntentForPackage(getApplicationContext().getPackageName());
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Bundle bundle = extras.getBundle(PushConstants.PUSH_BUNDLE);
            if (bundle != null) {
                launchIntentForPackage.putExtras(bundle);
            }
            launchIntentForPackage.addFlags(67108864);
            launchIntentForPackage.addFlags(4);
            launchIntentForPackage.putExtra(PushConstants.START_IN_BACKGROUND, z);
        }
        startActivity(launchIntentForPackage);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        ((NotificationManager) getSystemService("notification")).cancelAll();
    }
}
