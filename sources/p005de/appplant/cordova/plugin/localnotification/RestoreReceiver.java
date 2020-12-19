package p005de.appplant.cordova.plugin.localnotification;

import java.util.Date;
import p005de.appplant.cordova.plugin.notification.Builder;
import p005de.appplant.cordova.plugin.notification.Manager;
import p005de.appplant.cordova.plugin.notification.Notification;
import p005de.appplant.cordova.plugin.notification.Request;
import p005de.appplant.cordova.plugin.notification.receiver.AbstractRestoreReceiver;

/* renamed from: de.appplant.cordova.plugin.localnotification.RestoreReceiver */
public class RestoreReceiver extends AbstractRestoreReceiver {
    public void onRestore(Request request, Notification notification) {
        Date triggerDate = request.getTriggerDate();
        boolean z = triggerDate != null && triggerDate.after(new Date());
        if (z || !notification.isHighPrio()) {
            notification.clear();
        } else {
            notification.show();
        }
        Manager instance = Manager.getInstance(notification.getContext());
        if (z || notification.isRepeating()) {
            instance.schedule(request, TriggerReceiver.class);
        }
    }

    public Notification buildNotification(Builder builder) {
        return builder.setClickActivity(ClickReceiver.class).setClearReceiver(ClearReceiver.class).build();
    }
}
