package com.adobe.phonegap.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class PushDismissedHandler extends BroadcastReceiver implements PushConstants {
    private static String LOG_TAG = "Push_DismissedHandler";

    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        FCMService fCMService = new FCMService();
        String action = intent.getAction();
        int intExtra = intent.getIntExtra(PushConstants.NOT_ID, 0);
        if (action.equals(PushConstants.PUSH_DISMISSED)) {
            String str = LOG_TAG;
            Log.d(str, "PushDismissedHandler = " + extras);
            String str2 = LOG_TAG;
            Log.d(str2, "not id = " + intExtra);
            fCMService.setNotification(intExtra, "");
        }
    }
}
