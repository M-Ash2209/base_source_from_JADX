package com.darryncampbell.cordova.plugin.intent;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.p000v4.app.ActivityCompat;
import android.support.p000v4.content.ContextCompat;
import android.support.p000v4.content.FileProvider;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.MimeTypeMap;
import com.adobe.phonegap.push.PushConstants;
import com.google.android.gms.common.internal.ImagesContract;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaResourceApi;
import org.apache.cordova.PluginResult;
import org.apache.cordova.globalization.Globalization;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class IntentShim extends CordovaPlugin {
    private static final String LOG_TAG = "Cordova Intents Shim";
    private Intent deferredIntent = null;
    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            intent.getAction();
            if (IntentShim.this.onBroadcastCallbackContext != null) {
                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, IntentShim.this.getIntentJson(intent));
                pluginResult.setKeepCallback(true);
                IntentShim.this.onBroadcastCallbackContext.sendPluginResult(pluginResult);
            }
        }
    };
    private CallbackContext onActivityResultCallbackContext = null;
    /* access modifiers changed from: private */
    public CallbackContext onBroadcastCallbackContext = null;
    private CallbackContext onNewIntentCallbackContext = null;

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v3, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v5, resolved type: org.json.JSONObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v7, resolved type: org.json.JSONArray} */
    /* JADX WARNING: type inference failed for: r1v2 */
    /* JADX WARNING: type inference failed for: r1v4 */
    /* JADX WARNING: type inference failed for: r3v4 */
    /* JADX WARNING: type inference failed for: r1v5, types: [int] */
    /* JADX WARNING: type inference failed for: r1v7, types: [int] */
    /* JADX WARNING: type inference failed for: r1v9 */
    /* JADX WARNING: type inference failed for: r3v9 */
    /* JADX WARNING: type inference failed for: r1v10 */
    /* JADX WARNING: type inference failed for: r3v10 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean execute(java.lang.String r10, org.json.JSONArray r11, org.apache.cordova.CallbackContext r12) throws org.json.JSONException {
        /*
            r9 = this;
            java.lang.String r0 = "Cordova Intents Shim"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Action: "
            r1.append(r2)
            r1.append(r10)
            java.lang.String r1 = r1.toString()
            android.util.Log.d(r0, r1)
            java.lang.String r0 = "startActivity"
            boolean r0 = r10.equals(r0)
            r1 = 0
            r2 = 1
            if (r0 != 0) goto L_0x032b
            java.lang.String r0 = "startActivityForResult"
            boolean r0 = r10.equals(r0)
            if (r0 == 0) goto L_0x002a
            goto L_0x032b
        L_0x002a:
            java.lang.String r0 = "sendBroadcast"
            boolean r0 = r10.equals(r0)
            if (r0 == 0) goto L_0x0059
            int r10 = r11.length()
            if (r10 == r2) goto L_0x0043
            org.apache.cordova.PluginResult r10 = new org.apache.cordova.PluginResult
            org.apache.cordova.PluginResult$Status r11 = org.apache.cordova.PluginResult.Status.INVALID_ACTION
            r10.<init>(r11)
            r12.sendPluginResult(r10)
            return r1
        L_0x0043:
            org.json.JSONObject r10 = r11.getJSONObject(r1)
            android.content.Intent r10 = r9.populateIntent(r10, r12)
            r9.sendBroadcast(r10)
            org.apache.cordova.PluginResult r10 = new org.apache.cordova.PluginResult
            org.apache.cordova.PluginResult$Status r11 = org.apache.cordova.PluginResult.Status.OK
            r10.<init>(r11)
            r12.sendPluginResult(r10)
            return r2
        L_0x0059:
            java.lang.String r0 = "startService"
            boolean r0 = r10.equals(r0)
            if (r0 == 0) goto L_0x0088
            int r10 = r11.length()
            if (r10 == r2) goto L_0x0072
            org.apache.cordova.PluginResult r10 = new org.apache.cordova.PluginResult
            org.apache.cordova.PluginResult$Status r11 = org.apache.cordova.PluginResult.Status.INVALID_ACTION
            r10.<init>(r11)
            r12.sendPluginResult(r10)
            return r1
        L_0x0072:
            org.json.JSONObject r10 = r11.getJSONObject(r1)
            android.content.Intent r10 = r9.populateIntent(r10, r12)
            r9.startService(r10)
            org.apache.cordova.PluginResult r10 = new org.apache.cordova.PluginResult
            org.apache.cordova.PluginResult$Status r11 = org.apache.cordova.PluginResult.Status.OK
            r10.<init>(r11)
            r12.sendPluginResult(r10)
            return r2
        L_0x0088:
            java.lang.String r0 = "registerBroadcastReceiver"
            boolean r0 = r10.equals(r0)
            r3 = 0
            if (r0 == 0) goto L_0x01a6
            org.apache.cordova.CordovaInterface r10 = r9.f59cordova     // Catch:{ IllegalArgumentException -> 0x009c }
            android.app.Activity r10 = r10.getActivity()     // Catch:{ IllegalArgumentException -> 0x009c }
            android.content.BroadcastReceiver r0 = r9.myBroadcastReceiver     // Catch:{ IllegalArgumentException -> 0x009c }
            r10.unregisterReceiver(r0)     // Catch:{ IllegalArgumentException -> 0x009c }
        L_0x009c:
            int r10 = r11.length()
            if (r10 == r2) goto L_0x00ad
            org.apache.cordova.PluginResult r10 = new org.apache.cordova.PluginResult
            org.apache.cordova.PluginResult$Status r11 = org.apache.cordova.PluginResult.Status.INVALID_ACTION
            r10.<init>(r11)
            r12.sendPluginResult(r10)
            return r1
        L_0x00ad:
            org.json.JSONObject r10 = r11.getJSONObject(r1)
            java.lang.String r11 = "filterActions"
            boolean r11 = r10.has(r11)
            if (r11 == 0) goto L_0x00c0
            java.lang.String r11 = "filterActions"
            org.json.JSONArray r11 = r10.getJSONArray(r11)
            goto L_0x00c1
        L_0x00c0:
            r11 = r3
        L_0x00c1:
            if (r11 == 0) goto L_0x0194
            int r0 = r11.length()
            if (r0 != 0) goto L_0x00cb
            goto L_0x0194
        L_0x00cb:
            r9.onBroadcastCallbackContext = r12
            org.apache.cordova.PluginResult r0 = new org.apache.cordova.PluginResult
            org.apache.cordova.PluginResult$Status r4 = org.apache.cordova.PluginResult.Status.NO_RESULT
            r0.<init>(r4)
            r0.setKeepCallback(r2)
            android.content.IntentFilter r4 = new android.content.IntentFilter
            r4.<init>()
            r5 = 0
        L_0x00dd:
            int r6 = r11.length()
            if (r5 >= r6) goto L_0x0107
            java.lang.String r6 = "Cordova Intents Shim"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "Registering broadcast receiver for filter: "
            r7.append(r8)
            java.lang.String r8 = r11.getString(r5)
            r7.append(r8)
            java.lang.String r7 = r7.toString()
            android.util.Log.d(r6, r7)
            java.lang.String r6 = r11.getString(r5)
            r4.addAction(r6)
            int r5 = r5 + 1
            goto L_0x00dd
        L_0x0107:
            java.lang.String r11 = "filterCategories"
            boolean r11 = r10.has(r11)
            if (r11 == 0) goto L_0x0116
            java.lang.String r11 = "filterCategories"
            org.json.JSONArray r11 = r10.getJSONArray(r11)
            goto L_0x0117
        L_0x0116:
            r11 = r3
        L_0x0117:
            if (r11 == 0) goto L_0x0144
            r5 = 0
        L_0x011a:
            int r6 = r11.length()
            if (r5 >= r6) goto L_0x0144
            java.lang.String r6 = "Cordova Intents Shim"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "Registering broadcast receiver for category filter: "
            r7.append(r8)
            java.lang.String r8 = r11.getString(r5)
            r7.append(r8)
            java.lang.String r7 = r7.toString()
            android.util.Log.d(r6, r7)
            java.lang.String r6 = r11.getString(r5)
            r4.addCategory(r6)
            int r5 = r5 + 1
            goto L_0x011a
        L_0x0144:
            java.lang.String r11 = "filterDataSchemes"
            boolean r11 = r10.has(r11)
            if (r11 == 0) goto L_0x0152
            java.lang.String r11 = "filterDataSchemes"
            org.json.JSONArray r3 = r10.getJSONArray(r11)
        L_0x0152:
            if (r3 == 0) goto L_0x0184
            int r10 = r3.length()
            if (r10 <= 0) goto L_0x0184
        L_0x015a:
            int r10 = r3.length()
            if (r1 >= r10) goto L_0x0184
            java.lang.String r10 = "Cordova Intents Shim"
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r5 = "Associating data scheme to filter: "
            r11.append(r5)
            java.lang.String r5 = r3.getString(r1)
            r11.append(r5)
            java.lang.String r11 = r11.toString()
            android.util.Log.d(r10, r11)
            java.lang.String r10 = r3.getString(r1)
            r4.addDataScheme(r10)
            int r1 = r1 + 1
            goto L_0x015a
        L_0x0184:
            org.apache.cordova.CordovaInterface r10 = r9.f59cordova
            android.app.Activity r10 = r10.getActivity()
            android.content.BroadcastReceiver r11 = r9.myBroadcastReceiver
            r10.registerReceiver(r11, r4)
            r12.sendPluginResult(r0)
            goto L_0x032a
        L_0x0194:
            java.lang.String r10 = "Cordova Intents Shim"
            java.lang.String r11 = "filterActions argument is not in the expected format"
            android.util.Log.w(r10, r11)
            org.apache.cordova.PluginResult r10 = new org.apache.cordova.PluginResult
            org.apache.cordova.PluginResult$Status r11 = org.apache.cordova.PluginResult.Status.INVALID_ACTION
            r10.<init>(r11)
            r12.sendPluginResult(r10)
            return r1
        L_0x01a6:
            java.lang.String r0 = "unregisterBroadcastReceiver"
            boolean r0 = r10.equals(r0)
            if (r0 == 0) goto L_0x01bb
            org.apache.cordova.CordovaInterface r10 = r9.f59cordova     // Catch:{ IllegalArgumentException -> 0x032a }
            android.app.Activity r10 = r10.getActivity()     // Catch:{ IllegalArgumentException -> 0x032a }
            android.content.BroadcastReceiver r11 = r9.myBroadcastReceiver     // Catch:{ IllegalArgumentException -> 0x032a }
            r10.unregisterReceiver(r11)     // Catch:{ IllegalArgumentException -> 0x032a }
            goto L_0x032a
        L_0x01bb:
            java.lang.String r0 = "onIntent"
            boolean r0 = r10.equals(r0)
            if (r0 == 0) goto L_0x01ed
            int r10 = r11.length()
            if (r10 == r2) goto L_0x01d4
            org.apache.cordova.PluginResult r10 = new org.apache.cordova.PluginResult
            org.apache.cordova.PluginResult$Status r11 = org.apache.cordova.PluginResult.Status.INVALID_ACTION
            r10.<init>(r11)
            r12.sendPluginResult(r10)
            return r1
        L_0x01d4:
            r9.onNewIntentCallbackContext = r12
            android.content.Intent r10 = r9.deferredIntent
            if (r10 == 0) goto L_0x01df
            r9.fireOnNewIntent(r10)
            r9.deferredIntent = r3
        L_0x01df:
            org.apache.cordova.PluginResult r10 = new org.apache.cordova.PluginResult
            org.apache.cordova.PluginResult$Status r11 = org.apache.cordova.PluginResult.Status.NO_RESULT
            r10.<init>(r11)
            r10.setKeepCallback(r2)
            r12.sendPluginResult(r10)
            return r2
        L_0x01ed:
            java.lang.String r0 = "onActivityResult"
            boolean r0 = r10.equals(r0)
            if (r0 == 0) goto L_0x0216
            int r10 = r11.length()
            if (r10 == r2) goto L_0x0206
            org.apache.cordova.PluginResult r10 = new org.apache.cordova.PluginResult
            org.apache.cordova.PluginResult$Status r11 = org.apache.cordova.PluginResult.Status.INVALID_ACTION
            r10.<init>(r11)
            r12.sendPluginResult(r10)
            return r1
        L_0x0206:
            r9.onActivityResultCallbackContext = r12
            org.apache.cordova.PluginResult r10 = new org.apache.cordova.PluginResult
            org.apache.cordova.PluginResult$Status r11 = org.apache.cordova.PluginResult.Status.NO_RESULT
            r10.<init>(r11)
            r10.setKeepCallback(r2)
            r12.sendPluginResult(r10)
            return r2
        L_0x0216:
            java.lang.String r0 = "getIntent"
            boolean r0 = r10.equals(r0)
            if (r0 == 0) goto L_0x024f
            int r10 = r11.length()
            if (r10 == 0) goto L_0x022f
            org.apache.cordova.PluginResult r10 = new org.apache.cordova.PluginResult
            org.apache.cordova.PluginResult$Status r11 = org.apache.cordova.PluginResult.Status.INVALID_ACTION
            r10.<init>(r11)
            r12.sendPluginResult(r10)
            return r1
        L_0x022f:
            android.content.Intent r10 = r9.deferredIntent
            if (r10 == 0) goto L_0x0236
            r9.deferredIntent = r3
            goto L_0x0240
        L_0x0236:
            org.apache.cordova.CordovaInterface r10 = r9.f59cordova
            android.app.Activity r10 = r10.getActivity()
            android.content.Intent r10 = r10.getIntent()
        L_0x0240:
            org.apache.cordova.PluginResult r11 = new org.apache.cordova.PluginResult
            org.apache.cordova.PluginResult$Status r0 = org.apache.cordova.PluginResult.Status.OK
            org.json.JSONObject r10 = r9.getIntentJson(r10)
            r11.<init>((org.apache.cordova.PluginResult.Status) r0, (org.json.JSONObject) r10)
            r12.sendPluginResult(r11)
            return r2
        L_0x024f:
            java.lang.String r0 = "sendResult"
            boolean r0 = r10.equals(r0)
            if (r0 == 0) goto L_0x02fe
            android.content.Intent r10 = new android.content.Intent
            r10.<init>()
            int r0 = r11.length()
            if (r0 <= 0) goto L_0x02e0
            org.json.JSONObject r11 = r11.getJSONObject(r1)
            java.lang.String r0 = "extras"
            boolean r0 = r11.has(r0)
            if (r0 == 0) goto L_0x0274
            java.lang.String r0 = "extras"
            org.json.JSONObject r3 = r11.getJSONObject(r0)
        L_0x0274:
            if (r3 == 0) goto L_0x02e0
            org.json.JSONArray r11 = r3.names()
        L_0x027a:
            int r0 = r11.length()
            if (r1 >= r0) goto L_0x02e0
            java.lang.String r0 = r11.getString(r1)
            java.lang.Object r4 = r3.get(r0)
            boolean r5 = r4 instanceof org.json.JSONObject
            if (r5 == 0) goto L_0x029a
            java.lang.Object r4 = r3.get(r0)
            org.json.JSONObject r4 = (org.json.JSONObject) r4
            android.os.Bundle r4 = r9.toBundle(r4)
            r10.putExtra(r0, r4)
            goto L_0x02dd
        L_0x029a:
            boolean r5 = r4 instanceof java.lang.Boolean
            if (r5 == 0) goto L_0x02a6
            boolean r4 = r3.getBoolean(r0)
            r10.putExtra(r0, r4)
            goto L_0x02dd
        L_0x02a6:
            boolean r5 = r4 instanceof java.lang.Integer
            if (r5 == 0) goto L_0x02b2
            int r4 = r3.getInt(r0)
            r10.putExtra(r0, r4)
            goto L_0x02dd
        L_0x02b2:
            boolean r5 = r4 instanceof java.lang.Long
            if (r5 == 0) goto L_0x02be
            long r4 = r3.getLong(r0)
            r10.putExtra(r0, r4)
            goto L_0x02dd
        L_0x02be:
            boolean r5 = r4 instanceof java.lang.Double
            if (r5 == 0) goto L_0x02ca
            double r4 = r3.getDouble(r0)
            r10.putExtra(r0, r4)
            goto L_0x02dd
        L_0x02ca:
            boolean r4 = r4 instanceof java.lang.Float
            if (r4 == 0) goto L_0x02d6
            double r4 = r3.getDouble(r0)
            r10.putExtra(r0, r4)
            goto L_0x02dd
        L_0x02d6:
            java.lang.String r4 = r3.getString(r0)
            r10.putExtra(r0, r4)
        L_0x02dd:
            int r1 = r1 + 1
            goto L_0x027a
        L_0x02e0:
            org.apache.cordova.CordovaInterface r11 = r9.f59cordova
            android.app.Activity r11 = r11.getActivity()
            r0 = -1
            r11.setResult(r0, r10)
            org.apache.cordova.PluginResult r10 = new org.apache.cordova.PluginResult
            org.apache.cordova.PluginResult$Status r11 = org.apache.cordova.PluginResult.Status.OK
            r10.<init>(r11)
            r12.sendPluginResult(r10)
            org.apache.cordova.CordovaInterface r10 = r9.f59cordova
            android.app.Activity r10 = r10.getActivity()
            r10.finish()
            goto L_0x032a
        L_0x02fe:
            java.lang.String r0 = "realPathFromUri"
            boolean r10 = r10.equals(r0)
            if (r10 == 0) goto L_0x032a
            int r10 = r11.length()
            if (r10 == r2) goto L_0x0317
            org.apache.cordova.PluginResult r10 = new org.apache.cordova.PluginResult
            org.apache.cordova.PluginResult$Status r11 = org.apache.cordova.PluginResult.Status.INVALID_ACTION
            r10.<init>(r11)
            r12.sendPluginResult(r10)
            return r1
        L_0x0317:
            org.json.JSONObject r10 = r11.getJSONObject(r1)
            java.lang.String r10 = r9.getRealPathFromURI_API19(r10, r12)
            org.apache.cordova.PluginResult r11 = new org.apache.cordova.PluginResult
            org.apache.cordova.PluginResult$Status r0 = org.apache.cordova.PluginResult.Status.OK
            r11.<init>((org.apache.cordova.PluginResult.Status) r0, (java.lang.String) r10)
            r12.sendPluginResult(r11)
            return r2
        L_0x032a:
            return r2
        L_0x032b:
            int r0 = r11.length()
            if (r0 == r2) goto L_0x033c
            org.apache.cordova.PluginResult r10 = new org.apache.cordova.PluginResult
            org.apache.cordova.PluginResult$Status r11 = org.apache.cordova.PluginResult.Status.INVALID_ACTION
            r10.<init>(r11)
            r12.sendPluginResult(r10)
            return r1
        L_0x033c:
            org.json.JSONObject r11 = r11.getJSONObject(r1)
            android.content.Intent r0 = r9.populateIntent(r11, r12)
            java.lang.String r3 = "requestCode"
            boolean r3 = r11.has(r3)
            if (r3 == 0) goto L_0x0353
            java.lang.String r3 = "requestCode"
            int r11 = r11.getInt(r3)
            goto L_0x0354
        L_0x0353:
            r11 = 1
        L_0x0354:
            java.lang.String r3 = "startActivityForResult"
            boolean r10 = r10.equals(r3)
            if (r10 == 0) goto L_0x035f
            r9.onActivityResultCallbackContext = r12
            r1 = 1
        L_0x035f:
            r9.startActivity(r0, r1, r11, r12)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.darryncampbell.cordova.plugin.intent.IntentShim.execute(java.lang.String, org.json.JSONArray, org.apache.cordova.CallbackContext):boolean");
    }

    private Uri remapUriWithFileProvider(String str, CallbackContext callbackContext) {
        if (ContextCompat.checkSelfPermission(this.f59cordova.getActivity(), "android.permission.READ_EXTERNAL_STORAGE") != 0) {
            ActivityCompat.requestPermissions(this.f59cordova.getActivity(), new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 1);
            callbackContext.error("Please grant read external storage permission");
            return null;
        }
        try {
            String externalStorageState = Environment.getExternalStorageState();
            if (!externalStorageState.equals("mounted")) {
                if (!externalStorageState.equals("mounted_ro")) {
                    Log.e(LOG_TAG, "Storage directory is not mounted.  Please ensure the device is not connected via USB for file transfer");
                    callbackContext.error("Storage directory is returning not mounted");
                    return null;
                }
            }
            File file = new File(str.substring(str.indexOf(47) + 2, str.length()));
            if (!file.exists()) {
                Log.e(LOG_TAG, "File at path " + file.getPath() + " with name " + file.getName() + "does not exist");
                StringBuilder sb = new StringBuilder();
                sb.append("File not found: ");
                sb.append(file.toString());
                callbackContext.error(sb.toString());
                return null;
            }
            return FileProvider.getUriForFile(this.f59cordova.getActivity().getApplicationContext(), this.f59cordova.getActivity().getPackageName() + ".darryncampbell.cordova.plugin.intent.fileprovider", file);
        } catch (StringIndexOutOfBoundsException unused) {
            Log.e(LOG_TAG, "URL is not well formed");
            callbackContext.error("URL is not well formed");
            return null;
        }
    }

    private String getRealPathFromURI_API19(JSONObject jSONObject, CallbackContext callbackContext) throws JSONException {
        Uri parse = jSONObject.has("uri") ? Uri.parse(jSONObject.getString("uri")) : null;
        if (parse == null) {
            Log.w(LOG_TAG, "URI is not a specified parameter");
            throw new JSONException("URI is not a specified parameter");
        } else if (Build.VERSION.SDK_INT < 19) {
            return "Requires KK or higher";
        } else {
            String str = "";
            if (!parse.getHost().contains("com.android.providers.media")) {
                Cursor query = this.f59cordova.getActivity().getApplicationContext().getContentResolver().query(parse, new String[]{"_data"}, (String) null, (String[]) null, (String) null);
                int columnIndexOrThrow = query.getColumnIndexOrThrow("_data");
                query.moveToFirst();
                return query.getString(columnIndexOrThrow);
            } else if (ContextCompat.checkSelfPermission(this.f59cordova.getActivity(), "android.permission.READ_EXTERNAL_STORAGE") != 0) {
                ActivityCompat.requestPermissions(this.f59cordova.getActivity(), new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 1);
                callbackContext.error("Please grant read external storage permission");
                return null;
            } else {
                String[] strArr = {"_data"};
                Cursor query2 = this.f59cordova.getActivity().getApplicationContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, strArr, "_id=?", new String[]{DocumentsContract.getDocumentId(parse).split(":")[1]}, (String) null);
                int columnIndex = query2.getColumnIndex(strArr[0]);
                if (query2.moveToFirst()) {
                    str = query2.getString(columnIndex);
                }
                query2.close();
                return str;
            }
        }
    }

    private void startActivity(Intent intent, boolean z, int i, CallbackContext callbackContext) {
        if (intent.resolveActivityInfo(this.f59cordova.getActivity().getPackageManager(), 0) == null) {
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR));
        } else if (z) {
            this.f59cordova.setActivityResultCallback(this);
            this.f59cordova.getActivity().startActivityForResult(intent, i);
        } else {
            this.f59cordova.getActivity().startActivity(intent);
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK));
        }
    }

    private void sendBroadcast(Intent intent) {
        this.f59cordova.getActivity().sendBroadcast(intent);
    }

    private void startService(Intent intent) {
        this.f59cordova.getActivity().startService(intent);
    }

    private Intent populateIntent(JSONObject jSONObject, CallbackContext callbackContext) throws JSONException {
        Uri uri;
        String str;
        Bundle bundle;
        JSONObject jSONObject2 = jSONObject;
        CallbackContext callbackContext2 = callbackContext;
        String string = jSONObject2.has(Globalization.TYPE) ? jSONObject2.getString(Globalization.TYPE) : null;
        String string2 = jSONObject2.has("package") ? jSONObject2.getString("package") : null;
        CordovaResourceApi resourceApi = this.webView.getResourceApi();
        if (jSONObject2.has(ImagesContract.URL)) {
            String string3 = jSONObject2.getString(ImagesContract.URL);
            uri = (Build.VERSION.SDK_INT < 24 || !string3.startsWith("file://")) ? resourceApi.remapUri(Uri.parse(jSONObject2.getString(ImagesContract.URL))) : remapUriWithFileProvider(string3, callbackContext2);
        } else {
            uri = null;
        }
        JSONObject jSONObject3 = jSONObject2.has("extras") ? jSONObject2.getJSONObject("extras") : null;
        HashMap hashMap = new HashMap();
        if (jSONObject3 != null) {
            JSONArray names = jSONObject3.names();
            str = "";
            bundle = null;
            for (int i = 0; i < names.length(); i++) {
                String string4 = names.getString(i);
                if (jSONObject3.get(string4) instanceof JSONObject) {
                    str = string4;
                    bundle = toBundle((JSONObject) jSONObject3.get(string4));
                } else {
                    hashMap.put(string4, jSONObject3.get(string4));
                }
            }
        } else {
            str = "";
            bundle = null;
        }
        String string5 = jSONObject2.has("action") ? jSONObject2.getString("action") : null;
        Intent intent = new Intent();
        if (string5 != null) {
            intent.setAction(string5);
        }
        if (string == null || uri == null) {
            if (string != null) {
                intent.setType(string);
            }
            if (uri != null) {
                intent.setData(uri);
            }
        } else {
            intent.setDataAndType(uri, string);
        }
        JSONObject jSONObject4 = jSONObject2.has("component") ? jSONObject2.getJSONObject("component") : null;
        if (jSONObject4 != null) {
            String string6 = jSONObject4.has("package") ? jSONObject4.getString("package") : null;
            String string7 = jSONObject4.has("class") ? jSONObject4.getString("class") : null;
            if (string6 == null || string7 == null) {
                Log.w(LOG_TAG, "Component specified but missing corresponding package or class");
                throw new JSONException("Component specified but missing corresponding package or class");
            }
            intent.setComponent(new ComponentName(string6, string7));
        }
        if (string2 != null) {
            intent.setPackage(string2);
        }
        JSONArray jSONArray = jSONObject2.has("flags") ? jSONObject2.getJSONArray("flags") : null;
        if (jSONArray != null) {
            int length = jSONArray.length();
            for (int i2 = 0; i2 < length; i2++) {
                intent.addFlags(jSONArray.getInt(i2));
            }
        }
        if (bundle != null) {
            intent.putExtra(str, bundle);
        }
        for (String str2 : hashMap.keySet()) {
            Object obj = hashMap.get(str2);
            String valueOf = String.valueOf(obj);
            if (str2.equals("android.intent.extra.TEXT") && string.equals("text/html")) {
                intent.putExtra(str2, Html.fromHtml(valueOf));
            } else if (str2.equals("android.intent.extra.STREAM")) {
                if (Build.VERSION.SDK_INT < 24 || !valueOf.startsWith("file://")) {
                    intent.putExtra(str2, resourceApi.remapUri(Uri.parse(valueOf)));
                } else {
                    Uri remapUriWithFileProvider = remapUriWithFileProvider(valueOf, callbackContext2);
                    if (remapUriWithFileProvider != null) {
                        intent.putExtra(str2, remapUriWithFileProvider);
                    }
                }
            } else if (str2.equals("android.intent.extra.EMAIL")) {
                intent.putExtra("android.intent.extra.EMAIL", new String[]{valueOf});
            } else if (str2.equals("android.intent.extra.KEY_EVENT")) {
                JSONObject jSONObject5 = new JSONObject(valueOf);
                intent.putExtra("android.intent.extra.KEY_EVENT", new KeyEvent(jSONObject5.getInt("action"), jSONObject5.getInt("code")));
            } else if (obj instanceof Boolean) {
                intent.putExtra(str2, Boolean.valueOf(valueOf));
            } else if (obj instanceof Integer) {
                intent.putExtra(str2, Integer.valueOf(valueOf));
            } else if (obj instanceof Long) {
                intent.putExtra(str2, Long.valueOf(valueOf));
            } else if (obj instanceof Double) {
                intent.putExtra(str2, Double.valueOf(valueOf));
            } else {
                intent.putExtra(str2, valueOf);
            }
        }
        intent.addFlags(1);
        return jSONObject2.has("chooser") ? Intent.createChooser(intent, jSONObject2.getString("chooser")) : intent;
    }

    public void onNewIntent(Intent intent) {
        if (this.onNewIntentCallbackContext != null) {
            fireOnNewIntent(intent);
        } else {
            this.deferredIntent = intent;
        }
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (this.onActivityResultCallbackContext != null && intent != null) {
            intent.putExtra("requestCode", i);
            intent.putExtra("resultCode", i2);
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, getIntentJson(intent));
            pluginResult.setKeepCallback(true);
            this.onActivityResultCallbackContext.sendPluginResult(pluginResult);
        } else if (this.onActivityResultCallbackContext != null) {
            Intent intent2 = new Intent();
            intent2.putExtra("requestCode", i);
            intent2.putExtra("resultCode", i2);
            PluginResult pluginResult2 = new PluginResult(PluginResult.Status.OK, getIntentJson(intent2));
            pluginResult2.setKeepCallback(true);
            this.onActivityResultCallbackContext.sendPluginResult(pluginResult2);
        }
    }

    private void fireOnNewIntent(Intent intent) {
        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, getIntentJson(intent));
        pluginResult.setKeepCallback(true);
        this.onNewIntentCallbackContext.sendPluginResult(pluginResult);
    }

    /* access modifiers changed from: private */
    public JSONObject getIntentJson(Intent intent) {
        JSONObject[] jSONObjectArr;
        ClipData clipData;
        ContentResolver contentResolver = this.f59cordova.getActivity().getApplicationContext().getContentResolver();
        MimeTypeMap singleton = MimeTypeMap.getSingleton();
        if (Build.VERSION.SDK_INT < 19 || (clipData = intent.getClipData()) == null) {
            jSONObjectArr = null;
        } else {
            int itemCount = clipData.getItemCount();
            jSONObjectArr = new JSONObject[itemCount];
            for (int i = 0; i < itemCount; i++) {
                ClipData.Item itemAt = clipData.getItemAt(i);
                try {
                    jSONObjectArr[i] = new JSONObject();
                    jSONObjectArr[i].put("htmlText", itemAt.getHtmlText());
                    jSONObjectArr[i].put("intent", itemAt.getIntent());
                    jSONObjectArr[i].put(PushConstants.STYLE_TEXT, itemAt.getText());
                    jSONObjectArr[i].put("uri", itemAt.getUri());
                    if (itemAt.getUri() != null) {
                        String type = contentResolver.getType(itemAt.getUri());
                        String extensionFromMimeType = singleton.getExtensionFromMimeType(contentResolver.getType(itemAt.getUri()));
                        jSONObjectArr[i].put(Globalization.TYPE, type);
                        jSONObjectArr[i].put("extension", extensionFromMimeType);
                    }
                } catch (JSONException e) {
                    Log.d(LOG_TAG, " Error thrown during intent > JSON conversion");
                    Log.d(LOG_TAG, e.getMessage());
                    Log.d(LOG_TAG, Arrays.toString(e.getStackTrace()));
                }
            }
        }
        try {
            JSONObject jSONObject = new JSONObject();
            if (Build.VERSION.SDK_INT >= 19 && jSONObjectArr != null) {
                jSONObject.put("clipItems", new JSONArray(jSONObjectArr));
            }
            jSONObject.put(Globalization.TYPE, intent.getType());
            jSONObject.put("extras", toJsonObject(intent.getExtras()));
            jSONObject.put("action", intent.getAction());
            jSONObject.put("categories", intent.getCategories());
            jSONObject.put("flags", intent.getFlags());
            jSONObject.put("component", intent.getComponent());
            jSONObject.put(PushConstants.PARSE_COM_DATA, intent.getData());
            jSONObject.put("package", intent.getPackage());
            return jSONObject;
        } catch (JSONException e2) {
            Log.d(LOG_TAG, " Error thrown during intent > JSON conversion");
            Log.d(LOG_TAG, e2.getMessage());
            Log.d(LOG_TAG, Arrays.toString(e2.getStackTrace()));
            return null;
        }
    }

    private static JSONObject toJsonObject(Bundle bundle) {
        try {
            return (JSONObject) toJsonValue(bundle);
        } catch (JSONException e) {
            throw new IllegalArgumentException("Cannot convert bundle to JSON: " + e.getMessage(), e);
        }
    }

    private static Object toJsonValue(Object obj) throws JSONException {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Bundle) {
            Bundle bundle = (Bundle) obj;
            JSONObject jSONObject = new JSONObject();
            for (String str : bundle.keySet()) {
                jSONObject.put(str, toJsonValue(bundle.get(str)));
            }
            return jSONObject;
        }
        int i = 0;
        if (obj.getClass().isArray()) {
            JSONArray jSONArray = new JSONArray();
            int length = Array.getLength(obj);
            while (i < length) {
                jSONArray.put(i, toJsonValue(Array.get(obj, i)));
                i++;
            }
            return jSONArray;
        } else if (!(obj instanceof ArrayList)) {
            return ((obj instanceof String) || (obj instanceof Boolean) || (obj instanceof Integer) || (obj instanceof Long) || (obj instanceof Double)) ? obj : String.valueOf(obj);
        } else {
            ArrayList arrayList = (ArrayList) obj;
            JSONArray jSONArray2 = new JSONArray();
            while (i < arrayList.size()) {
                jSONArray2.put(toJsonValue(arrayList.get(i)));
                i++;
            }
            return jSONArray2;
        }
    }

    private Bundle toBundle(JSONObject jSONObject) {
        Bundle bundle = new Bundle();
        if (jSONObject == null) {
            return null;
        }
        try {
            Iterator<String> keys = jSONObject.keys();
            while (keys.hasNext()) {
                String next = keys.next();
                jSONObject.get(next);
                if (jSONObject.get(next) instanceof String) {
                    bundle.putString(next, jSONObject.getString(next));
                } else if (jSONObject.get(next) instanceof Boolean) {
                    bundle.putBoolean(next, jSONObject.getBoolean(next));
                } else if (jSONObject.get(next) instanceof Integer) {
                    bundle.putInt(next, jSONObject.getInt(next));
                } else if (jSONObject.get(next) instanceof Long) {
                    bundle.putLong(next, jSONObject.getLong(next));
                } else if (jSONObject.get(next) instanceof Double) {
                    bundle.putDouble(next, jSONObject.getDouble(next));
                } else {
                    if (!jSONObject.get(next).getClass().isArray()) {
                        if (!(jSONObject.get(next) instanceof JSONArray)) {
                            if (jSONObject.get(next) instanceof JSONObject) {
                                bundle.putBundle(next, toBundle((JSONObject) jSONObject.get(next)));
                            }
                        }
                    }
                    JSONArray jSONArray = jSONObject.getJSONArray(next);
                    int length = jSONArray.length();
                    int i = 0;
                    if (jSONArray.get(0) instanceof String) {
                        String[] strArr = new String[length];
                        while (i < length) {
                            strArr[i] = jSONArray.getString(i);
                            i++;
                        }
                        bundle.putStringArray(next, strArr);
                    } else if (next.equals("PLUGIN_CONFIG")) {
                        ArrayList arrayList = new ArrayList();
                        while (i < length) {
                            arrayList.add(toBundle(jSONArray.getJSONObject(i)));
                            i++;
                        }
                        bundle.putParcelableArrayList(next, arrayList);
                    } else {
                        Bundle[] bundleArr = new Bundle[length];
                        while (i < length) {
                            bundleArr[i] = toBundle(jSONArray.getJSONObject(i));
                            i++;
                        }
                        bundle.putParcelableArray(next, bundleArr);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bundle;
    }
}
