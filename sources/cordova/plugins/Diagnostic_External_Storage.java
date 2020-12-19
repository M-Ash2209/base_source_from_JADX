package cordova.plugins;

import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.p000v4.p002os.EnvironmentCompat;
import android.util.Log;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.globalization.Globalization;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Diagnostic_External_Storage extends CordovaPlugin {
    public static final String TAG = "Diagnostic_External_Storage";
    protected static String externalStoragePermission = "READ_EXTERNAL_STORAGE";
    public static Diagnostic_External_Storage instance;
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
            if (str.equals("getExternalSdCardDetails")) {
                getExternalSdCardDetails();
                return true;
            }
            this.diagnostic.handleError("Invalid action");
            return false;
        } catch (Exception e) {
            this.diagnostic.handleError("Exception occurred: ".concat(e.getMessage()));
            return false;
        }
    }

    public static void onReceivePermissionResult() throws JSONException {
        instance._getExternalSdCardDetails();
    }

    /* access modifiers changed from: protected */
    public void getExternalSdCardDetails() throws Exception {
        Diagnostic diagnostic2 = this.diagnostic;
        String str = Diagnostic.permissionsMap.get(externalStoragePermission);
        if (this.diagnostic.hasPermission(str)) {
            _getExternalSdCardDetails();
        } else {
            this.diagnostic.requestRuntimePermission(str, Diagnostic.GET_EXTERNAL_SD_CARD_DETAILS_PERMISSION_REQUEST.intValue());
        }
    }

    /* access modifiers changed from: protected */
    public void _getExternalSdCardDetails() throws JSONException {
        String[] storageDirectories = getStorageDirectories();
        JSONArray jSONArray = new JSONArray();
        for (String str : storageDirectories) {
            File file = new File(str);
            JSONObject jSONObject = new JSONObject();
            if (file.canRead()) {
                jSONObject.put("path", str);
                jSONObject.put("filePath", "file://" + str);
                jSONObject.put("canWrite", file.canWrite());
                jSONObject.put("freeSpace", getFreeSpaceInBytes(str));
                if (str.contains("Android")) {
                    jSONObject.put(Globalization.TYPE, "application");
                } else {
                    jSONObject.put(Globalization.TYPE, "root");
                }
                jSONArray.put(jSONObject);
            }
        }
        this.currentContext.success(jSONArray);
    }

    /* access modifiers changed from: protected */
    public long getFreeSpaceInBytes(String str) {
        try {
            StatFs statFs = new StatFs(str);
            return ((long) statFs.getAvailableBlocks()) * ((long) statFs.getBlockSize());
        } catch (IllegalArgumentException unused) {
            return 0;
        }
    }

    /* access modifiers changed from: protected */
    public String[] getStorageDirectories() {
        boolean z;
        ArrayList arrayList = new ArrayList();
        if (Build.VERSION.SDK_INT >= 19) {
            for (File file : this.f59cordova.getActivity().getApplicationContext().getExternalFilesDirs((String) null)) {
                if (file != null) {
                    String path = file.getPath();
                    String str = path.split("/Android")[0];
                    if (Build.VERSION.SDK_INT >= 21) {
                        z = Environment.isExternalStorageRemovable(file);
                    } else {
                        z = "mounted".equals(EnvironmentCompat.getStorageState(file));
                    }
                    if (z) {
                        arrayList.add(str);
                        arrayList.add(path);
                    }
                }
            }
        }
        if (arrayList.isEmpty()) {
            String str2 = "";
            try {
                Process start = new ProcessBuilder(new String[0]).command(new String[]{"mount | grep /dev/block/vold"}).redirectErrorStream(true).start();
                start.waitFor();
                InputStream inputStream = start.getInputStream();
                byte[] bArr = new byte[1024];
                while (inputStream.read(bArr) != -1) {
                    str2 = str2 + new String(bArr);
                }
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!str2.trim().isEmpty()) {
                for (String split : str2.split("\n")) {
                    arrayList.add(split.split(" ")[2]);
                }
            }
        }
        if (Build.VERSION.SDK_INT >= 23) {
            int i = 0;
            while (i < arrayList.size()) {
                if (!((String) arrayList.get(i)).toLowerCase().matches(".*[0-9a-f]{4}[-][0-9a-f]{4}.*")) {
                    this.diagnostic.logDebug(((String) arrayList.get(i)) + " might not be extSDcard");
                    arrayList.remove(i);
                    i += -1;
                }
                i++;
            }
        } else {
            int i2 = 0;
            while (i2 < arrayList.size()) {
                if (!((String) arrayList.get(i2)).toLowerCase().contains("ext") && !((String) arrayList.get(i2)).toLowerCase().contains("sdcard")) {
                    this.diagnostic.logDebug(((String) arrayList.get(i2)) + " might not be extSDcard");
                    arrayList.remove(i2);
                    i2 += -1;
                }
                i2++;
            }
        }
        String[] strArr = new String[arrayList.size()];
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            strArr[i3] = (String) arrayList.get(i3);
        }
        return strArr;
    }
}
