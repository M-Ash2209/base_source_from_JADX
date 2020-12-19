package cordova.plugins;

import android.support.p000v4.app.NotificationManagerCompat;
import android.util.Log;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

public class Diagnostic_Notifications extends CordovaPlugin {
    public static final String TAG = "Diagnostic_Notifications";
    public static Diagnostic_Notifications instance;
    protected CallbackContext currentContext;
    private Diagnostic diagnostic;

    public void initialize(CordovaInterface cordovaInterface, CordovaWebView cordovaWebView) {
        Log.d(TAG, "initialize()");
        instance = this;
        this.diagnostic = Diagnostic.getInstance();
        super.initialize(cordovaInterface, cordovaWebView);
    }

    public boolean execute(String str, JSONArray jSONArray, CallbackContext callbackContext) throws JSONException {
        this.currentContext = callbackContext;
        try {
            if (str.equals("isRemoteNotificationsEnabled")) {
                callbackContext.success(isRemoteNotificationsEnabled() ? 1 : 0);
                return true;
            }
            this.diagnostic.handleError("Invalid action");
            return false;
        } catch (Exception e) {
            this.diagnostic.handleError("Exception occurred: ".concat(e.getMessage()));
            return false;
        }
    }

    public boolean isRemoteNotificationsEnabled() {
        return NotificationManagerCompat.from(this.f59cordova.getActivity().getApplicationContext()).areNotificationsEnabled();
    }
}
