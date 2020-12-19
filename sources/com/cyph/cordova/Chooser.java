package com.cyph.cordova;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.p000v4.internal.view.SupportMenu;
import android.util.Base64;
import com.adobe.phonegap.push.PushConstants;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Chooser extends CordovaPlugin {
    private static final String ACTION_OPEN = "getFile";
    private static final int PICK_FILE_REQUEST = 1;
    private static final String TAG = "Chooser";
    private CallbackContext callback;
    private Boolean includeData;

    public static byte[] getBytesFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bArr = new byte[SupportMenu.USER_MASK];
        while (true) {
            int read = inputStream.read(bArr);
            if (read == -1) {
                return byteArrayOutputStream.toByteArray();
            }
            byteArrayOutputStream.write(bArr, 0, read);
        }
    }

    public static String getDisplayName(ContentResolver contentResolver, Uri uri) {
        Cursor query = contentResolver.query(uri, new String[]{"_display_name"}, (String) null, (String[]) null, (String) null);
        if (query == null) {
            return "File";
        }
        try {
            if (query.moveToFirst()) {
                return query.getString(0);
            }
            query.close();
            return "File";
        } finally {
            query.close();
        }
    }

    public void chooseFile(CallbackContext callbackContext, String str, Boolean bool) {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("*/*");
        if (!str.equals("*/*")) {
            intent.putExtra("android.intent.extra.MIME_TYPES", str.split(","));
        }
        intent.addCategory("android.intent.category.OPENABLE");
        intent.putExtra("android.intent.extra.ALLOW_MULTIPLE", false);
        intent.putExtra("android.intent.extra.LOCAL_ONLY", true);
        this.includeData = bool;
        this.f59cordova.startActivityForResult(this, Intent.createChooser(intent, "Select File"), 1);
        PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
        pluginResult.setKeepCallback(true);
        this.callback = callbackContext;
        callbackContext.sendPluginResult(pluginResult);
    }

    public boolean execute(String str, JSONArray jSONArray, CallbackContext callbackContext) {
        try {
            if (str.equals(ACTION_OPEN)) {
                chooseFile(callbackContext, jSONArray.getString(0), Boolean.valueOf(jSONArray.getBoolean(1)));
                return true;
            }
        } catch (JSONException e) {
            CallbackContext callbackContext2 = this.callback;
            callbackContext2.error("Execute failed: " + e.toString());
        }
        return false;
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 1) {
            try {
                if (this.callback == null) {
                    return;
                }
                if (i2 == -1) {
                    Uri data = intent.getData();
                    if (data != null) {
                        ContentResolver contentResolver = this.f59cordova.getActivity().getContentResolver();
                        String displayName = getDisplayName(contentResolver, data);
                        String type = contentResolver.getType(data);
                        if (type == null || type.isEmpty()) {
                            type = "application/octet-stream";
                        }
                        String str = "";
                        if (this.includeData.booleanValue()) {
                            str = Base64.encodeToString(getBytesFromInputStream(contentResolver.openInputStream(data)), 0);
                        }
                        JSONObject jSONObject = new JSONObject();
                        jSONObject.put(PushConstants.PARSE_COM_DATA, str);
                        jSONObject.put("mediaType", type);
                        jSONObject.put("name", displayName);
                        jSONObject.put("uri", data.toString());
                        this.callback.success(jSONObject.toString());
                        return;
                    }
                    this.callback.error("File URI was null.");
                } else if (i2 == 0) {
                    this.callback.success("RESULT_CANCELED");
                } else {
                    this.callback.error(i2);
                }
            } catch (Exception e) {
                CallbackContext callbackContext = this.callback;
                callbackContext.error("Failed to read file: " + e.toString());
            }
        }
    }
}
