package com.verso.cordova.clipboard;

import android.content.ClipData;
import android.content.ClipboardManager;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

public class Clipboard extends CordovaPlugin {
    private static final String actionClear = "clear";
    private static final String actionCopy = "copy";
    private static final String actionPaste = "paste";

    public boolean execute(String str, JSONArray jSONArray, CallbackContext callbackContext) throws JSONException {
        ClipboardManager clipboardManager = (ClipboardManager) this.f59cordova.getActivity().getSystemService("clipboard");
        if (str.equals(actionCopy)) {
            try {
                String string = jSONArray.getString(0);
                clipboardManager.setPrimaryClip(ClipData.newPlainText("Text", string));
                callbackContext.success(string);
                return true;
            } catch (JSONException unused) {
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.JSON_EXCEPTION));
            } catch (Exception e) {
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, e.toString()));
            }
        } else if (str.equals(actionPaste)) {
            String str2 = "";
            try {
                ClipData primaryClip = clipboardManager.getPrimaryClip();
                if (primaryClip != null) {
                    str2 = primaryClip.getItemAt(0).getText().toString();
                }
                callbackContext.success(str2);
                return true;
            } catch (Exception e2) {
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, e2.toString()));
            }
        } else {
            if (str.equals(actionClear)) {
                try {
                    clipboardManager.setPrimaryClip(ClipData.newPlainText("", ""));
                    callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK));
                    return true;
                } catch (Exception e3) {
                    callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, e3.toString()));
                }
            }
            return false;
        }
    }
}
