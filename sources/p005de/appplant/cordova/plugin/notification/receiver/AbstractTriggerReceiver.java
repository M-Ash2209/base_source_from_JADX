package p005de.appplant.cordova.plugin.notification.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import p005de.appplant.cordova.plugin.notification.Builder;
import p005de.appplant.cordova.plugin.notification.Manager;
import p005de.appplant.cordova.plugin.notification.Notification;
import p005de.appplant.cordova.plugin.notification.Options;

/* renamed from: de.appplant.cordova.plugin.notification.receiver.AbstractTriggerReceiver */
public abstract class AbstractTriggerReceiver extends BroadcastReceiver {
    public abstract Notification buildNotification(Builder builder, Bundle bundle);

    public abstract void onTrigger(Notification notification, Bundle bundle);

    public void onReceive(Context context, Intent intent) {
        Options options;
        Notification buildNotification;
        Bundle extras = intent.getExtras();
        if (extras != null && (options = Manager.getInstance(context).getOptions(extras.getInt(Notification.EXTRA_ID, 0))) != null && (buildNotification = buildNotification(new Builder(options), extras)) != null) {
            onTrigger(buildNotification, extras);
        }
    }
}
