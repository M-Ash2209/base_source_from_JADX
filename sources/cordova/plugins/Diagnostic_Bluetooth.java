package cordova.plugins;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

public class Diagnostic_Bluetooth extends CordovaPlugin {
    protected static final String BLUETOOTH_STATE_POWERED_OFF = "powered_off";
    protected static final String BLUETOOTH_STATE_POWERED_ON = "powered_on";
    protected static final String BLUETOOTH_STATE_POWERING_OFF = "powering_off";
    protected static final String BLUETOOTH_STATE_POWERING_ON = "powering_on";
    protected static final String BLUETOOTH_STATE_UNKNOWN = "unknown";
    public static final String TAG = "Diagnostic_Bluetooth";
    public static Diagnostic_Bluetooth instance;
    protected final BroadcastReceiver bluetoothStateChangeReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Diagnostic_Bluetooth.instance != null && action.equals("android.bluetooth.adapter.action.STATE_CHANGED")) {
                Log.v(Diagnostic_Bluetooth.TAG, "bluetoothStateChangeReceiver");
                Diagnostic_Bluetooth.instance.notifyBluetoothStateChange();
            }
        }
    };
    private String currentBluetoothState = null;
    protected CallbackContext currentContext;
    private Diagnostic diagnostic;

    public void initialize(CordovaInterface cordovaInterface, CordovaWebView cordovaWebView) {
        Log.d(TAG, "initialize()");
        instance = this;
        this.diagnostic = Diagnostic.getInstance();
        try {
            this.diagnostic.applicationContext.registerReceiver(this.bluetoothStateChangeReceiver, new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED"));
            this.currentBluetoothState = getBluetoothState();
        } catch (Exception e) {
            Diagnostic diagnostic2 = this.diagnostic;
            diagnostic2.logWarning("Unable to register Bluetooth state change receiver: " + e.getMessage());
        }
        super.initialize(cordovaInterface, cordovaWebView);
    }

    public void onDestroy() {
        try {
            this.diagnostic.applicationContext.unregisterReceiver(this.bluetoothStateChangeReceiver);
        } catch (Exception e) {
            Diagnostic diagnostic2 = this.diagnostic;
            diagnostic2.logWarning("Unable to unregister Bluetooth state change receiver: " + e.getMessage());
        }
    }

    public boolean execute(String str, JSONArray jSONArray, CallbackContext callbackContext) throws JSONException {
        this.currentContext = callbackContext;
        try {
            if (str.equals("switchToBluetoothSettings")) {
                switchToBluetoothSettings();
                callbackContext.success();
            } else if (str.equals("isBluetoothAvailable")) {
                callbackContext.success(isBluetoothAvailable() ? 1 : 0);
            } else if (str.equals("isBluetoothEnabled")) {
                callbackContext.success(isBluetoothEnabled() ? 1 : 0);
            } else if (str.equals("hasBluetoothSupport")) {
                callbackContext.success(hasBluetoothSupport() ? 1 : 0);
            } else if (str.equals("hasBluetoothLESupport")) {
                callbackContext.success(hasBluetoothLESupport() ? 1 : 0);
            } else if (str.equals("hasBluetoothLEPeripheralSupport")) {
                callbackContext.success(hasBluetoothLEPeripheralSupport() ? 1 : 0);
            } else if (str.equals("setBluetoothState")) {
                setBluetoothState(jSONArray.getBoolean(0));
                callbackContext.success();
            } else if (str.equals("getBluetoothState")) {
                callbackContext.success(getBluetoothState());
            } else {
                this.diagnostic.handleError("Invalid action");
                return false;
            }
            return true;
        } catch (Exception e) {
            this.diagnostic.handleError("Exception occurred: ".concat(e.getMessage()));
            return false;
        }
    }

    public void switchToBluetoothSettings() {
        this.diagnostic.logDebug("Switch to Bluetooth Settings");
        this.f59cordova.getActivity().startActivity(new Intent("android.settings.BLUETOOTH_SETTINGS"));
    }

    public boolean isBluetoothAvailable() {
        return hasBluetoothSupport() && isBluetoothEnabled();
    }

    public boolean isBluetoothEnabled() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        return defaultAdapter != null && defaultAdapter.isEnabled();
    }

    public boolean hasBluetoothSupport() {
        return this.f59cordova.getActivity().getPackageManager().hasSystemFeature("android.hardware.bluetooth");
    }

    public boolean hasBluetoothLESupport() {
        return this.f59cordova.getActivity().getPackageManager().hasSystemFeature("android.hardware.bluetooth_le");
    }

    public boolean hasBluetoothLEPeripheralSupport() {
        BluetoothAdapter defaultAdapter;
        if (Build.VERSION.SDK_INT < 21 || (defaultAdapter = BluetoothAdapter.getDefaultAdapter()) == null || !defaultAdapter.isMultipleAdvertisementSupported()) {
            return false;
        }
        return true;
    }

    public static boolean setBluetoothState(boolean z) {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean isEnabled = defaultAdapter.isEnabled();
        if (z && !isEnabled) {
            return defaultAdapter.enable();
        }
        if (z || !isEnabled) {
            return true;
        }
        return defaultAdapter.disable();
    }

    public String getBluetoothState() {
        if (!hasBluetoothSupport()) {
            return "unknown";
        }
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter == null) {
            this.diagnostic.logWarning("Bluetooth adapter unavailable or not found");
            return "unknown";
        }
        switch (defaultAdapter.getState()) {
            case 10:
                return "powered_off";
            case 11:
                return "powering_on";
            case 12:
                return "powered_on";
            case 13:
                return "powering_off";
            default:
                return "unknown";
        }
    }

    public void notifyBluetoothStateChange() {
        try {
            String bluetoothState = getBluetoothState();
            if (!bluetoothState.equals(this.currentBluetoothState)) {
                Diagnostic diagnostic2 = this.diagnostic;
                diagnostic2.logDebug("Bluetooth state changed to: " + bluetoothState);
                Diagnostic diagnostic3 = this.diagnostic;
                diagnostic3.executePluginJavascript("bluetooth._onBluetoothStateChange(\"" + bluetoothState + "\");");
                this.currentBluetoothState = bluetoothState;
            }
        } catch (Exception e) {
            Diagnostic diagnostic4 = this.diagnostic;
            diagnostic4.logError("Error retrieving current Bluetooth state on Bluetooth state change: " + e.toString());
        }
    }
}
