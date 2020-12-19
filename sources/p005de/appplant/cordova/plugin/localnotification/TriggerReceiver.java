package p005de.appplant.cordova.plugin.localnotification;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import java.util.Calendar;
import p005de.appplant.cordova.plugin.notification.Builder;
import p005de.appplant.cordova.plugin.notification.Manager;
import p005de.appplant.cordova.plugin.notification.Notification;
import p005de.appplant.cordova.plugin.notification.Options;
import p005de.appplant.cordova.plugin.notification.Request;
import p005de.appplant.cordova.plugin.notification.receiver.AbstractTriggerReceiver;

/* renamed from: de.appplant.cordova.plugin.localnotification.TriggerReceiver */
public class TriggerReceiver extends AbstractTriggerReceiver {
    public void onTrigger(Notification notification, Bundle bundle) {
        boolean z = bundle.getBoolean(Notification.EXTRA_UPDATE, false);
        Context context = notification.getContext();
        Options options = notification.getOptions();
        Manager instance = Manager.getInstance(context);
        int badgeNumber = options.getBadgeNumber();
        if (badgeNumber > 0) {
            instance.setBadge(badgeNumber);
        }
        if (options.shallWakeUp()) {
            wakeUp(context);
        }
        notification.show();
        if (!z && LocalNotification.isAppRunning()) {
            LocalNotification.fireEvent("trigger", notification);
        }
        if (options.isInfiniteTrigger()) {
            Calendar instance2 = Calendar.getInstance();
            instance2.add(12, 1);
            instance.schedule(new Request(options, instance2.getTime()), getClass());
        }
    }

    private void wakeUp(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService("power");
        if (powerManager != null) {
            PowerManager.WakeLock newWakeLock = powerManager.newWakeLock(268435462, "LocalNotification");
            newWakeLock.setReferenceCounted(false);
            newWakeLock.acquire(1000);
            if (Build.VERSION.SDK_INT >= 21) {
                newWakeLock.release(1);
            } else {
                newWakeLock.release();
            }
        }
    }

    public Notification buildNotification(Builder builder, Bundle bundle) {
        return builder.setClickActivity(ClickReceiver.class).setClearReceiver(ClearReceiver.class).setExtras(bundle).build();
    }
}
