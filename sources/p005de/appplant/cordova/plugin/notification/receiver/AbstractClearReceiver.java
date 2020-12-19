package p005de.appplant.cordova.plugin.notification.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import p005de.appplant.cordova.plugin.notification.Manager;
import p005de.appplant.cordova.plugin.notification.Notification;

/* renamed from: de.appplant.cordova.plugin.notification.receiver.AbstractClearReceiver */
public abstract class AbstractClearReceiver extends BroadcastReceiver {
    public abstract void onClear(Notification notification, Bundle bundle);

    public void onReceive(Context context, Intent intent) {
        Notification notification;
        Bundle extras = intent.getExtras();
        if (extras != null && (notification = Manager.getInstance(context).get(extras.getInt(Notification.EXTRA_ID))) != null) {
            onClear(notification, extras);
        }
    }
}
