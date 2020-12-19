package p005de.appplant.cordova.plugin.notification.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.UserManager;
import org.json.JSONObject;
import p005de.appplant.cordova.plugin.notification.Builder;
import p005de.appplant.cordova.plugin.notification.Manager;
import p005de.appplant.cordova.plugin.notification.Notification;
import p005de.appplant.cordova.plugin.notification.Options;
import p005de.appplant.cordova.plugin.notification.Request;

/* renamed from: de.appplant.cordova.plugin.notification.receiver.AbstractRestoreReceiver */
public abstract class AbstractRestoreReceiver extends BroadcastReceiver {
    public abstract Notification buildNotification(Builder builder);

    public abstract void onRestore(Request request, Notification notification);

    public void onReceive(Context context, Intent intent) {
        UserManager userManager;
        intent.getAction();
        if (Build.VERSION.SDK_INT < 24 || ((userManager = (UserManager) context.getSystemService(UserManager.class)) != null && userManager.isUserUnlocked())) {
            for (JSONObject options : Manager.getInstance(context).getOptions()) {
                Options options2 = new Options(context, options);
                onRestore(new Request(options2), buildNotification(new Builder(options2)));
            }
        }
    }
}
