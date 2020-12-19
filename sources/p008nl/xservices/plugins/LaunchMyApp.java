package p008nl.xservices.plugins;

import android.content.Intent;
import android.net.Uri;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.Locale;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

/* renamed from: nl.xservices.plugins.LaunchMyApp */
public class LaunchMyApp extends CordovaPlugin {
    private static final String ACTION_CHECKINTENT = "checkIntent";
    private static final String ACTION_CLEARINTENT = "clearIntent";
    private static final String ACTION_GETLASTINTENT = "getLastIntent";
    private String lastIntentString = null;
    private boolean resetIntent;

    public void initialize(CordovaInterface cordovaInterface, CordovaWebView cordovaWebView) {
        boolean z = false;
        if (this.preferences.getBoolean("resetIntent", false) || this.preferences.getBoolean("CustomURLSchemePluginClearsAndroidIntent", false)) {
            z = true;
        }
        this.resetIntent = z;
    }

    public boolean execute(String str, JSONArray jSONArray, CallbackContext callbackContext) throws JSONException {
        if (ACTION_CLEARINTENT.equalsIgnoreCase(str)) {
            Intent intent = this.f59cordova.getActivity().getIntent();
            if (this.resetIntent) {
                intent.setData((Uri) null);
            }
            return true;
        } else if (ACTION_CHECKINTENT.equalsIgnoreCase(str)) {
            Intent intent2 = this.f59cordova.getActivity().getIntent();
            String dataString = intent2.getDataString();
            if (dataString == null || intent2.getScheme() == null) {
                callbackContext.error("App was not started via the launchmyapp URL scheme. Ignoring this errorcallback is the best approach.");
            } else {
                this.lastIntentString = dataString;
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, intent2.getDataString()));
            }
            return true;
        } else if (ACTION_GETLASTINTENT.equalsIgnoreCase(str)) {
            if (this.lastIntentString != null) {
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, this.lastIntentString));
            } else {
                callbackContext.error("No intent received so far.");
            }
            return true;
        } else {
            callbackContext.error("This plugin only responds to the checkIntent action.");
            return false;
        }
    }

    public void onNewIntent(Intent intent) {
        String dataString = intent.getDataString();
        if (dataString != null && intent.getScheme() != null) {
            if (this.resetIntent) {
                intent.setData((Uri) null);
            }
            try {
                StringWriter stringWriter = new StringWriter(dataString.length() * 2);
                escapeJavaStyleString(stringWriter, dataString, true, false);
                CordovaWebView cordovaWebView = this.webView;
                cordovaWebView.loadUrl("javascript:handleOpenURL('" + URLEncoder.encode(stringWriter.toString()) + "');");
            } catch (IOException unused) {
            }
        }
    }

    private static void escapeJavaStyleString(Writer writer, String str, boolean z, boolean z2) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("The Writer must not be null");
        } else if (str != null) {
            int length = str.length();
            for (int i = 0; i < length; i++) {
                char charAt = str.charAt(i);
                if (charAt > 4095) {
                    writer.write("\\u" + hex(charAt));
                } else if (charAt > 255) {
                    writer.write("\\u0" + hex(charAt));
                } else if (charAt > 127) {
                    writer.write("\\u00" + hex(charAt));
                } else if (charAt < ' ') {
                    switch (charAt) {
                        case 8:
                            writer.write(92);
                            writer.write(98);
                            break;
                        case 9:
                            writer.write(92);
                            writer.write(116);
                            break;
                        case 10:
                            writer.write(92);
                            writer.write(110);
                            break;
                        case 12:
                            writer.write(92);
                            writer.write(102);
                            break;
                        case 13:
                            writer.write(92);
                            writer.write(114);
                            break;
                        default:
                            if (charAt <= 15) {
                                writer.write("\\u000" + hex(charAt));
                                break;
                            } else {
                                writer.write("\\u00" + hex(charAt));
                                break;
                            }
                    }
                } else if (charAt == '\"') {
                    writer.write(92);
                    writer.write(34);
                } else if (charAt == '\'') {
                    if (z) {
                        writer.write(92);
                    }
                    writer.write(39);
                } else if (charAt == '/') {
                    if (z2) {
                        writer.write(92);
                    }
                    writer.write(47);
                } else if (charAt != '\\') {
                    writer.write(charAt);
                } else {
                    writer.write(92);
                    writer.write(92);
                }
            }
        }
    }

    private static String hex(char c) {
        return Integer.toHexString(c).toUpperCase(Locale.ENGLISH);
    }
}
