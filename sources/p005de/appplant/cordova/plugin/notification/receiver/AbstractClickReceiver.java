package p005de.appplant.cordova.plugin.notification.receiver;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import p005de.appplant.cordova.plugin.notification.Manager;
import p005de.appplant.cordova.plugin.notification.Notification;
import p005de.appplant.cordova.plugin.notification.action.Action;

/* renamed from: de.appplant.cordova.plugin.notification.receiver.AbstractClickReceiver */
public abstract class AbstractClickReceiver extends IntentService {
    private Intent intent;

    public abstract void onClick(Notification notification, Bundle bundle);

    public AbstractClickReceiver() {
        super("LocalNotificationClickReceiver");
    }

    /* access modifiers changed from: protected */
    public void onHandleIntent(Intent intent2) {
        Notification notification;
        this.intent = intent2;
        if (intent2 != null) {
            Bundle extras = intent2.getExtras();
            Context applicationContext = getApplicationContext();
            if (extras != null && (notification = Manager.getInstance(applicationContext).get(extras.getInt(Notification.EXTRA_ID))) != null) {
                onClick(notification, extras);
                this.intent = null;
            }
        }
    }

    /* access modifiers changed from: protected */
    public String getAction() {
        return getIntent().getExtras().getString(Action.EXTRA_ID, Action.CLICK_ACTION_ID);
    }

    /* access modifiers changed from: protected */
    public Intent getIntent() {
        return this.intent;
    }

    /* access modifiers changed from: protected */
    public void launchApp() {
        Context applicationContext = getApplicationContext();
        Intent launchIntentForPackage = applicationContext.getPackageManager().getLaunchIntentForPackage(applicationContext.getPackageName());
        if (launchIntentForPackage != null) {
            launchIntentForPackage.addFlags(537001984);
            applicationContext.startActivity(launchIntentForPackage);
        }
    }
}
