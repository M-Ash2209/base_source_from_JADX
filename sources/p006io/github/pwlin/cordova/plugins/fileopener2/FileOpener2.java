package p006io.github.pwlin.cordova.plugins.fileopener2;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.p000v4.app.NotificationCompat;
import android.webkit.MimeTypeMap;
import com.adobe.phonegap.push.PushConstants;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* renamed from: io.github.pwlin.cordova.plugins.fileopener2.FileOpener2 */
public class FileOpener2 extends CordovaPlugin {
    public boolean execute(String str, JSONArray jSONArray, CallbackContext callbackContext) throws JSONException {
        if (str.equals("open")) {
            String string = jSONArray.getString(0);
            String string2 = jSONArray.getString(1);
            boolean z = true;
            if (jSONArray.length() > 2) {
                z = Boolean.valueOf(jSONArray.getBoolean(2));
            }
            _open(string, string2, z, callbackContext);
        } else if (str.equals("uninstall")) {
            _uninstall(jSONArray.getString(0), callbackContext);
        } else if (str.equals("appIsInstalled")) {
            JSONObject jSONObject = new JSONObject();
            if (_appIsInstalled(jSONArray.getString(0))) {
                jSONObject.put(NotificationCompat.CATEGORY_STATUS, PluginResult.Status.OK.ordinal());
                jSONObject.put(PushConstants.MESSAGE, "Installed");
            } else {
                jSONObject.put(NotificationCompat.CATEGORY_STATUS, PluginResult.Status.NO_RESULT.ordinal());
                jSONObject.put(PushConstants.MESSAGE, "Not installed");
            }
            callbackContext.success(jSONObject);
        } else {
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put(NotificationCompat.CATEGORY_STATUS, PluginResult.Status.INVALID_ACTION.ordinal());
            jSONObject2.put(PushConstants.MESSAGE, "Invalid action");
            callbackContext.error(jSONObject2);
        }
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0029, code lost:
        if (r6.trim().equals("") != false) goto L_0x002b;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void _open(java.lang.String r5, java.lang.String r6, java.lang.Boolean r7, org.apache.cordova.CallbackContext r8) throws org.json.JSONException {
        /*
            r4 = this;
            org.apache.cordova.CordovaWebView r0 = r4.webView     // Catch:{ Exception -> 0x0012 }
            org.apache.cordova.CordovaResourceApi r0 = r0.getResourceApi()     // Catch:{ Exception -> 0x0012 }
            android.net.Uri r1 = android.net.Uri.parse(r5)     // Catch:{ Exception -> 0x0012 }
            android.net.Uri r0 = r0.remapUri(r1)     // Catch:{ Exception -> 0x0012 }
            java.lang.String r5 = r0.getPath()     // Catch:{ Exception -> 0x0012 }
        L_0x0012:
            java.io.File r0 = new java.io.File
            r0.<init>(r5)
            boolean r1 = r0.exists()
            if (r1 == 0) goto L_0x0105
            if (r6 == 0) goto L_0x002b
            java.lang.String r1 = r6.trim()     // Catch:{ ActivityNotFoundException -> 0x00d6 }
            java.lang.String r2 = ""
            boolean r1 = r1.equals(r2)     // Catch:{ ActivityNotFoundException -> 0x00d6 }
            if (r1 == 0) goto L_0x002f
        L_0x002b:
            java.lang.String r6 = r4._getMimeType(r5)     // Catch:{ ActivityNotFoundException -> 0x00d6 }
        L_0x002f:
            java.lang.String r5 = "application/vnd.android.package-archive"
            boolean r5 = r6.equals(r5)     // Catch:{ ActivityNotFoundException -> 0x00d6 }
            if (r5 == 0) goto L_0x007c
            android.content.Intent r5 = new android.content.Intent     // Catch:{ ActivityNotFoundException -> 0x00d6 }
            java.lang.String r1 = "android.intent.action.INSTALL_PACKAGE"
            r5.<init>(r1)     // Catch:{ ActivityNotFoundException -> 0x00d6 }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ ActivityNotFoundException -> 0x00d6 }
            r2 = 24
            if (r1 >= r2) goto L_0x0049
            android.net.Uri r0 = android.net.Uri.fromFile(r0)     // Catch:{ ActivityNotFoundException -> 0x00d6 }
            goto L_0x0072
        L_0x0049:
            org.apache.cordova.CordovaInterface r1 = r4.f59cordova     // Catch:{ ActivityNotFoundException -> 0x00d6 }
            android.app.Activity r1 = r1.getActivity()     // Catch:{ ActivityNotFoundException -> 0x00d6 }
            android.content.Context r1 = r1.getApplicationContext()     // Catch:{ ActivityNotFoundException -> 0x00d6 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ ActivityNotFoundException -> 0x00d6 }
            r2.<init>()     // Catch:{ ActivityNotFoundException -> 0x00d6 }
            org.apache.cordova.CordovaInterface r3 = r4.f59cordova     // Catch:{ ActivityNotFoundException -> 0x00d6 }
            android.app.Activity r3 = r3.getActivity()     // Catch:{ ActivityNotFoundException -> 0x00d6 }
            java.lang.String r3 = r3.getPackageName()     // Catch:{ ActivityNotFoundException -> 0x00d6 }
            r2.append(r3)     // Catch:{ ActivityNotFoundException -> 0x00d6 }
            java.lang.String r3 = ".fileOpener2.provider"
            r2.append(r3)     // Catch:{ ActivityNotFoundException -> 0x00d6 }
            java.lang.String r2 = r2.toString()     // Catch:{ ActivityNotFoundException -> 0x00d6 }
            android.net.Uri r0 = p006io.github.pwlin.cordova.plugins.fileopener2.FileProvider.getUriForFile(r1, r2, r0)     // Catch:{ ActivityNotFoundException -> 0x00d6 }
        L_0x0072:
            r5.setDataAndType(r0, r6)     // Catch:{ ActivityNotFoundException -> 0x00d6 }
            r6 = 268435457(0x10000001, float:2.5243552E-29)
            r5.setFlags(r6)     // Catch:{ ActivityNotFoundException -> 0x00d6 }
            goto L_0x00b3
        L_0x007c:
            android.content.Intent r5 = new android.content.Intent     // Catch:{ ActivityNotFoundException -> 0x00d6 }
            java.lang.String r1 = "android.intent.action.VIEW"
            r5.<init>(r1)     // Catch:{ ActivityNotFoundException -> 0x00d6 }
            org.apache.cordova.CordovaInterface r1 = r4.f59cordova     // Catch:{ ActivityNotFoundException -> 0x00d6 }
            android.app.Activity r1 = r1.getActivity()     // Catch:{ ActivityNotFoundException -> 0x00d6 }
            android.content.Context r1 = r1.getApplicationContext()     // Catch:{ ActivityNotFoundException -> 0x00d6 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ ActivityNotFoundException -> 0x00d6 }
            r2.<init>()     // Catch:{ ActivityNotFoundException -> 0x00d6 }
            org.apache.cordova.CordovaInterface r3 = r4.f59cordova     // Catch:{ ActivityNotFoundException -> 0x00d6 }
            android.app.Activity r3 = r3.getActivity()     // Catch:{ ActivityNotFoundException -> 0x00d6 }
            java.lang.String r3 = r3.getPackageName()     // Catch:{ ActivityNotFoundException -> 0x00d6 }
            r2.append(r3)     // Catch:{ ActivityNotFoundException -> 0x00d6 }
            java.lang.String r3 = ".fileOpener2.provider"
            r2.append(r3)     // Catch:{ ActivityNotFoundException -> 0x00d6 }
            java.lang.String r2 = r2.toString()     // Catch:{ ActivityNotFoundException -> 0x00d6 }
            android.net.Uri r0 = p006io.github.pwlin.cordova.plugins.fileopener2.FileProvider.getUriForFile(r1, r2, r0)     // Catch:{ ActivityNotFoundException -> 0x00d6 }
            r5.setDataAndType(r0, r6)     // Catch:{ ActivityNotFoundException -> 0x00d6 }
            r6 = 3
            r5.setFlags(r6)     // Catch:{ ActivityNotFoundException -> 0x00d6 }
        L_0x00b3:
            boolean r6 = r7.booleanValue()     // Catch:{ ActivityNotFoundException -> 0x00d6 }
            if (r6 == 0) goto L_0x00c3
            org.apache.cordova.CordovaInterface r6 = r4.f59cordova     // Catch:{ ActivityNotFoundException -> 0x00d6 }
            android.app.Activity r6 = r6.getActivity()     // Catch:{ ActivityNotFoundException -> 0x00d6 }
            r6.startActivity(r5)     // Catch:{ ActivityNotFoundException -> 0x00d6 }
            goto L_0x00d2
        L_0x00c3:
            org.apache.cordova.CordovaInterface r6 = r4.f59cordova     // Catch:{ ActivityNotFoundException -> 0x00d6 }
            android.app.Activity r6 = r6.getActivity()     // Catch:{ ActivityNotFoundException -> 0x00d6 }
            java.lang.String r7 = "Open File in..."
            android.content.Intent r5 = android.content.Intent.createChooser(r5, r7)     // Catch:{ ActivityNotFoundException -> 0x00d6 }
            r6.startActivity(r5)     // Catch:{ ActivityNotFoundException -> 0x00d6 }
        L_0x00d2:
            r8.success()     // Catch:{ ActivityNotFoundException -> 0x00d6 }
            goto L_0x011f
        L_0x00d6:
            r5 = move-exception
            org.json.JSONObject r6 = new org.json.JSONObject
            r6.<init>()
            java.lang.String r7 = "status"
            org.apache.cordova.PluginResult$Status r0 = org.apache.cordova.PluginResult.Status.ERROR
            int r0 = r0.ordinal()
            r6.put(r7, r0)
            java.lang.String r7 = "message"
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Activity not found: "
            r0.append(r1)
            java.lang.String r5 = r5.getMessage()
            r0.append(r5)
            java.lang.String r5 = r0.toString()
            r6.put(r7, r5)
            r8.error((org.json.JSONObject) r6)
            goto L_0x011f
        L_0x0105:
            org.json.JSONObject r5 = new org.json.JSONObject
            r5.<init>()
            java.lang.String r6 = "status"
            org.apache.cordova.PluginResult$Status r7 = org.apache.cordova.PluginResult.Status.ERROR
            int r7 = r7.ordinal()
            r5.put(r6, r7)
            java.lang.String r6 = "message"
            java.lang.String r7 = "File not found"
            r5.put(r6, r7)
            r8.error((org.json.JSONObject) r5)
        L_0x011f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: p006io.github.pwlin.cordova.plugins.fileopener2.FileOpener2._open(java.lang.String, java.lang.String, java.lang.Boolean, org.apache.cordova.CallbackContext):void");
    }

    private String _getMimeType(String str) {
        String mimeTypeFromExtension;
        int lastIndexOf = str.lastIndexOf(46);
        if (lastIndexOf <= 0 || (mimeTypeFromExtension = MimeTypeMap.getSingleton().getMimeTypeFromExtension(str.substring(lastIndexOf + 1))) == null) {
            return "*/*";
        }
        return mimeTypeFromExtension;
    }

    private void _uninstall(String str, CallbackContext callbackContext) throws JSONException {
        if (_appIsInstalled(str)) {
            Intent intent = new Intent("android.intent.action.UNINSTALL_PACKAGE");
            intent.setData(Uri.parse("package:" + str));
            this.f59cordova.getActivity().startActivity(intent);
            callbackContext.success();
            return;
        }
        JSONObject jSONObject = new JSONObject();
        jSONObject.put(NotificationCompat.CATEGORY_STATUS, PluginResult.Status.ERROR.ordinal());
        jSONObject.put(PushConstants.MESSAGE, "This package is not installed");
        callbackContext.error(jSONObject);
    }

    private boolean _appIsInstalled(String str) {
        try {
            this.f59cordova.getActivity().getPackageManager().getPackageInfo(str, 1);
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }
}
