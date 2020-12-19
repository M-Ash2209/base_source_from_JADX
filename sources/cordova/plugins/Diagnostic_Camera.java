package cordova.plugins;

import android.hardware.Camera;
import android.util.Log;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

public class Diagnostic_Camera extends CordovaPlugin {
    public static final String TAG = "Diagnostic_Camera";
    public static Diagnostic_Camera instance;
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
            if (str.equals("isCameraPresent")) {
                callbackContext.success(isCameraPresent() ? 1 : 0);
                return true;
            }
            this.diagnostic.handleError("Invalid action");
            return false;
        } catch (Exception e) {
            this.diagnostic.handleError("Exception occurred: ".concat(e.getMessage()));
            return false;
        }
    }

    public boolean isCameraPresent() {
        return this.f59cordova.getActivity().getPackageManager().hasSystemFeature("android.hardware.camera") && Camera.getNumberOfCameras() > 0;
    }
}
