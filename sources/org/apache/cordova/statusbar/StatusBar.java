package org.apache.cordova.statusbar;

import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import com.adobe.phonegap.push.PushConstants;
import java.util.Arrays;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.json.JSONException;

public class StatusBar extends CordovaPlugin {
    private static final String TAG = "StatusBar";

    public void initialize(final CordovaInterface cordovaInterface, CordovaWebView cordovaWebView) {
        LOG.m46v(TAG, "StatusBar: initialization");
        super.initialize(cordovaInterface, cordovaWebView);
        this.f59cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                cordovaInterface.getActivity().getWindow().clearFlags(2048);
                StatusBar statusBar = StatusBar.this;
                statusBar.setStatusBarBackgroundColor(statusBar.preferences.getString("StatusBarBackgroundColor", "#000000"));
                StatusBar statusBar2 = StatusBar.this;
                statusBar2.setStatusBarStyle(statusBar2.preferences.getString("StatusBarStyle", "lightcontent"));
            }
        });
    }

    public boolean execute(String str, final CordovaArgs cordovaArgs, CallbackContext callbackContext) throws JSONException {
        LOG.m46v(TAG, "Executing action: " + str);
        final Window window = this.f59cordova.getActivity().getWindow();
        boolean z = false;
        if ("_ready".equals(str)) {
            if ((window.getAttributes().flags & 1024) == 0) {
                z = true;
            }
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, z));
            return true;
        } else if ("show".equals(str)) {
            this.f59cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    if (Build.VERSION.SDK_INT >= 19) {
                        window.getDecorView().setSystemUiVisibility(window.getDecorView().getSystemUiVisibility() & -1025 & -5);
                    }
                    window.clearFlags(1024);
                }
            });
            return true;
        } else if ("hide".equals(str)) {
            this.f59cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    if (Build.VERSION.SDK_INT >= 19) {
                        window.getDecorView().setSystemUiVisibility(window.getDecorView().getSystemUiVisibility() | 1024 | 4);
                    }
                    window.addFlags(1024);
                }
            });
            return true;
        } else if ("backgroundColorByHexString".equals(str)) {
            this.f59cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        StatusBar.this.setStatusBarBackgroundColor(cordovaArgs.getString(0));
                    } catch (JSONException unused) {
                        LOG.m40e(StatusBar.TAG, "Invalid hexString argument, use f.i. '#777777'");
                    }
                }
            });
            return true;
        } else if ("overlaysWebView".equals(str)) {
            if (Build.VERSION.SDK_INT < 21) {
                return !cordovaArgs.getBoolean(0);
            }
            this.f59cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        StatusBar.this.setStatusBarTransparent(cordovaArgs.getBoolean(0));
                    } catch (JSONException unused) {
                        LOG.m40e(StatusBar.TAG, "Invalid boolean argument");
                    }
                }
            });
            return true;
        } else if ("styleDefault".equals(str)) {
            this.f59cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    StatusBar.this.setStatusBarStyle(PushConstants.SOUND_DEFAULT);
                }
            });
            return true;
        } else if ("styleLightContent".equals(str)) {
            this.f59cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    StatusBar.this.setStatusBarStyle("lightcontent");
                }
            });
            return true;
        } else if ("styleBlackTranslucent".equals(str)) {
            this.f59cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    StatusBar.this.setStatusBarStyle("blacktranslucent");
                }
            });
            return true;
        } else if (!"styleBlackOpaque".equals(str)) {
            return false;
        } else {
            this.f59cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    StatusBar.this.setStatusBarStyle("blackopaque");
                }
            });
            return true;
        }
    }

    /* access modifiers changed from: private */
    public void setStatusBarBackgroundColor(String str) {
        if (Build.VERSION.SDK_INT >= 21 && str != null && !str.isEmpty()) {
            Window window = this.f59cordova.getActivity().getWindow();
            window.clearFlags(67108864);
            window.addFlags(Integer.MIN_VALUE);
            try {
                window.getClass().getMethod("setStatusBarColor", new Class[]{Integer.TYPE}).invoke(window, new Object[]{Integer.valueOf(Color.parseColor(str))});
            } catch (IllegalArgumentException unused) {
                LOG.m40e(TAG, "Invalid hexString argument, use f.i. '#999999'");
            } catch (Exception unused2) {
                LOG.m49w(TAG, "Method window.setStatusBarColor not found for SDK level " + Build.VERSION.SDK_INT);
            }
        }
    }

    /* access modifiers changed from: private */
    public void setStatusBarTransparent(boolean z) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.f59cordova.getActivity().getWindow();
            if (z) {
                window.getDecorView().setSystemUiVisibility(1280);
                window.setStatusBarColor(0);
                return;
            }
            window.getDecorView().setSystemUiVisibility(256);
        }
    }

    /* access modifiers changed from: private */
    public void setStatusBarStyle(String str) {
        if (Build.VERSION.SDK_INT >= 23 && str != null && !str.isEmpty()) {
            View decorView = this.f59cordova.getActivity().getWindow().getDecorView();
            int systemUiVisibility = decorView.getSystemUiVisibility();
            String[] strArr = {"lightcontent", "blacktranslucent", "blackopaque"};
            if (Arrays.asList(new String[]{PushConstants.SOUND_DEFAULT}).contains(str.toLowerCase())) {
                decorView.setSystemUiVisibility(systemUiVisibility | 8192);
            } else if (Arrays.asList(strArr).contains(str.toLowerCase())) {
                decorView.setSystemUiVisibility(systemUiVisibility & -8193);
            } else {
                LOG.m40e(TAG, "Invalid style, must be either 'default', 'lightcontent' or the deprecated 'blacktranslucent' and 'blackopaque'");
            }
        }
    }
}
