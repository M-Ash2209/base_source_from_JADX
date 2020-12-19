package cordova.plugins;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.p000v4.app.ActivityCompat;
import android.support.p000v4.app.NotificationCompat;
import android.util.Log;
import com.adobe.phonegap.push.PushConstants;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Diagnostic extends CordovaPlugin {
    public static final String CPU_ARCH_ARMv6 = "ARMv6";
    public static final String CPU_ARCH_ARMv7 = "ARMv7";
    public static final String CPU_ARCH_ARMv8 = "ARMv8";
    public static final String CPU_ARCH_MIPS = "MIPS";
    public static final String CPU_ARCH_MIPS_64 = "MIPS_64";
    public static final String CPU_ARCH_UNKNOWN = "unknown";
    public static final String CPU_ARCH_X86 = "X86";
    public static final String CPU_ARCH_X86_64 = "X86_64";
    protected static final Integer GET_EXTERNAL_SD_CARD_DETAILS_PERMISSION_REQUEST = 1000;
    protected static final String STATUS_DENIED_ALWAYS = "DENIED_ALWAYS";
    protected static final String STATUS_DENIED_ONCE = "DENIED_ONCE";
    protected static final String STATUS_GRANTED = "GRANTED";
    protected static final String STATUS_NOT_REQUESTED = "NOT_REQUESTED";
    public static final String TAG = "Diagnostic";
    protected static final String externalStorageClassName = "cordova.plugins.Diagnostic_External_Storage";
    public static Diagnostic instance = null;
    protected static final Map<String, String> permissionsMap;
    protected Context applicationContext;
    protected HashMap<String, CallbackContext> callbackContexts = new HashMap<>();
    protected CallbackContext currentContext;
    boolean debugEnabled = false;
    protected SharedPreferences.Editor editor;
    protected HashMap<String, JSONObject> permissionStatuses = new HashMap<>();
    protected SharedPreferences sharedPref;

    static {
        HashMap hashMap = new HashMap();
        addBiDirMapEntry(hashMap, "READ_CALENDAR", "android.permission.READ_CALENDAR");
        addBiDirMapEntry(hashMap, "WRITE_CALENDAR", "android.permission.WRITE_CALENDAR");
        addBiDirMapEntry(hashMap, "CAMERA", "android.permission.CAMERA");
        addBiDirMapEntry(hashMap, "READ_CONTACTS", "android.permission.READ_CONTACTS");
        addBiDirMapEntry(hashMap, "WRITE_CONTACTS", "android.permission.WRITE_CONTACTS");
        addBiDirMapEntry(hashMap, "GET_ACCOUNTS", "android.permission.GET_ACCOUNTS");
        addBiDirMapEntry(hashMap, "ACCESS_FINE_LOCATION", "android.permission.ACCESS_FINE_LOCATION");
        addBiDirMapEntry(hashMap, "ACCESS_COARSE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION");
        addBiDirMapEntry(hashMap, "ACCESS_BACKGROUND_LOCATION", "android.permission.ACCESS_BACKGROUND_LOCATION");
        addBiDirMapEntry(hashMap, "RECORD_AUDIO", "android.permission.RECORD_AUDIO");
        addBiDirMapEntry(hashMap, "READ_PHONE_STATE", "android.permission.READ_PHONE_STATE");
        addBiDirMapEntry(hashMap, "CALL_PHONE", "android.permission.CALL_PHONE");
        addBiDirMapEntry(hashMap, "ADD_VOICEMAIL", "com.android.voicemail.permission.ADD_VOICEMAIL");
        addBiDirMapEntry(hashMap, "USE_SIP", "android.permission.USE_SIP");
        addBiDirMapEntry(hashMap, "PROCESS_OUTGOING_CALLS", "android.permission.PROCESS_OUTGOING_CALLS");
        addBiDirMapEntry(hashMap, "SEND_SMS", "android.permission.SEND_SMS");
        addBiDirMapEntry(hashMap, "RECEIVE_SMS", "android.permission.RECEIVE_SMS");
        addBiDirMapEntry(hashMap, "READ_SMS", "android.permission.READ_SMS");
        addBiDirMapEntry(hashMap, "RECEIVE_WAP_PUSH", "android.permission.RECEIVE_WAP_PUSH");
        addBiDirMapEntry(hashMap, "RECEIVE_MMS", "android.permission.RECEIVE_MMS");
        addBiDirMapEntry(hashMap, "WRITE_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE");
        addBiDirMapEntry(hashMap, "READ_CALL_LOG", "android.permission.READ_CALL_LOG");
        addBiDirMapEntry(hashMap, "WRITE_CALL_LOG", "android.permission.WRITE_CALL_LOG");
        addBiDirMapEntry(hashMap, "READ_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE");
        addBiDirMapEntry(hashMap, "BODY_SENSORS", "android.permission.BODY_SENSORS");
        permissionsMap = Collections.unmodifiableMap(hashMap);
    }

    public static Diagnostic getInstance() {
        return instance;
    }

    public void initialize(CordovaInterface cordovaInterface, CordovaWebView cordovaWebView) {
        Log.d(TAG, "initialize()");
        instance = this;
        this.applicationContext = this.f59cordova.getActivity().getApplicationContext();
        this.sharedPref = cordovaInterface.getActivity().getSharedPreferences(TAG, 0);
        this.editor = this.sharedPref.edit();
        super.initialize(cordovaInterface, cordovaWebView);
    }

    public boolean execute(String str, JSONArray jSONArray, CallbackContext callbackContext) throws JSONException {
        this.currentContext = callbackContext;
        try {
            if (str.equals("enableDebug")) {
                this.debugEnabled = true;
                logDebug("Debug enabled");
                callbackContext.success();
            } else if (str.equals("switchToSettings")) {
                switchToAppSettings();
                callbackContext.success();
            } else if (str.equals("switchToMobileDataSettings")) {
                switchToMobileDataSettings();
                callbackContext.success();
            } else if (str.equals("switchToWirelessSettings")) {
                switchToWirelessSettings();
                callbackContext.success();
            } else if (str.equals("isDataRoamingEnabled")) {
                callbackContext.success(isDataRoamingEnabled() ? 1 : 0);
            } else if (str.equals("getPermissionAuthorizationStatus")) {
                getPermissionAuthorizationStatus(jSONArray);
            } else if (str.equals("getPermissionsAuthorizationStatus")) {
                getPermissionsAuthorizationStatus(jSONArray);
            } else if (str.equals("requestRuntimePermission")) {
                requestRuntimePermission(jSONArray);
            } else if (str.equals("requestRuntimePermissions")) {
                requestRuntimePermissions(jSONArray);
            } else if (str.equals("isADBModeEnabled")) {
                callbackContext.success(isADBModeEnabled() ? 1 : 0);
            } else if (str.equals("isDeviceRooted")) {
                callbackContext.success(isDeviceRooted() ? 1 : 0);
            } else if (str.equals("restart")) {
                restart(jSONArray);
            } else if (str.equals("getArchitecture")) {
                callbackContext.success(getCPUArchitecture());
            } else {
                handleError("Invalid action");
                return false;
            }
            return true;
        } catch (Exception e) {
            handleError("Exception occurred: ".concat(e.getMessage()));
            return false;
        }
    }

    public void restart(JSONArray jSONArray) throws Exception {
        if (jSONArray.getBoolean(0)) {
            doColdRestart();
        } else {
            doWarmRestart();
        }
    }

    public boolean isDataRoamingEnabled() throws Exception {
        if (Build.VERSION.SDK_INT < 17) {
            if (Settings.System.getInt(this.f59cordova.getActivity().getContentResolver(), "data_roaming", 0) == 1) {
                return true;
            }
            return false;
        } else if (Settings.Global.getInt(this.f59cordova.getActivity().getContentResolver(), "data_roaming", 0) == 1) {
            return true;
        } else {
            return false;
        }
    }

    public void switchToAppSettings() {
        logDebug("Switch to App Settings");
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", this.f59cordova.getActivity().getPackageName(), (String) null));
        this.f59cordova.getActivity().startActivity(intent);
    }

    public void switchToMobileDataSettings() {
        logDebug("Switch to Mobile Data Settings");
        this.f59cordova.getActivity().startActivity(new Intent("android.settings.DATA_ROAMING_SETTINGS"));
    }

    public void switchToWirelessSettings() {
        logDebug("Switch to wireless Settings");
        this.f59cordova.getActivity().startActivity(new Intent("android.settings.WIRELESS_SETTINGS"));
    }

    public void getPermissionsAuthorizationStatus(JSONArray jSONArray) throws Exception {
        this.currentContext.success(_getPermissionsAuthorizationStatus(jsonArrayToStringArray(jSONArray.getJSONArray(0))));
    }

    public void getPermissionAuthorizationStatus(JSONArray jSONArray) throws Exception {
        String string = jSONArray.getString(0);
        JSONArray jSONArray2 = new JSONArray();
        jSONArray2.put(string);
        this.currentContext.success(_getPermissionsAuthorizationStatus(jsonArrayToStringArray(jSONArray2)).getString(string));
    }

    public void requestRuntimePermissions(JSONArray jSONArray) throws Exception {
        _requestRuntimePermissions(jSONArray.getJSONArray(0), storeContextByRequestId());
    }

    public void requestRuntimePermission(JSONArray jSONArray) throws Exception {
        requestRuntimePermission(jSONArray.getString(0));
    }

    public void requestRuntimePermission(String str) throws Exception {
        requestRuntimePermission(str, storeContextByRequestId());
    }

    public void requestRuntimePermission(String str, int i) throws Exception {
        JSONArray jSONArray = new JSONArray();
        jSONArray.put(str);
        _requestRuntimePermissions(jSONArray, i);
    }

    public int getADBMode() {
        if (Build.VERSION.SDK_INT >= 17) {
            return Settings.Global.getInt(this.applicationContext.getContentResolver(), "adb_enabled", 0);
        }
        return Settings.Secure.getInt(this.applicationContext.getContentResolver(), "adb_enabled", 0);
    }

    public boolean isADBModeEnabled() {
        boolean z = false;
        try {
            if (getADBMode() == 1) {
                z = true;
            }
        } catch (Exception e) {
            logError(e.getMessage());
        }
        logDebug("ADB mode enabled: " + z);
        return z;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:22:0x006d, code lost:
        if (r2 != null) goto L_0x007c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x007a, code lost:
        if (r2 == null) goto L_0x007f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x007c, code lost:
        r2.destroy();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x007f, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isDeviceRooted() {
        /*
            r11 = this;
            java.lang.String r0 = android.os.Build.TAGS
            r1 = 1
            if (r0 == 0) goto L_0x000e
            java.lang.String r2 = "test-keys"
            boolean r0 = r0.contains(r2)
            if (r0 == 0) goto L_0x000e
            return r1
        L_0x000e:
            r0 = 0
            java.lang.String r2 = "/system/app/Superuser.apk"
            java.lang.String r3 = "/sbin/su"
            java.lang.String r4 = "/system/bin/su"
            java.lang.String r5 = "/system/xbin/su"
            java.lang.String r6 = "/data/local/xbin/su"
            java.lang.String r7 = "/data/local/bin/su"
            java.lang.String r8 = "/system/sd/xbin/su"
            java.lang.String r9 = "/system/bin/failsafe/su"
            java.lang.String r10 = "/data/local/su"
            java.lang.String[] r2 = new java.lang.String[]{r2, r3, r4, r5, r6, r7, r8, r9, r10}     // Catch:{ Exception -> 0x003a }
            int r3 = r2.length     // Catch:{ Exception -> 0x003a }
            r4 = 0
        L_0x0027:
            if (r4 >= r3) goto L_0x0042
            r5 = r2[r4]     // Catch:{ Exception -> 0x003a }
            java.io.File r6 = new java.io.File     // Catch:{ Exception -> 0x003a }
            r6.<init>(r5)     // Catch:{ Exception -> 0x003a }
            boolean r5 = r6.exists()     // Catch:{ Exception -> 0x003a }
            if (r5 == 0) goto L_0x0037
            return r1
        L_0x0037:
            int r4 = r4 + 1
            goto L_0x0027
        L_0x003a:
            r2 = move-exception
            java.lang.String r2 = r2.getMessage()
            r11.logDebug(r2)
        L_0x0042:
            r2 = 0
            java.lang.Runtime r3 = java.lang.Runtime.getRuntime()     // Catch:{ Exception -> 0x0072 }
            java.lang.String r4 = "/system/xbin/which"
            java.lang.String r5 = "su"
            java.lang.String[] r4 = new java.lang.String[]{r4, r5}     // Catch:{ Exception -> 0x0072 }
            java.lang.Process r2 = r3.exec(r4)     // Catch:{ Exception -> 0x0072 }
            java.io.BufferedReader r3 = new java.io.BufferedReader     // Catch:{ Exception -> 0x0072 }
            java.io.InputStreamReader r4 = new java.io.InputStreamReader     // Catch:{ Exception -> 0x0072 }
            java.io.InputStream r5 = r2.getInputStream()     // Catch:{ Exception -> 0x0072 }
            r4.<init>(r5)     // Catch:{ Exception -> 0x0072 }
            r3.<init>(r4)     // Catch:{ Exception -> 0x0072 }
            java.lang.String r3 = r3.readLine()     // Catch:{ Exception -> 0x0072 }
            if (r3 == 0) goto L_0x006d
            if (r2 == 0) goto L_0x006c
            r2.destroy()
        L_0x006c:
            return r1
        L_0x006d:
            if (r2 == 0) goto L_0x007f
            goto L_0x007c
        L_0x0070:
            r0 = move-exception
            goto L_0x0080
        L_0x0072:
            r1 = move-exception
            java.lang.String r1 = r1.getMessage()     // Catch:{ all -> 0x0070 }
            r11.logDebug(r1)     // Catch:{ all -> 0x0070 }
            if (r2 == 0) goto L_0x007f
        L_0x007c:
            r2.destroy()
        L_0x007f:
            return r0
        L_0x0080:
            if (r2 == 0) goto L_0x0085
            r2.destroy()
        L_0x0085:
            throw r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: cordova.plugins.Diagnostic.isDeviceRooted():boolean");
    }

    public void logDebug(String str) {
        if (this.debugEnabled) {
            Log.d(TAG, str);
            executeGlobalJavascript("console.log(\"Diagnostic[native]: " + escapeDoubleQuotes(str) + "\")");
        }
    }

    public void logInfo(String str) {
        Log.i(TAG, str);
        if (this.debugEnabled) {
            executeGlobalJavascript("console.info(\"Diagnostic[native]: " + escapeDoubleQuotes(str) + "\")");
        }
    }

    public void logWarning(String str) {
        Log.w(TAG, str);
        if (this.debugEnabled) {
            executeGlobalJavascript("console.warn(\"Diagnostic[native]: " + escapeDoubleQuotes(str) + "\")");
        }
    }

    public void logError(String str) {
        Log.e(TAG, str);
        if (this.debugEnabled) {
            executeGlobalJavascript("console.error(\"Diagnostic[native]: " + escapeDoubleQuotes(str) + "\")");
        }
    }

    public String escapeDoubleQuotes(String str) {
        return str.replace("\"", "\\\"").replace("%22", "\\%22");
    }

    public void handleError(String str, CallbackContext callbackContext) {
        try {
            logError(str);
            callbackContext.error(str);
        } catch (Exception e) {
            logError(e.toString());
        }
    }

    public void handleError(String str) {
        handleError(str, this.currentContext);
    }

    public void handleError(String str, int i) {
        CallbackContext callbackContext;
        String valueOf = String.valueOf(i);
        if (this.callbackContexts.containsKey(valueOf)) {
            callbackContext = this.callbackContexts.get(valueOf);
        } else {
            callbackContext = this.currentContext;
        }
        handleError(str, callbackContext);
        clearRequest(i);
    }

    /* access modifiers changed from: protected */
    public JSONObject _getPermissionsAuthorizationStatus(String[] strArr) throws Exception {
        JSONObject jSONObject = new JSONObject();
        int i = 0;
        while (i < strArr.length) {
            String str = strArr[i];
            if (permissionsMap.containsKey(str)) {
                String str2 = permissionsMap.get(str);
                Log.v(TAG, "Get authorisation status for " + str2);
                if (hasPermission(str2)) {
                    jSONObject.put(str, STATUS_GRANTED);
                } else if (shouldShowRequestPermissionRationale(this.f59cordova.getActivity(), str2)) {
                    jSONObject.put(str, STATUS_DENIED_ONCE);
                } else if (isPermissionRequested(str)) {
                    jSONObject.put(str, STATUS_DENIED_ALWAYS);
                } else if (Build.VERSION.SDK_INT >= 29 || !str.equals("ACCESS_BACKGROUND_LOCATION")) {
                    jSONObject.put(str, STATUS_NOT_REQUESTED);
                } else {
                    jSONObject.put(str, STATUS_GRANTED);
                }
                i++;
            } else {
                throw new Exception("Permission name '" + str + "' is not a valid permission");
            }
        }
        return jSONObject;
    }

    /* access modifiers changed from: protected */
    public void _requestRuntimePermissions(JSONArray jSONArray, int i) throws Exception {
        JSONObject _getPermissionsAuthorizationStatus = _getPermissionsAuthorizationStatus(jsonArrayToStringArray(jSONArray));
        JSONArray jSONArray2 = new JSONArray();
        for (int i2 = 0; i2 < _getPermissionsAuthorizationStatus.names().length(); i2++) {
            String string = _getPermissionsAuthorizationStatus.names().getString(i2);
            if (_getPermissionsAuthorizationStatus.getString(string) == STATUS_GRANTED) {
                Log.d(TAG, "Permission already granted for " + string);
                JSONObject jSONObject = this.permissionStatuses.get(String.valueOf(i));
                jSONObject.put(string, STATUS_GRANTED);
                this.permissionStatuses.put(String.valueOf(i), jSONObject);
            } else {
                String str = permissionsMap.get(string);
                Log.d(TAG, "Requesting permission for " + str);
                jSONArray2.put(str);
            }
        }
        if (jSONArray2.length() > 0) {
            Log.v(TAG, "Requesting permissions");
            requestPermissions(this, i, jsonArrayToStringArray(jSONArray2));
            return;
        }
        Log.d(TAG, "No permissions to request: returning result");
        sendRuntimeRequestResult(i);
    }

    /* access modifiers changed from: protected */
    public void sendRuntimeRequestResult(int i) {
        String valueOf = String.valueOf(i);
        Log.v(TAG, "Sending runtime request result for id=" + valueOf);
        this.callbackContexts.get(valueOf).success(this.permissionStatuses.get(valueOf));
    }

    /* access modifiers changed from: protected */
    public int storeContextByRequestId() {
        return storeContextByRequestId(this.currentContext);
    }

    /* access modifiers changed from: protected */
    public int storeContextByRequestId(CallbackContext callbackContext) {
        String generateRandomRequestId = generateRandomRequestId();
        this.callbackContexts.put(generateRandomRequestId, callbackContext);
        this.permissionStatuses.put(generateRandomRequestId, new JSONObject());
        return Integer.valueOf(generateRandomRequestId).intValue();
    }

    /* access modifiers changed from: protected */
    public String generateRandomRequestId() {
        String str = null;
        while (str == null) {
            str = generateRandom();
            if (this.callbackContexts.containsKey(str)) {
                str = null;
            }
        }
        return str;
    }

    /* access modifiers changed from: protected */
    public String generateRandom() {
        return Integer.toString(new Random().nextInt(1000000) + 1);
    }

    /* access modifiers changed from: protected */
    public String[] jsonArrayToStringArray(JSONArray jSONArray) throws JSONException {
        if (jSONArray == null) {
            return null;
        }
        String[] strArr = new String[jSONArray.length()];
        for (int i = 0; i < strArr.length; i++) {
            strArr[i] = jSONArray.optString(i);
        }
        return strArr;
    }

    /* access modifiers changed from: protected */
    public CallbackContext getContextById(String str) throws Exception {
        if (this.callbackContexts.containsKey(str)) {
            return this.callbackContexts.get(str);
        }
        throw new Exception("No context found for request id=" + str);
    }

    /* access modifiers changed from: protected */
    public void clearRequest(int i) {
        String valueOf = String.valueOf(i);
        if (this.callbackContexts.containsKey(valueOf)) {
            this.callbackContexts.remove(valueOf);
            this.permissionStatuses.remove(valueOf);
        }
    }

    protected static void addBiDirMapEntry(Map map, Object obj, Object obj2) {
        map.put(obj, obj2);
        map.put(obj2, obj);
    }

    /* access modifiers changed from: protected */
    public boolean hasPermission(String str) throws Exception {
        try {
            return ((Boolean) this.f59cordova.getClass().getMethod(PushConstants.HAS_PERMISSION, new Class[]{str.getClass()}).invoke(this.f59cordova, new Object[]{str})).booleanValue();
        } catch (NoSuchMethodException unused) {
            logWarning("Cordova v8.1.0 does not support runtime permissions so defaulting to GRANTED for " + str);
            return true;
        }
    }

    /* access modifiers changed from: protected */
    public void requestPermissions(CordovaPlugin cordovaPlugin, int i, String[] strArr) throws Exception {
        try {
            this.f59cordova.getClass().getMethod("requestPermissions", new Class[]{CordovaPlugin.class, Integer.TYPE, String[].class}).invoke(this.f59cordova, new Object[]{cordovaPlugin, Integer.valueOf(i), strArr});
            for (String str : strArr) {
                setPermissionRequested(permissionsMap.get(str));
            }
        } catch (NoSuchMethodException unused) {
            throw new Exception("requestPermissions() method not found in CordovaInterface implementation of Cordova v8.1.0");
        }
    }

    /* access modifiers changed from: protected */
    public boolean shouldShowRequestPermissionRationale(Activity activity, String str) throws Exception {
        try {
            return ((Boolean) ActivityCompat.class.getMethod("shouldShowRequestPermissionRationale", new Class[]{Activity.class, String.class}).invoke((Object) null, new Object[]{activity, str})).booleanValue();
        } catch (NoSuchMethodException unused) {
            throw new Exception("shouldShowRequestPermissionRationale() method not found in ActivityCompat class. Check you have Android Support Library v23+ installed");
        }
    }

    public void executeGlobalJavascript(final String str) {
        this.f59cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                CordovaWebView cordovaWebView = Diagnostic.this.webView;
                cordovaWebView.loadUrl("javascript:" + str);
            }
        });
    }

    public void executePluginJavascript(String str) {
        executeGlobalJavascript("cordova.plugins.diagnostic." + str);
    }

    /* access modifiers changed from: protected */
    public void doWarmRestart() {
        this.f59cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                try {
                    Diagnostic.this.logInfo("Warm restarting main activity");
                    Diagnostic.instance.f59cordova.getActivity().recreate();
                } catch (Exception e) {
                    Diagnostic diagnostic = Diagnostic.this;
                    diagnostic.handleError("Unable to warm restart main activity: " + e.getMessage());
                }
            }
        });
    }

    /* access modifiers changed from: protected */
    public void doColdRestart() {
        try {
            logInfo("Cold restarting application");
            Context context = this.applicationContext;
            if (context != null) {
                PackageManager packageManager = context.getPackageManager();
                if (packageManager != null) {
                    Intent launchIntentForPackage = packageManager.getLaunchIntentForPackage(context.getPackageName());
                    if (launchIntentForPackage != null) {
                        ((AlarmManager) context.getSystemService(NotificationCompat.CATEGORY_ALARM)).set(1, System.currentTimeMillis() + 100, PendingIntent.getActivity(context, 223344, launchIntentForPackage, 268435456));
                        Log.i(TAG, "Killing application for cold restart");
                        System.exit(0);
                        return;
                    }
                    handleError("Unable to cold restart application: " + "StartActivity is null");
                    return;
                }
                handleError("Unable to cold restart application: " + "PackageManager is null");
                return;
            }
            handleError("Unable to cold restart application: " + "Context is null");
        } catch (Exception e) {
            handleError("Unable to cold restart application: " + e.getMessage());
        }
    }

    /* access modifiers changed from: protected */
    public String getCPUArchitecture() {
        String str;
        if (Build.VERSION.SDK_INT < 21) {
            str = Build.CPU_ABI;
        } else {
            str = Build.SUPPORTED_ABIS[0];
        }
        if (str == "armeabi") {
            return CPU_ARCH_ARMv6;
        }
        if (str.equals("armeabi-v7a")) {
            return CPU_ARCH_ARMv7;
        }
        if (str.equals("arm64-v8a")) {
            return CPU_ARCH_ARMv8;
        }
        if (str.equals("x86")) {
            return CPU_ARCH_X86;
        }
        if (str.equals("x86_64")) {
            return CPU_ARCH_X86_64;
        }
        if (str.equals("mips")) {
            return CPU_ARCH_MIPS;
        }
        if (str.equals("mips64")) {
            return CPU_ARCH_MIPS_64;
        }
        return "unknown";
    }

    /* access modifiers changed from: protected */
    public void setPermissionRequested(String str) {
        this.editor.putBoolean(str, true);
        if (!this.editor.commit()) {
            handleError("Failed to set permission requested flag for " + str);
        }
    }

    /* access modifiers changed from: protected */
    public boolean isPermissionRequested(String str) {
        return this.sharedPref.getBoolean(str, false);
    }

    public void onRequestPermissionResult(int i, String[] strArr, int[] iArr) throws JSONException {
        Class<?> cls;
        String str;
        String valueOf = String.valueOf(i);
        Log.v(TAG, "Received result for permissions request id=" + valueOf);
        try {
            CallbackContext contextById = getContextById(valueOf);
            JSONObject jSONObject = this.permissionStatuses.get(valueOf);
            int length = strArr.length;
            for (int i2 = 0; i2 < length; i2++) {
                String str2 = strArr[i2];
                String str3 = permissionsMap.get(str2);
                if (iArr[i2] == -1) {
                    str = !shouldShowRequestPermissionRationale(this.f59cordova.getActivity(), str2) ? isPermissionRequested(str3) ? STATUS_DENIED_ALWAYS : (Build.VERSION.SDK_INT >= 29 || !str3.equals("ACCESS_BACKGROUND_LOCATION")) ? STATUS_NOT_REQUESTED : STATUS_GRANTED : STATUS_DENIED_ONCE;
                } else {
                    str = STATUS_GRANTED;
                }
                jSONObject.put(str3, str);
                Log.v(TAG, "Authorisation for " + str3 + " is " + jSONObject.get(str3));
                clearRequest(i);
            }
            try {
                cls = Class.forName(externalStorageClassName);
            } catch (ClassNotFoundException unused) {
                cls = null;
            }
            if (i != GET_EXTERNAL_SD_CARD_DETAILS_PERMISSION_REQUEST.intValue() || cls == null) {
                contextById.success(jSONObject);
            } else {
                cls.getMethod("onReceivePermissionResult", new Class[0]).invoke((Object) null, new Object[0]);
            }
        } catch (Exception e) {
            handleError("Exception occurred onRequestPermissionsResult: ".concat(e.getMessage()), i);
        }
    }
}
