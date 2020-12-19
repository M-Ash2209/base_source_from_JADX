package p005de.appplant.cordova.plugin.localnotification;

import android.os.Bundle;
import p005de.appplant.cordova.plugin.notification.Notification;
import p005de.appplant.cordova.plugin.notification.Request;
import p005de.appplant.cordova.plugin.notification.receiver.AbstractClearReceiver;

/* renamed from: de.appplant.cordova.plugin.localnotification.ClearReceiver */
public class ClearReceiver extends AbstractClearReceiver {
    public void onClear(Notification notification, Bundle bundle) {
        if (bundle.getBoolean(Request.EXTRA_LAST, false)) {
            notification.cancel();
        } else {
            notification.clear();
        }
        if (LocalNotification.isAppRunning()) {
            LocalNotification.fireEvent("clear", notification);
        }
    }
}
