package p005de.appplant.cordova.plugin.localnotification;

import android.os.Bundle;
import android.support.p000v4.app.RemoteInput;
import com.adobe.phonegap.push.PushConstants;
import org.json.JSONException;
import org.json.JSONObject;
import p005de.appplant.cordova.plugin.notification.Notification;
import p005de.appplant.cordova.plugin.notification.Options;
import p005de.appplant.cordova.plugin.notification.Request;
import p005de.appplant.cordova.plugin.notification.receiver.AbstractClickReceiver;

/* renamed from: de.appplant.cordova.plugin.localnotification.ClickReceiver */
public class ClickReceiver extends AbstractClickReceiver {
    public void onClick(Notification notification, Bundle bundle) {
        String action = getAction();
        JSONObject jSONObject = new JSONObject();
        setTextInput(action, jSONObject);
        launchAppIf();
        LocalNotification.fireEvent(action, notification, jSONObject);
        if (!notification.getOptions().isSticky().booleanValue()) {
            if (isLast()) {
                notification.cancel();
            } else {
                notification.clear();
            }
        }
    }

    private void setTextInput(String str, JSONObject jSONObject) {
        Bundle resultsFromIntent = RemoteInput.getResultsFromIntent(getIntent());
        if (resultsFromIntent != null) {
            try {
                jSONObject.put(PushConstants.STYLE_TEXT, resultsFromIntent.getCharSequence(str));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void launchAppIf() {
        if (getIntent().getBooleanExtra(Options.EXTRA_LAUNCH, true)) {
            launchApp();
        }
    }

    private boolean isLast() {
        return getIntent().getBooleanExtra(Request.EXTRA_LAST, false);
    }
}
