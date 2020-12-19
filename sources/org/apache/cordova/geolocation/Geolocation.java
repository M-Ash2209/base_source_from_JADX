package org.apache.cordova.geolocation;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
import org.apache.cordova.PermissionHelper;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

public class Geolocation extends CordovaPlugin {
    String TAG = "GeolocationPlugin";
    CallbackContext context;
    String[] permissions = {"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"};

    public boolean execute(String str, JSONArray jSONArray, CallbackContext callbackContext) throws JSONException {
        LOG.m37d(this.TAG, "We are entering execute");
        this.context = callbackContext;
        if (!str.equals("getPermission")) {
            return false;
        }
        if (hasPermisssion()) {
            this.context.sendPluginResult(new PluginResult(PluginResult.Status.OK));
            return true;
        }
        PermissionHelper.requestPermissions(this, 0, this.permissions);
        return true;
    }

    public void onRequestPermissionResult(int i, String[] strArr, int[] iArr) throws JSONException {
        if (this.context != null) {
            for (int i2 : iArr) {
                if (i2 == -1) {
                    LOG.m37d(this.TAG, "Permission Denied!");
                    this.context.sendPluginResult(new PluginResult(PluginResult.Status.ILLEGAL_ACCESS_EXCEPTION));
                    return;
                }
            }
            this.context.sendPluginResult(new PluginResult(PluginResult.Status.OK));
        }
    }

    public boolean hasPermisssion() {
        for (String hasPermission : this.permissions) {
            if (!PermissionHelper.hasPermission(this, hasPermission)) {
                return false;
            }
        }
        return true;
    }

    public void requestPermissions(int i) {
        PermissionHelper.requestPermissions(this, i, this.permissions);
    }
}
