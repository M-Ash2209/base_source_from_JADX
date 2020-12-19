package org.apache.cordova.inappbrowser;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.adobe.phonegap.push.PushConstants;
import com.google.android.gms.common.internal.ImagesContract;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.Config;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginManager;
import org.apache.cordova.PluginResult;
import org.apache.cordova.globalization.Globalization;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint({"SetJavaScriptEnabled"})
public class InAppBrowser extends CordovaPlugin {
    private static final String BEFORELOAD = "beforeload";
    private static final String CLEAR_ALL_CACHE = "clearcache";
    private static final String CLEAR_SESSION_CACHE = "clearsessioncache";
    private static final String CLOSE_BUTTON_CAPTION = "closebuttoncaption";
    private static final String CLOSE_BUTTON_COLOR = "closebuttoncolor";
    private static final Boolean DEFAULT_HARDWARE_BACK = true;
    private static final String EXIT_EVENT = "exit";
    private static final int FILECHOOSER_REQUESTCODE = 1;
    private static final int FILECHOOSER_REQUESTCODE_LOLLIPOP = 2;
    private static final String FOOTER = "footer";
    private static final String FOOTER_COLOR = "footercolor";
    private static final String FULLSCREEN = "fullscreen";
    private static final String HARDWARE_BACK_BUTTON = "hardwareback";
    private static final String HIDDEN = "hidden";
    private static final String HIDE_NAVIGATION = "hidenavigationbuttons";
    private static final String HIDE_URL = "hideurlbar";
    private static final String LEFT_TO_RIGHT = "lefttoright";
    private static final String LOAD_ERROR_EVENT = "loaderror";
    private static final String LOAD_START_EVENT = "loadstart";
    private static final String LOAD_STOP_EVENT = "loadstop";
    private static final String LOCATION = "location";
    protected static final String LOG_TAG = "InAppBrowser";
    private static final String MEDIA_PLAYBACK_REQUIRES_USER_ACTION = "mediaPlaybackRequiresUserAction";
    private static final String MESSAGE_EVENT = "message";
    private static final String NAVIGATION_COLOR = "navigationbuttoncolor";
    private static final String NULL = "null";
    private static final String SELF = "_self";
    private static final String SHOULD_PAUSE = "shouldPauseOnSuspend";
    private static final String SYSTEM = "_system";
    private static final String TOOLBAR_COLOR = "toolbarcolor";
    private static final String USER_WIDE_VIEW_PORT = "useWideViewPort";
    private static final String ZOOM = "zoom";
    private static final List customizableOptions = Arrays.asList(new String[]{CLOSE_BUTTON_CAPTION, TOOLBAR_COLOR, NAVIGATION_COLOR, CLOSE_BUTTON_COLOR, FOOTER_COLOR});
    /* access modifiers changed from: private */
    public String[] allowedSchemes;
    /* access modifiers changed from: private */
    public String beforeload = "";
    private CallbackContext callbackContext;
    /* access modifiers changed from: private */
    public boolean clearAllCache = false;
    /* access modifiers changed from: private */
    public boolean clearSessionCache = false;
    /* access modifiers changed from: private */
    public String closeButtonCaption = "";
    /* access modifiers changed from: private */
    public String closeButtonColor = "";
    /* access modifiers changed from: private */
    public InAppBrowserClient currentClient;
    /* access modifiers changed from: private */
    public InAppBrowserDialog dialog;
    /* access modifiers changed from: private */
    public EditText edittext;
    /* access modifiers changed from: private */
    public String footerColor = "";
    /* access modifiers changed from: private */
    public boolean fullscreen = true;
    private boolean hadwareBackButton = true;
    /* access modifiers changed from: private */
    public boolean hideNavigationButtons = false;
    /* access modifiers changed from: private */
    public boolean hideUrlBar = false;
    /* access modifiers changed from: private */
    public WebView inAppWebView;
    /* access modifiers changed from: private */
    public boolean leftToRight = false;
    /* access modifiers changed from: private */
    public ValueCallback<Uri> mUploadCallback;
    /* access modifiers changed from: private */
    public ValueCallback<Uri[]> mUploadCallbackLollipop;
    /* access modifiers changed from: private */
    public boolean mediaPlaybackRequiresUserGesture = false;
    /* access modifiers changed from: private */
    public String navigationButtonColor = "";
    /* access modifiers changed from: private */
    public boolean openWindowHidden = false;
    private boolean shouldPauseInAppBrowser = false;
    /* access modifiers changed from: private */
    public boolean showFooter = false;
    private boolean showLocationBar = true;
    /* access modifiers changed from: private */
    public boolean showZoomControls = true;
    /* access modifiers changed from: private */
    public int toolbarColor = -3355444;
    /* access modifiers changed from: private */
    public boolean useWideViewPort = true;

    /* access modifiers changed from: private */
    public InAppBrowser getInAppBrowser() {
        return this;
    }

    public boolean execute(String str, CordovaArgs cordovaArgs, CallbackContext callbackContext2) throws JSONException {
        if (str.equals("open")) {
            this.callbackContext = callbackContext2;
            final String string = cordovaArgs.getString(0);
            String optString = cordovaArgs.optString(1);
            final String str2 = (optString == null || optString.equals("") || optString.equals(NULL)) ? SELF : optString;
            final HashMap<String, String> parseFeature = parseFeature(cordovaArgs.optString(2));
            LOG.m37d(LOG_TAG, "target = " + str2);
            final CallbackContext callbackContext3 = callbackContext2;
            this.f59cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    String str = "";
                    if (InAppBrowser.SELF.equals(str2)) {
                        LOG.m37d(InAppBrowser.LOG_TAG, "in self");
                        boolean z = string.startsWith("javascript:") ? true : null;
                        if (z == null) {
                            try {
                                z = (Boolean) Config.class.getMethod("isUrlWhiteListed", new Class[]{String.class}).invoke((Object) null, new Object[]{string});
                            } catch (NoSuchMethodException e) {
                                LOG.m37d(InAppBrowser.LOG_TAG, e.getLocalizedMessage());
                            } catch (IllegalAccessException e2) {
                                LOG.m37d(InAppBrowser.LOG_TAG, e2.getLocalizedMessage());
                            } catch (InvocationTargetException e3) {
                                LOG.m37d(InAppBrowser.LOG_TAG, e3.getLocalizedMessage());
                            }
                        }
                        if (z == null) {
                            try {
                                PluginManager pluginManager = (PluginManager) InAppBrowser.this.webView.getClass().getMethod("getPluginManager", new Class[0]).invoke(InAppBrowser.this.webView, new Object[0]);
                                z = (Boolean) pluginManager.getClass().getMethod("shouldAllowNavigation", new Class[]{String.class}).invoke(pluginManager, new Object[]{string});
                            } catch (NoSuchMethodException e4) {
                                LOG.m37d(InAppBrowser.LOG_TAG, e4.getLocalizedMessage());
                            } catch (IllegalAccessException e5) {
                                LOG.m37d(InAppBrowser.LOG_TAG, e5.getLocalizedMessage());
                            } catch (InvocationTargetException e6) {
                                LOG.m37d(InAppBrowser.LOG_TAG, e6.getLocalizedMessage());
                            }
                        }
                        if (Boolean.TRUE.equals(z)) {
                            LOG.m37d(InAppBrowser.LOG_TAG, "loading in webview");
                            InAppBrowser.this.webView.loadUrl(string);
                        } else if (string.startsWith("tel:")) {
                            try {
                                LOG.m37d(InAppBrowser.LOG_TAG, "loading in dialer");
                                Intent intent = new Intent("android.intent.action.DIAL");
                                intent.setData(Uri.parse(string));
                                InAppBrowser.this.f59cordova.getActivity().startActivity(intent);
                            } catch (ActivityNotFoundException e7) {
                                LOG.m40e(InAppBrowser.LOG_TAG, "Error dialing " + string + ": " + e7.toString());
                            }
                        } else {
                            LOG.m37d(InAppBrowser.LOG_TAG, "loading in InAppBrowser");
                            str = InAppBrowser.this.showWebPage(string, parseFeature);
                        }
                    } else if (InAppBrowser.SYSTEM.equals(str2)) {
                        LOG.m37d(InAppBrowser.LOG_TAG, "in system");
                        str = InAppBrowser.this.openExternal(string);
                    } else {
                        LOG.m37d(InAppBrowser.LOG_TAG, "in blank");
                        str = InAppBrowser.this.showWebPage(string, parseFeature);
                    }
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, str);
                    pluginResult.setKeepCallback(true);
                    callbackContext3.sendPluginResult(pluginResult);
                }
            });
        } else if (str.equals("close")) {
            closeDialog();
        } else if (str.equals("loadAfterBeforeload")) {
            if (this.beforeload == null) {
                LOG.m40e(LOG_TAG, "unexpected loadAfterBeforeload called without feature beforeload=yes");
            }
            final String string2 = cordovaArgs.getString(0);
            this.f59cordova.getActivity().runOnUiThread(new Runnable() {
                @SuppressLint({"NewApi"})
                public void run() {
                    if (Build.VERSION.SDK_INT < 26) {
                        InAppBrowser.this.currentClient.waitForBeforeload = false;
                        InAppBrowser.this.inAppWebView.setWebViewClient(InAppBrowser.this.currentClient);
                    } else {
                        ((InAppBrowserClient) InAppBrowser.this.inAppWebView.getWebViewClient()).waitForBeforeload = false;
                    }
                    InAppBrowser.this.inAppWebView.loadUrl(string2);
                }
            });
        } else if (str.equals("injectScriptCode")) {
            String str3 = null;
            if (cordovaArgs.getBoolean(1)) {
                str3 = String.format("(function(){prompt(JSON.stringify([eval(%%s)]), 'gap-iab://%s')})()", new Object[]{callbackContext2.getCallbackId()});
            }
            injectDeferredObject(cordovaArgs.getString(0), str3);
        } else if (str.equals("injectScriptFile")) {
            injectDeferredObject(cordovaArgs.getString(0), cordovaArgs.getBoolean(1) ? String.format("(function(d) { var c = d.createElement('script'); c.src = %%s; c.onload = function() { prompt('', 'gap-iab://%s'); }; d.body.appendChild(c); })(document)", new Object[]{callbackContext2.getCallbackId()}) : "(function(d) { var c = d.createElement('script'); c.src = %s; d.body.appendChild(c); })(document)");
        } else if (str.equals("injectStyleCode")) {
            injectDeferredObject(cordovaArgs.getString(0), cordovaArgs.getBoolean(1) ? String.format("(function(d) { var c = d.createElement('style'); c.innerHTML = %%s; d.body.appendChild(c); prompt('', 'gap-iab://%s');})(document)", new Object[]{callbackContext2.getCallbackId()}) : "(function(d) { var c = d.createElement('style'); c.innerHTML = %s; d.body.appendChild(c); })(document)");
        } else if (str.equals("injectStyleFile")) {
            injectDeferredObject(cordovaArgs.getString(0), cordovaArgs.getBoolean(1) ? String.format("(function(d) { var c = d.createElement('link'); c.rel='stylesheet'; c.type='text/css'; c.href = %%s; d.head.appendChild(c); prompt('', 'gap-iab://%s');})(document)", new Object[]{callbackContext2.getCallbackId()}) : "(function(d) { var c = d.createElement('link'); c.rel='stylesheet'; c.type='text/css'; c.href = %s; d.head.appendChild(c); })(document)");
        } else if (str.equals("show")) {
            this.f59cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    if (InAppBrowser.this.dialog != null && !InAppBrowser.this.f59cordova.getActivity().isFinishing()) {
                        InAppBrowser.this.dialog.show();
                    }
                }
            });
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
            pluginResult.setKeepCallback(true);
            this.callbackContext.sendPluginResult(pluginResult);
        } else if (!str.equals("hide")) {
            return false;
        } else {
            this.f59cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    if (InAppBrowser.this.dialog != null && !InAppBrowser.this.f59cordova.getActivity().isFinishing()) {
                        InAppBrowser.this.dialog.hide();
                    }
                }
            });
            PluginResult pluginResult2 = new PluginResult(PluginResult.Status.OK);
            pluginResult2.setKeepCallback(true);
            this.callbackContext.sendPluginResult(pluginResult2);
        }
        return true;
    }

    public void onReset() {
        closeDialog();
    }

    public void onPause(boolean z) {
        if (this.shouldPauseInAppBrowser) {
            this.inAppWebView.onPause();
        }
    }

    public void onResume(boolean z) {
        if (this.shouldPauseInAppBrowser) {
            this.inAppWebView.onResume();
        }
    }

    public void onDestroy() {
        closeDialog();
    }

    /* access modifiers changed from: private */
    public void injectDeferredObject(final String str, String str2) {
        if (this.inAppWebView != null) {
            if (str2 != null) {
                JSONArray jSONArray = new JSONArray();
                jSONArray.put(str);
                String jSONArray2 = jSONArray.toString();
                str = String.format(str2, new Object[]{jSONArray2.substring(1, jSONArray2.length() - 1)});
            }
            this.f59cordova.getActivity().runOnUiThread(new Runnable() {
                @SuppressLint({"NewApi"})
                public void run() {
                    if (Build.VERSION.SDK_INT < 19) {
                        WebView access$100 = InAppBrowser.this.inAppWebView;
                        access$100.loadUrl("javascript:" + str);
                        return;
                    }
                    InAppBrowser.this.inAppWebView.evaluateJavascript(str, (ValueCallback) null);
                }
            });
            return;
        }
        LOG.m37d(LOG_TAG, "Can't inject code into the system browser");
    }

    private HashMap<String, String> parseFeature(String str) {
        if (str.equals(NULL)) {
            return null;
        }
        HashMap<String, String> hashMap = new HashMap<>();
        StringTokenizer stringTokenizer = new StringTokenizer(str, ",");
        while (stringTokenizer.hasMoreElements()) {
            StringTokenizer stringTokenizer2 = new StringTokenizer(stringTokenizer.nextToken(), "=");
            if (stringTokenizer2.hasMoreElements()) {
                String nextToken = stringTokenizer2.nextToken();
                String nextToken2 = stringTokenizer2.nextToken();
                if (!customizableOptions.contains(nextToken) && !nextToken2.equals("yes") && !nextToken2.equals("no")) {
                    nextToken2 = "yes";
                }
                hashMap.put(nextToken, nextToken2);
            }
        }
        return hashMap;
    }

    public String openExternal(String str) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW");
            Uri parse = Uri.parse(str);
            if ("file".equals(parse.getScheme())) {
                intent.setDataAndType(parse, this.webView.getResourceApi().getMimeType(parse));
            } else {
                intent.setData(parse);
            }
            intent.putExtra("com.android.browser.application_id", this.f59cordova.getActivity().getPackageName());
            openExternalExcludeCurrentApp(intent);
            return "";
        } catch (RuntimeException e) {
            LOG.m37d(LOG_TAG, "InAppBrowser: Error loading url " + str + ":" + e.toString());
            return e.toString();
        }
    }

    private void openExternalExcludeCurrentApp(Intent intent) {
        String packageName = this.f59cordova.getActivity().getPackageName();
        List<ResolveInfo> queryIntentActivities = this.f59cordova.getActivity().getPackageManager().queryIntentActivities(intent, 0);
        ArrayList arrayList = new ArrayList();
        boolean z = false;
        for (ResolveInfo next : queryIntentActivities) {
            if (!packageName.equals(next.activityInfo.packageName)) {
                Intent intent2 = (Intent) intent.clone();
                intent2.setPackage(next.activityInfo.packageName);
                arrayList.add(intent2);
            } else {
                z = true;
            }
        }
        if (!z || arrayList.size() == 0) {
            this.f59cordova.getActivity().startActivity(intent);
        } else if (arrayList.size() == 1) {
            this.f59cordova.getActivity().startActivity((Intent) arrayList.get(0));
        } else if (arrayList.size() > 0) {
            Intent createChooser = Intent.createChooser((Intent) arrayList.remove(arrayList.size() - 1), (CharSequence) null);
            createChooser.putExtra("android.intent.extra.INITIAL_INTENTS", (Parcelable[]) arrayList.toArray(new Parcelable[0]));
            this.f59cordova.getActivity().startActivity(createChooser);
        }
    }

    public void closeDialog() {
        this.f59cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                WebView access$100 = InAppBrowser.this.inAppWebView;
                if (access$100 != null) {
                    access$100.setWebViewClient(new WebViewClient() {
                        public void onPageFinished(WebView webView, String str) {
                            if (InAppBrowser.this.dialog != null && !InAppBrowser.this.f59cordova.getActivity().isFinishing()) {
                                InAppBrowser.this.dialog.dismiss();
                                InAppBrowserDialog unused = InAppBrowser.this.dialog = null;
                            }
                        }
                    });
                    access$100.loadUrl("about:blank");
                    try {
                        JSONObject jSONObject = new JSONObject();
                        jSONObject.put(Globalization.TYPE, "exit");
                        InAppBrowser.this.sendUpdate(jSONObject, false);
                    } catch (JSONException unused) {
                        LOG.m37d(InAppBrowser.LOG_TAG, "Should never happen");
                    }
                }
            }
        });
    }

    public void goBack() {
        if (this.inAppWebView.canGoBack()) {
            this.inAppWebView.goBack();
        }
    }

    public boolean canGoBack() {
        return this.inAppWebView.canGoBack();
    }

    public boolean hardwareBack() {
        return this.hadwareBackButton;
    }

    /* access modifiers changed from: private */
    public void goForward() {
        if (this.inAppWebView.canGoForward()) {
            this.inAppWebView.goForward();
        }
    }

    /* access modifiers changed from: private */
    public void navigate(String str) {
        ((InputMethodManager) this.f59cordova.getActivity().getSystemService("input_method")).hideSoftInputFromWindow(this.edittext.getWindowToken(), 0);
        if (str.startsWith("http") || str.startsWith("file:")) {
            this.inAppWebView.loadUrl(str);
        } else {
            WebView webView = this.inAppWebView;
            webView.loadUrl("http://" + str);
        }
        this.inAppWebView.requestFocus();
    }

    /* access modifiers changed from: private */
    public boolean getShowLocationBar() {
        return this.showLocationBar;
    }

    public String showWebPage(final String str, HashMap<String, String> hashMap) {
        boolean z = true;
        this.showLocationBar = true;
        this.showZoomControls = true;
        this.openWindowHidden = false;
        this.mediaPlaybackRequiresUserGesture = false;
        if (hashMap != null) {
            String str2 = hashMap.get(LOCATION);
            if (str2 != null) {
                this.showLocationBar = str2.equals("yes");
            }
            if (this.showLocationBar) {
                String str3 = hashMap.get(HIDE_NAVIGATION);
                String str4 = hashMap.get(HIDE_URL);
                if (str3 != null) {
                    this.hideNavigationButtons = str3.equals("yes");
                }
                if (str4 != null) {
                    this.hideUrlBar = str4.equals("yes");
                }
            }
            String str5 = hashMap.get(ZOOM);
            if (str5 != null) {
                this.showZoomControls = str5.equals("yes");
            }
            String str6 = hashMap.get(HIDDEN);
            if (str6 != null) {
                this.openWindowHidden = str6.equals("yes");
            }
            String str7 = hashMap.get(HARDWARE_BACK_BUTTON);
            if (str7 != null) {
                this.hadwareBackButton = str7.equals("yes");
            } else {
                this.hadwareBackButton = DEFAULT_HARDWARE_BACK.booleanValue();
            }
            String str8 = hashMap.get(MEDIA_PLAYBACK_REQUIRES_USER_ACTION);
            if (str8 != null) {
                this.mediaPlaybackRequiresUserGesture = str8.equals("yes");
            }
            String str9 = hashMap.get(CLEAR_ALL_CACHE);
            if (str9 != null) {
                this.clearAllCache = str9.equals("yes");
            } else {
                String str10 = hashMap.get(CLEAR_SESSION_CACHE);
                if (str10 != null) {
                    this.clearSessionCache = str10.equals("yes");
                }
            }
            String str11 = hashMap.get(SHOULD_PAUSE);
            if (str11 != null) {
                this.shouldPauseInAppBrowser = str11.equals("yes");
            }
            String str12 = hashMap.get(USER_WIDE_VIEW_PORT);
            if (str12 != null) {
                this.useWideViewPort = str12.equals("yes");
            }
            String str13 = hashMap.get(CLOSE_BUTTON_CAPTION);
            if (str13 != null) {
                this.closeButtonCaption = str13;
            }
            String str14 = hashMap.get(CLOSE_BUTTON_COLOR);
            if (str14 != null) {
                this.closeButtonColor = str14;
            }
            String str15 = hashMap.get(LEFT_TO_RIGHT);
            if (str15 == null || !str15.equals("yes")) {
                z = false;
            }
            this.leftToRight = z;
            String str16 = hashMap.get(TOOLBAR_COLOR);
            if (str16 != null) {
                this.toolbarColor = Color.parseColor(str16);
            }
            String str17 = hashMap.get(NAVIGATION_COLOR);
            if (str17 != null) {
                this.navigationButtonColor = str17;
            }
            String str18 = hashMap.get(FOOTER);
            if (str18 != null) {
                this.showFooter = str18.equals("yes");
            }
            String str19 = hashMap.get(FOOTER_COLOR);
            if (str19 != null) {
                this.footerColor = str19;
            }
            if (hashMap.get(BEFORELOAD) != null) {
                this.beforeload = hashMap.get(BEFORELOAD);
            }
            String str20 = hashMap.get(FULLSCREEN);
            if (str20 != null) {
                this.fullscreen = str20.equals("yes");
            }
        }
        final CordovaWebView cordovaWebView = this.webView;
        this.f59cordova.getActivity().runOnUiThread(new Runnable() {
            private int dpToPixels(int i) {
                return (int) TypedValue.applyDimension(1, (float) i, InAppBrowser.this.f59cordova.getActivity().getResources().getDisplayMetrics());
            }

            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v4, resolved type: android.widget.ImageButton} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v8, resolved type: android.widget.ImageButton} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v9, resolved type: android.widget.TextView} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v10, resolved type: android.widget.ImageButton} */
            /* JADX WARNING: Multi-variable type inference failed */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            private android.view.View createCloseButton(int r7) {
                /*
                    r6 = this;
                    org.apache.cordova.inappbrowser.InAppBrowser r0 = org.apache.cordova.inappbrowser.InAppBrowser.this
                    org.apache.cordova.CordovaInterface r0 = r0.f59cordova
                    android.app.Activity r0 = r0.getActivity()
                    android.content.res.Resources r0 = r0.getResources()
                    org.apache.cordova.inappbrowser.InAppBrowser r1 = org.apache.cordova.inappbrowser.InAppBrowser.this
                    java.lang.String r1 = r1.closeButtonCaption
                    java.lang.String r2 = ""
                    r3 = 16
                    if (r1 == r2) goto L_0x005c
                    android.widget.TextView r0 = new android.widget.TextView
                    org.apache.cordova.inappbrowser.InAppBrowser r1 = org.apache.cordova.inappbrowser.InAppBrowser.this
                    org.apache.cordova.CordovaInterface r1 = r1.f59cordova
                    android.app.Activity r1 = r1.getActivity()
                    r0.<init>(r1)
                    org.apache.cordova.inappbrowser.InAppBrowser r1 = org.apache.cordova.inappbrowser.InAppBrowser.this
                    java.lang.String r1 = r1.closeButtonCaption
                    r0.setText(r1)
                    r1 = 1101004800(0x41a00000, float:20.0)
                    r0.setTextSize(r1)
                    org.apache.cordova.inappbrowser.InAppBrowser r1 = org.apache.cordova.inappbrowser.InAppBrowser.this
                    java.lang.String r1 = r1.closeButtonColor
                    java.lang.String r2 = ""
                    if (r1 == r2) goto L_0x004a
                    org.apache.cordova.inappbrowser.InAppBrowser r1 = org.apache.cordova.inappbrowser.InAppBrowser.this
                    java.lang.String r1 = r1.closeButtonColor
                    int r1 = android.graphics.Color.parseColor(r1)
                    r0.setTextColor(r1)
                L_0x004a:
                    r0.setGravity(r3)
                    r1 = 10
                    int r2 = r6.dpToPixels(r1)
                    int r1 = r6.dpToPixels(r1)
                    r4 = 0
                    r0.setPadding(r2, r4, r1, r4)
                    goto L_0x00a8
                L_0x005c:
                    android.widget.ImageButton r1 = new android.widget.ImageButton
                    org.apache.cordova.inappbrowser.InAppBrowser r2 = org.apache.cordova.inappbrowser.InAppBrowser.this
                    org.apache.cordova.CordovaInterface r2 = r2.f59cordova
                    android.app.Activity r2 = r2.getActivity()
                    r1.<init>(r2)
                    java.lang.String r2 = "ic_action_remove"
                    java.lang.String r4 = "drawable"
                    org.apache.cordova.inappbrowser.InAppBrowser r5 = org.apache.cordova.inappbrowser.InAppBrowser.this
                    org.apache.cordova.CordovaInterface r5 = r5.f59cordova
                    android.app.Activity r5 = r5.getActivity()
                    java.lang.String r5 = r5.getPackageName()
                    int r2 = r0.getIdentifier(r2, r4, r5)
                    android.graphics.drawable.Drawable r0 = r0.getDrawable(r2)
                    org.apache.cordova.inappbrowser.InAppBrowser r2 = org.apache.cordova.inappbrowser.InAppBrowser.this
                    java.lang.String r2 = r2.closeButtonColor
                    java.lang.String r4 = ""
                    if (r2 == r4) goto L_0x0098
                    org.apache.cordova.inappbrowser.InAppBrowser r2 = org.apache.cordova.inappbrowser.InAppBrowser.this
                    java.lang.String r2 = r2.closeButtonColor
                    int r2 = android.graphics.Color.parseColor(r2)
                    r1.setColorFilter(r2)
                L_0x0098:
                    r1.setImageDrawable(r0)
                    android.widget.ImageView$ScaleType r0 = android.widget.ImageView.ScaleType.FIT_CENTER
                    r1.setScaleType(r0)
                    int r0 = android.os.Build.VERSION.SDK_INT
                    if (r0 < r3) goto L_0x00a7
                    r1.getAdjustViewBounds()
                L_0x00a7:
                    r0 = r1
                L_0x00a8:
                    android.widget.RelativeLayout$LayoutParams r1 = new android.widget.RelativeLayout$LayoutParams
                    r2 = -2
                    r4 = -1
                    r1.<init>(r2, r4)
                    org.apache.cordova.inappbrowser.InAppBrowser r2 = org.apache.cordova.inappbrowser.InAppBrowser.this
                    boolean r2 = r2.leftToRight
                    if (r2 == 0) goto L_0x00bd
                    r2 = 9
                    r1.addRule(r2)
                    goto L_0x00c2
                L_0x00bd:
                    r2 = 11
                    r1.addRule(r2)
                L_0x00c2:
                    r0.setLayoutParams(r1)
                    int r1 = android.os.Build.VERSION.SDK_INT
                    r2 = 0
                    if (r1 < r3) goto L_0x00ce
                    r0.setBackground(r2)
                    goto L_0x00d1
                L_0x00ce:
                    r0.setBackgroundDrawable(r2)
                L_0x00d1:
                    java.lang.String r1 = "Close Button"
                    r0.setContentDescription(r1)
                    java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
                    int r7 = r7.intValue()
                    r0.setId(r7)
                    org.apache.cordova.inappbrowser.InAppBrowser$7$1 r7 = new org.apache.cordova.inappbrowser.InAppBrowser$7$1
                    r7.<init>()
                    r0.setOnClickListener(r7)
                    return r0
                */
                throw new UnsupportedOperationException("Method not decompiled: org.apache.cordova.inappbrowser.InAppBrowser.C05837.createCloseButton(int):android.view.View");
            }

            @SuppressLint({"NewApi"})
            public void run() {
                boolean z;
                if (InAppBrowser.this.dialog != null) {
                    InAppBrowser.this.dialog.dismiss();
                }
                InAppBrowser inAppBrowser = InAppBrowser.this;
                InAppBrowserDialog unused = inAppBrowser.dialog = new InAppBrowserDialog(inAppBrowser.f59cordova.getActivity(), 16973830);
                InAppBrowser.this.dialog.getWindow().getAttributes().windowAnimations = 16973826;
                InAppBrowser.this.dialog.requestWindowFeature(1);
                if (InAppBrowser.this.fullscreen) {
                    InAppBrowser.this.dialog.getWindow().setFlags(1024, 1024);
                }
                InAppBrowser.this.dialog.setCancelable(true);
                InAppBrowser.this.dialog.setInAppBroswer(InAppBrowser.this.getInAppBrowser());
                LinearLayout linearLayout = new LinearLayout(InAppBrowser.this.f59cordova.getActivity());
                linearLayout.setOrientation(1);
                RelativeLayout relativeLayout = new RelativeLayout(InAppBrowser.this.f59cordova.getActivity());
                relativeLayout.setBackgroundColor(InAppBrowser.this.toolbarColor);
                relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(-1, dpToPixels(44)));
                relativeLayout.setPadding(dpToPixels(2), dpToPixels(2), dpToPixels(2), dpToPixels(2));
                if (InAppBrowser.this.leftToRight) {
                    relativeLayout.setHorizontalGravity(3);
                } else {
                    relativeLayout.setHorizontalGravity(5);
                }
                relativeLayout.setVerticalGravity(48);
                RelativeLayout relativeLayout2 = new RelativeLayout(InAppBrowser.this.f59cordova.getActivity());
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, -2);
                if (InAppBrowser.this.leftToRight) {
                    layoutParams.addRule(11);
                } else {
                    layoutParams.addRule(9);
                }
                relativeLayout2.setLayoutParams(layoutParams);
                relativeLayout2.setHorizontalGravity(3);
                relativeLayout2.setVerticalGravity(16);
                relativeLayout2.setId((InAppBrowser.this.leftToRight ? 5 : 1).intValue());
                ImageButton imageButton = new ImageButton(InAppBrowser.this.f59cordova.getActivity());
                RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(-2, -1);
                layoutParams2.addRule(5);
                imageButton.setLayoutParams(layoutParams2);
                imageButton.setContentDescription("Back Button");
                Integer num = 2;
                imageButton.setId(num.intValue());
                Resources resources = InAppBrowser.this.f59cordova.getActivity().getResources();
                Drawable drawable = resources.getDrawable(resources.getIdentifier("ic_action_previous_item", PushConstants.DRAWABLE, InAppBrowser.this.f59cordova.getActivity().getPackageName()));
                if (InAppBrowser.this.navigationButtonColor != "") {
                    imageButton.setColorFilter(Color.parseColor(InAppBrowser.this.navigationButtonColor));
                }
                if (Build.VERSION.SDK_INT >= 16) {
                    imageButton.setBackground((Drawable) null);
                } else {
                    imageButton.setBackgroundDrawable((Drawable) null);
                }
                imageButton.setImageDrawable(drawable);
                imageButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageButton.setPadding(0, dpToPixels(10), 0, dpToPixels(10));
                if (Build.VERSION.SDK_INT >= 16) {
                    imageButton.getAdjustViewBounds();
                }
                imageButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        InAppBrowser.this.goBack();
                    }
                });
                ImageButton imageButton2 = new ImageButton(InAppBrowser.this.f59cordova.getActivity());
                RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(-2, -1);
                layoutParams3.addRule(1, 2);
                imageButton2.setLayoutParams(layoutParams3);
                imageButton2.setContentDescription("Forward Button");
                Integer num2 = 3;
                imageButton2.setId(num2.intValue());
                Drawable drawable2 = resources.getDrawable(resources.getIdentifier("ic_action_next_item", PushConstants.DRAWABLE, InAppBrowser.this.f59cordova.getActivity().getPackageName()));
                if (InAppBrowser.this.navigationButtonColor != "") {
                    imageButton2.setColorFilter(Color.parseColor(InAppBrowser.this.navigationButtonColor));
                }
                if (Build.VERSION.SDK_INT >= 16) {
                    imageButton2.setBackground((Drawable) null);
                } else {
                    imageButton2.setBackgroundDrawable((Drawable) null);
                }
                imageButton2.setImageDrawable(drawable2);
                imageButton2.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageButton2.setPadding(0, dpToPixels(10), 0, dpToPixels(10));
                if (Build.VERSION.SDK_INT >= 16) {
                    imageButton2.getAdjustViewBounds();
                }
                imageButton2.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        InAppBrowser.this.goForward();
                    }
                });
                InAppBrowser inAppBrowser2 = InAppBrowser.this;
                EditText unused2 = inAppBrowser2.edittext = new EditText(inAppBrowser2.f59cordova.getActivity());
                RelativeLayout.LayoutParams layoutParams4 = new RelativeLayout.LayoutParams(-1, -1);
                layoutParams4.addRule(1, 1);
                int i = 5;
                layoutParams4.addRule(0, 5);
                InAppBrowser.this.edittext.setLayoutParams(layoutParams4);
                Integer num3 = 4;
                InAppBrowser.this.edittext.setId(num3.intValue());
                InAppBrowser.this.edittext.setSingleLine(true);
                InAppBrowser.this.edittext.setText(str);
                InAppBrowser.this.edittext.setInputType(16);
                InAppBrowser.this.edittext.setImeOptions(2);
                InAppBrowser.this.edittext.setInputType(0);
                InAppBrowser.this.edittext.setOnKeyListener(new View.OnKeyListener() {
                    public boolean onKey(View view, int i, KeyEvent keyEvent) {
                        if (keyEvent.getAction() != 0 || i != 66) {
                            return false;
                        }
                        InAppBrowser.this.navigate(InAppBrowser.this.edittext.getText().toString());
                        return true;
                    }
                });
                if (InAppBrowser.this.leftToRight) {
                    i = 1;
                }
                relativeLayout.addView(createCloseButton(i));
                RelativeLayout relativeLayout3 = new RelativeLayout(InAppBrowser.this.f59cordova.getActivity());
                relativeLayout3.setBackgroundColor(InAppBrowser.this.footerColor != "" ? Color.parseColor(InAppBrowser.this.footerColor) : -3355444);
                RelativeLayout.LayoutParams layoutParams5 = new RelativeLayout.LayoutParams(-1, dpToPixels(44));
                layoutParams5.addRule(12, -1);
                relativeLayout3.setLayoutParams(layoutParams5);
                if (InAppBrowser.this.closeButtonCaption != "") {
                    relativeLayout3.setPadding(dpToPixels(8), dpToPixels(8), dpToPixels(8), dpToPixels(8));
                }
                relativeLayout3.setHorizontalGravity(3);
                relativeLayout3.setVerticalGravity(80);
                relativeLayout3.addView(createCloseButton(7));
                InAppBrowser inAppBrowser3 = InAppBrowser.this;
                WebView unused3 = inAppBrowser3.inAppWebView = new WebView(inAppBrowser3.f59cordova.getActivity());
                InAppBrowser.this.inAppWebView.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
                Integer num4 = 6;
                InAppBrowser.this.inAppWebView.setId(num4.intValue());
                InAppBrowser.this.inAppWebView.setWebChromeClient(new InAppChromeClient(cordovaWebView) {
                    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                        LOG.m37d(InAppBrowser.LOG_TAG, "File Chooser 5.0+");
                        if (InAppBrowser.this.mUploadCallbackLollipop != null) {
                            InAppBrowser.this.mUploadCallbackLollipop.onReceiveValue((Object) null);
                        }
                        ValueCallback unused = InAppBrowser.this.mUploadCallbackLollipop = valueCallback;
                        Intent intent = new Intent("android.intent.action.GET_CONTENT");
                        intent.addCategory("android.intent.category.OPENABLE");
                        intent.setType("*/*");
                        InAppBrowser.this.f59cordova.startActivityForResult(InAppBrowser.this, Intent.createChooser(intent, "Select File"), 2);
                        return true;
                    }

                    public void openFileChooser(ValueCallback<Uri> valueCallback, String str, String str2) {
                        LOG.m37d(InAppBrowser.LOG_TAG, "File Chooser 4.1+");
                        openFileChooser(valueCallback, str);
                    }

                    public void openFileChooser(ValueCallback<Uri> valueCallback, String str) {
                        LOG.m37d(InAppBrowser.LOG_TAG, "File Chooser 3.0+");
                        ValueCallback unused = InAppBrowser.this.mUploadCallback = valueCallback;
                        Intent intent = new Intent("android.intent.action.GET_CONTENT");
                        intent.addCategory("android.intent.category.OPENABLE");
                        InAppBrowser.this.f59cordova.startActivityForResult(InAppBrowser.this, Intent.createChooser(intent, "Select File"), 1);
                    }
                });
                InAppBrowser inAppBrowser4 = InAppBrowser.this;
                InAppBrowserClient unused4 = inAppBrowser4.currentClient = new InAppBrowserClient(cordovaWebView, inAppBrowser4.edittext, InAppBrowser.this.beforeload);
                InAppBrowser.this.inAppWebView.setWebViewClient(InAppBrowser.this.currentClient);
                WebSettings settings = InAppBrowser.this.inAppWebView.getSettings();
                settings.setJavaScriptEnabled(true);
                settings.setJavaScriptCanOpenWindowsAutomatically(true);
                settings.setBuiltInZoomControls(InAppBrowser.this.showZoomControls);
                settings.setPluginState(WebSettings.PluginState.ON);
                if (Build.VERSION.SDK_INT >= 17) {
                    settings.setMediaPlaybackRequiresUserGesture(InAppBrowser.this.mediaPlaybackRequiresUserGesture);
                    InAppBrowser.this.inAppWebView.addJavascriptInterface(new Object() {
                        @JavascriptInterface
                        public void postMessage(String str) {
                            try {
                                JSONObject jSONObject = new JSONObject();
                                jSONObject.put(Globalization.TYPE, "message");
                                jSONObject.put(PushConstants.PARSE_COM_DATA, new JSONObject(str));
                                InAppBrowser.this.sendUpdate(jSONObject, true);
                            } catch (JSONException unused) {
                                LOG.m40e(InAppBrowser.LOG_TAG, "data object passed to postMessage has caused a JSON error.");
                            }
                        }
                    }, "cordova_iab");
                }
                String string = InAppBrowser.this.preferences.getString("OverrideUserAgent", (String) null);
                String string2 = InAppBrowser.this.preferences.getString("AppendUserAgent", (String) null);
                if (string != null) {
                    settings.setUserAgentString(string);
                }
                if (string2 != null) {
                    settings.setUserAgentString(settings.getUserAgentString() + string2);
                }
                Bundle extras = InAppBrowser.this.f59cordova.getActivity().getIntent().getExtras();
                if (extras == null) {
                    z = true;
                } else {
                    z = extras.getBoolean("InAppBrowserStorageEnabled", true);
                }
                if (z) {
                    settings.setDatabasePath(InAppBrowser.this.f59cordova.getActivity().getApplicationContext().getDir("inAppBrowserDB", 0).getPath());
                    settings.setDatabaseEnabled(true);
                }
                settings.setDomStorageEnabled(true);
                if (InAppBrowser.this.clearAllCache) {
                    CookieManager.getInstance().removeAllCookie();
                } else if (InAppBrowser.this.clearSessionCache) {
                    CookieManager.getInstance().removeSessionCookie();
                }
                if (Build.VERSION.SDK_INT >= 21) {
                    CookieManager.getInstance().setAcceptThirdPartyCookies(InAppBrowser.this.inAppWebView, true);
                }
                InAppBrowser.this.inAppWebView.loadUrl(str);
                Integer num5 = 6;
                InAppBrowser.this.inAppWebView.setId(num5.intValue());
                InAppBrowser.this.inAppWebView.getSettings().setLoadWithOverviewMode(true);
                InAppBrowser.this.inAppWebView.getSettings().setUseWideViewPort(InAppBrowser.this.useWideViewPort);
                InAppBrowser.this.inAppWebView.requestFocus();
                InAppBrowser.this.inAppWebView.requestFocusFromTouch();
                relativeLayout2.addView(imageButton);
                relativeLayout2.addView(imageButton2);
                if (!InAppBrowser.this.hideNavigationButtons) {
                    relativeLayout.addView(relativeLayout2);
                }
                if (!InAppBrowser.this.hideUrlBar) {
                    relativeLayout.addView(InAppBrowser.this.edittext);
                }
                if (InAppBrowser.this.getShowLocationBar()) {
                    linearLayout.addView(relativeLayout);
                }
                RelativeLayout relativeLayout4 = new RelativeLayout(InAppBrowser.this.f59cordova.getActivity());
                relativeLayout4.addView(InAppBrowser.this.inAppWebView);
                linearLayout.addView(relativeLayout4);
                if (InAppBrowser.this.showFooter) {
                    relativeLayout4.addView(relativeLayout3);
                }
                WindowManager.LayoutParams layoutParams6 = new WindowManager.LayoutParams();
                layoutParams6.copyFrom(InAppBrowser.this.dialog.getWindow().getAttributes());
                layoutParams6.width = -1;
                layoutParams6.height = -1;
                if (InAppBrowser.this.dialog != null) {
                    InAppBrowser.this.dialog.setContentView(linearLayout);
                    InAppBrowser.this.dialog.show();
                    InAppBrowser.this.dialog.getWindow().setAttributes(layoutParams6);
                }
                if (InAppBrowser.this.openWindowHidden && InAppBrowser.this.dialog != null) {
                    InAppBrowser.this.dialog.hide();
                }
            }
        });
        return "";
    }

    /* access modifiers changed from: private */
    public void sendUpdate(JSONObject jSONObject, boolean z) {
        sendUpdate(jSONObject, z, PluginResult.Status.OK);
    }

    /* access modifiers changed from: private */
    public void sendUpdate(JSONObject jSONObject, boolean z, PluginResult.Status status) {
        if (this.callbackContext != null) {
            PluginResult pluginResult = new PluginResult(status, jSONObject);
            pluginResult.setKeepCallback(z);
            this.callbackContext.sendPluginResult(pluginResult);
            if (!z) {
                this.callbackContext = null;
            }
        }
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        ValueCallback<Uri> valueCallback;
        Uri uri;
        ValueCallback<Uri[]> valueCallback2;
        if (Build.VERSION.SDK_INT >= 21) {
            LOG.m37d(LOG_TAG, "onActivityResult (For Android >= 5.0)");
            if (i != 2 || (valueCallback2 = this.mUploadCallbackLollipop) == null) {
                super.onActivityResult(i, i2, intent);
                return;
            }
            valueCallback2.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(i2, intent));
            this.mUploadCallbackLollipop = null;
            return;
        }
        LOG.m37d(LOG_TAG, "onActivityResult (For Android < 5.0)");
        if (i != 1 || (valueCallback = this.mUploadCallback) == null) {
            super.onActivityResult(i, i2, intent);
        } else if (valueCallback != null) {
            if (intent != null) {
                this.f59cordova.getActivity();
                if (i2 == -1) {
                    uri = intent.getData();
                    this.mUploadCallback.onReceiveValue(uri);
                    this.mUploadCallback = null;
                }
            }
            uri = null;
            this.mUploadCallback.onReceiveValue(uri);
            this.mUploadCallback = null;
        }
    }

    public class InAppBrowserClient extends WebViewClient {
        String beforeload;
        EditText edittext;
        boolean waitForBeforeload;
        CordovaWebView webView;

        public WebResourceResponse shouldInterceptRequest(String str, WebResourceResponse webResourceResponse, String str2) {
            return webResourceResponse;
        }

        public InAppBrowserClient(CordovaWebView cordovaWebView, EditText editText, String str) {
            this.webView = cordovaWebView;
            this.edittext = editText;
            this.beforeload = str;
            this.waitForBeforeload = str != null;
        }

        public boolean shouldOverrideUrlLoading(WebView webView2, String str) {
            return shouldOverrideUrlLoading(str, (String) null);
        }

        @TargetApi(24)
        public boolean shouldOverrideUrlLoading(WebView webView2, WebResourceRequest webResourceRequest) {
            return shouldOverrideUrlLoading(webResourceRequest.getUrl().toString(), webResourceRequest.getMethod());
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v1, resolved type: boolean} */
        /* JADX WARNING: type inference failed for: r2v0 */
        /* JADX WARNING: type inference failed for: r2v2 */
        /* JADX WARNING: type inference failed for: r2v3, types: [int] */
        /* JADX WARNING: type inference failed for: r2v4 */
        /* JADX WARNING: type inference failed for: r2v6 */
        /* JADX WARNING: type inference failed for: r2v7 */
        /* JADX WARNING: type inference failed for: r2v8 */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean shouldOverrideUrlLoading(java.lang.String r9, java.lang.String r10) {
            /*
                r8 = this;
                java.lang.String r0 = r8.beforeload
                java.lang.String r1 = "yes"
                boolean r0 = r0.equals(r1)
                r1 = 0
                r2 = 0
                r3 = 1
                if (r0 == 0) goto L_0x0012
                if (r10 != 0) goto L_0x0012
                r4 = r1
                r0 = 1
                goto L_0x0059
            L_0x0012:
                java.lang.String r0 = r8.beforeload
                java.lang.String r4 = "yes"
                boolean r0 = r0.equals(r4)
                if (r0 == 0) goto L_0x0027
                java.lang.String r0 = "POST"
                boolean r0 = r10.equals(r0)
                if (r0 != 0) goto L_0x0027
                r4 = r1
                r0 = 1
                goto L_0x0059
            L_0x0027:
                java.lang.String r0 = r8.beforeload
                java.lang.String r4 = "get"
                boolean r0 = r0.equals(r4)
                if (r0 == 0) goto L_0x003e
                if (r10 == 0) goto L_0x003b
                java.lang.String r0 = "GET"
                boolean r0 = r10.equals(r0)
                if (r0 == 0) goto L_0x003e
            L_0x003b:
                r4 = r1
                r0 = 1
                goto L_0x0059
            L_0x003e:
                java.lang.String r0 = r8.beforeload
                java.lang.String r4 = "post"
                boolean r0 = r0.equals(r4)
                if (r0 == 0) goto L_0x0057
                if (r10 == 0) goto L_0x0052
                java.lang.String r0 = "POST"
                boolean r0 = r10.equals(r0)
                if (r0 == 0) goto L_0x0057
            L_0x0052:
                java.lang.String r0 = "beforeload doesn't yet support POST requests"
                r4 = r0
                r0 = 0
                goto L_0x0059
            L_0x0057:
                r4 = r1
                r0 = 0
            L_0x0059:
                if (r0 == 0) goto L_0x0066
                boolean r5 = r8.waitForBeforeload
                if (r5 == 0) goto L_0x0066
                boolean r10 = r8.sendBeforeLoad(r9, r10)
                if (r10 == 0) goto L_0x0066
                return r3
            L_0x0066:
                r10 = -1
                if (r4 == 0) goto L_0x00b4
                java.lang.String r5 = "InAppBrowser"
                org.apache.cordova.LOG.m40e(r5, r4)     // Catch:{ Exception -> 0x0091 }
                org.json.JSONObject r5 = new org.json.JSONObject     // Catch:{ Exception -> 0x0091 }
                r5.<init>()     // Catch:{ Exception -> 0x0091 }
                java.lang.String r6 = "type"
                java.lang.String r7 = "loaderror"
                r5.put(r6, r7)     // Catch:{ Exception -> 0x0091 }
                java.lang.String r6 = "url"
                r5.put(r6, r9)     // Catch:{ Exception -> 0x0091 }
                java.lang.String r6 = "code"
                r5.put(r6, r10)     // Catch:{ Exception -> 0x0091 }
                java.lang.String r6 = "message"
                r5.put(r6, r4)     // Catch:{ Exception -> 0x0091 }
                org.apache.cordova.inappbrowser.InAppBrowser r4 = org.apache.cordova.inappbrowser.InAppBrowser.this     // Catch:{ Exception -> 0x0091 }
                org.apache.cordova.PluginResult$Status r6 = org.apache.cordova.PluginResult.Status.ERROR     // Catch:{ Exception -> 0x0091 }
                r4.sendUpdate(r5, r3, r6)     // Catch:{ Exception -> 0x0091 }
                goto L_0x00b4
            L_0x0091:
                r4 = move-exception
                java.lang.String r5 = "InAppBrowser"
                java.lang.StringBuilder r6 = new java.lang.StringBuilder
                r6.<init>()
                java.lang.String r7 = "Error sending loaderror for "
                r6.append(r7)
                r6.append(r9)
                java.lang.String r7 = ": "
                r6.append(r7)
                java.lang.String r4 = r4.toString()
                r6.append(r4)
                java.lang.String r4 = r6.toString()
                org.apache.cordova.LOG.m40e(r5, r4)
            L_0x00b4:
                java.lang.String r4 = "tel:"
                boolean r4 = r9.startsWith(r4)
                if (r4 == 0) goto L_0x00fd
                android.content.Intent r10 = new android.content.Intent     // Catch:{ ActivityNotFoundException -> 0x00d8 }
                java.lang.String r1 = "android.intent.action.DIAL"
                r10.<init>(r1)     // Catch:{ ActivityNotFoundException -> 0x00d8 }
                android.net.Uri r1 = android.net.Uri.parse(r9)     // Catch:{ ActivityNotFoundException -> 0x00d8 }
                r10.setData(r1)     // Catch:{ ActivityNotFoundException -> 0x00d8 }
                org.apache.cordova.inappbrowser.InAppBrowser r1 = org.apache.cordova.inappbrowser.InAppBrowser.this     // Catch:{ ActivityNotFoundException -> 0x00d8 }
                org.apache.cordova.CordovaInterface r1 = r1.f59cordova     // Catch:{ ActivityNotFoundException -> 0x00d8 }
                android.app.Activity r1 = r1.getActivity()     // Catch:{ ActivityNotFoundException -> 0x00d8 }
                r1.startActivity(r10)     // Catch:{ ActivityNotFoundException -> 0x00d8 }
                r2 = 1
                goto L_0x0266
            L_0x00d8:
                r10 = move-exception
                java.lang.String r1 = "InAppBrowser"
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                java.lang.String r5 = "Error dialing "
                r4.append(r5)
                r4.append(r9)
                java.lang.String r9 = ": "
                r4.append(r9)
                java.lang.String r9 = r10.toString()
                r4.append(r9)
                java.lang.String r9 = r4.toString()
                org.apache.cordova.LOG.m40e(r1, r9)
                goto L_0x0266
            L_0x00fd:
                java.lang.String r4 = "geo:"
                boolean r4 = r9.startsWith(r4)
                if (r4 != 0) goto L_0x0228
                java.lang.String r4 = "mailto:"
                boolean r4 = r9.startsWith(r4)
                if (r4 != 0) goto L_0x0228
                java.lang.String r4 = "market:"
                boolean r4 = r9.startsWith(r4)
                if (r4 != 0) goto L_0x0228
                java.lang.String r4 = "intent:"
                boolean r4 = r9.startsWith(r4)
                if (r4 == 0) goto L_0x011f
                goto L_0x0228
            L_0x011f:
                java.lang.String r4 = "sms:"
                boolean r4 = r9.startsWith(r4)
                if (r4 == 0) goto L_0x01b1
                android.content.Intent r1 = new android.content.Intent     // Catch:{ ActivityNotFoundException -> 0x018c }
                java.lang.String r4 = "android.intent.action.VIEW"
                r1.<init>(r4)     // Catch:{ ActivityNotFoundException -> 0x018c }
                r4 = 63
                int r4 = r9.indexOf(r4)     // Catch:{ ActivityNotFoundException -> 0x018c }
                r5 = 4
                if (r4 != r10) goto L_0x013c
                java.lang.String r10 = r9.substring(r5)     // Catch:{ ActivityNotFoundException -> 0x018c }
                goto L_0x015c
            L_0x013c:
                java.lang.String r10 = r9.substring(r5, r4)     // Catch:{ ActivityNotFoundException -> 0x018c }
                android.net.Uri r4 = android.net.Uri.parse(r9)     // Catch:{ ActivityNotFoundException -> 0x018c }
                java.lang.String r4 = r4.getQuery()     // Catch:{ ActivityNotFoundException -> 0x018c }
                if (r4 == 0) goto L_0x015c
                java.lang.String r5 = "body="
                boolean r5 = r4.startsWith(r5)     // Catch:{ ActivityNotFoundException -> 0x018c }
                if (r5 == 0) goto L_0x015c
                java.lang.String r5 = "sms_body"
                r6 = 5
                java.lang.String r4 = r4.substring(r6)     // Catch:{ ActivityNotFoundException -> 0x018c }
                r1.putExtra(r5, r4)     // Catch:{ ActivityNotFoundException -> 0x018c }
            L_0x015c:
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ ActivityNotFoundException -> 0x018c }
                r4.<init>()     // Catch:{ ActivityNotFoundException -> 0x018c }
                java.lang.String r5 = "sms:"
                r4.append(r5)     // Catch:{ ActivityNotFoundException -> 0x018c }
                r4.append(r10)     // Catch:{ ActivityNotFoundException -> 0x018c }
                java.lang.String r4 = r4.toString()     // Catch:{ ActivityNotFoundException -> 0x018c }
                android.net.Uri r4 = android.net.Uri.parse(r4)     // Catch:{ ActivityNotFoundException -> 0x018c }
                r1.setData(r4)     // Catch:{ ActivityNotFoundException -> 0x018c }
                java.lang.String r4 = "address"
                r1.putExtra(r4, r10)     // Catch:{ ActivityNotFoundException -> 0x018c }
                java.lang.String r10 = "vnd.android-dir/mms-sms"
                r1.setType(r10)     // Catch:{ ActivityNotFoundException -> 0x018c }
                org.apache.cordova.inappbrowser.InAppBrowser r10 = org.apache.cordova.inappbrowser.InAppBrowser.this     // Catch:{ ActivityNotFoundException -> 0x018c }
                org.apache.cordova.CordovaInterface r10 = r10.f59cordova     // Catch:{ ActivityNotFoundException -> 0x018c }
                android.app.Activity r10 = r10.getActivity()     // Catch:{ ActivityNotFoundException -> 0x018c }
                r10.startActivity(r1)     // Catch:{ ActivityNotFoundException -> 0x018c }
                r2 = 1
                goto L_0x0266
            L_0x018c:
                r10 = move-exception
                java.lang.String r1 = "InAppBrowser"
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                java.lang.String r5 = "Error sending sms "
                r4.append(r5)
                r4.append(r9)
                java.lang.String r9 = ":"
                r4.append(r9)
                java.lang.String r9 = r10.toString()
                r4.append(r9)
                java.lang.String r9 = r4.toString()
                org.apache.cordova.LOG.m40e(r1, r9)
                goto L_0x0266
            L_0x01b1:
                java.lang.String r10 = "http:"
                boolean r10 = r9.startsWith(r10)
                if (r10 != 0) goto L_0x0266
                java.lang.String r10 = "https:"
                boolean r10 = r9.startsWith(r10)
                if (r10 != 0) goto L_0x0266
                java.lang.String r10 = "^[A-Za-z0-9+.-]*://.*?$"
                boolean r10 = r9.matches(r10)
                if (r10 == 0) goto L_0x0266
                org.apache.cordova.inappbrowser.InAppBrowser r10 = org.apache.cordova.inappbrowser.InAppBrowser.this
                java.lang.String[] r10 = r10.allowedSchemes
                if (r10 != 0) goto L_0x01ea
                org.apache.cordova.inappbrowser.InAppBrowser r10 = org.apache.cordova.inappbrowser.InAppBrowser.this
                org.apache.cordova.CordovaPreferences r10 = r10.preferences
                java.lang.String r4 = "AllowedSchemes"
                java.lang.String r10 = r10.getString(r4, r1)
                if (r10 == 0) goto L_0x01ea
                org.apache.cordova.inappbrowser.InAppBrowser r1 = org.apache.cordova.inappbrowser.InAppBrowser.this
                java.lang.String r4 = ","
                java.lang.String[] r10 = r10.split(r4)
                java.lang.String[] unused = r1.allowedSchemes = r10
            L_0x01ea:
                org.apache.cordova.inappbrowser.InAppBrowser r10 = org.apache.cordova.inappbrowser.InAppBrowser.this
                java.lang.String[] r10 = r10.allowedSchemes
                if (r10 == 0) goto L_0x0266
                org.apache.cordova.inappbrowser.InAppBrowser r10 = org.apache.cordova.inappbrowser.InAppBrowser.this
                java.lang.String[] r10 = r10.allowedSchemes
                int r1 = r10.length
                r4 = 0
            L_0x01fa:
                if (r2 >= r1) goto L_0x0226
                r5 = r10[r2]
                boolean r5 = r9.startsWith(r5)
                if (r5 == 0) goto L_0x0223
                org.json.JSONObject r5 = new org.json.JSONObject     // Catch:{ JSONException -> 0x021c }
                r5.<init>()     // Catch:{ JSONException -> 0x021c }
                java.lang.String r6 = "type"
                java.lang.String r7 = "customscheme"
                r5.put(r6, r7)     // Catch:{ JSONException -> 0x021c }
                java.lang.String r6 = "url"
                r5.put(r6, r9)     // Catch:{ JSONException -> 0x021c }
                org.apache.cordova.inappbrowser.InAppBrowser r6 = org.apache.cordova.inappbrowser.InAppBrowser.this     // Catch:{ JSONException -> 0x021c }
                r6.sendUpdate(r5, r3)     // Catch:{ JSONException -> 0x021c }
                r4 = 1
                goto L_0x0223
            L_0x021c:
                java.lang.String r5 = "InAppBrowser"
                java.lang.String r6 = "Custom Scheme URI passed in has caused a JSON error."
                org.apache.cordova.LOG.m40e(r5, r6)
            L_0x0223:
                int r2 = r2 + 1
                goto L_0x01fa
            L_0x0226:
                r2 = r4
                goto L_0x0266
            L_0x0228:
                android.content.Intent r10 = new android.content.Intent     // Catch:{ ActivityNotFoundException -> 0x0243 }
                java.lang.String r1 = "android.intent.action.VIEW"
                r10.<init>(r1)     // Catch:{ ActivityNotFoundException -> 0x0243 }
                android.net.Uri r1 = android.net.Uri.parse(r9)     // Catch:{ ActivityNotFoundException -> 0x0243 }
                r10.setData(r1)     // Catch:{ ActivityNotFoundException -> 0x0243 }
                org.apache.cordova.inappbrowser.InAppBrowser r1 = org.apache.cordova.inappbrowser.InAppBrowser.this     // Catch:{ ActivityNotFoundException -> 0x0243 }
                org.apache.cordova.CordovaInterface r1 = r1.f59cordova     // Catch:{ ActivityNotFoundException -> 0x0243 }
                android.app.Activity r1 = r1.getActivity()     // Catch:{ ActivityNotFoundException -> 0x0243 }
                r1.startActivity(r10)     // Catch:{ ActivityNotFoundException -> 0x0243 }
                r2 = 1
                goto L_0x0266
            L_0x0243:
                r10 = move-exception
                java.lang.String r1 = "InAppBrowser"
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                java.lang.String r5 = "Error with "
                r4.append(r5)
                r4.append(r9)
                java.lang.String r9 = ": "
                r4.append(r9)
                java.lang.String r9 = r10.toString()
                r4.append(r9)
                java.lang.String r9 = r4.toString()
                org.apache.cordova.LOG.m40e(r1, r9)
            L_0x0266:
                if (r0 == 0) goto L_0x026a
                r8.waitForBeforeload = r3
            L_0x026a:
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: org.apache.cordova.inappbrowser.InAppBrowser.InAppBrowserClient.shouldOverrideUrlLoading(java.lang.String, java.lang.String):boolean");
        }

        private boolean sendBeforeLoad(String str, String str2) {
            try {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put(Globalization.TYPE, InAppBrowser.BEFORELOAD);
                jSONObject.put(ImagesContract.URL, str);
                if (str2 != null) {
                    jSONObject.put("method", str2);
                }
                InAppBrowser.this.sendUpdate(jSONObject, true);
                return true;
            } catch (JSONException unused) {
                LOG.m40e(InAppBrowser.LOG_TAG, "URI passed in has caused a JSON error.");
                return false;
            }
        }

        public WebResourceResponse shouldInterceptRequest(WebView webView2, String str) {
            return shouldInterceptRequest(str, super.shouldInterceptRequest(webView2, str), (String) null);
        }

        @TargetApi(21)
        public WebResourceResponse shouldInterceptRequest(WebView webView2, WebResourceRequest webResourceRequest) {
            return shouldInterceptRequest(webResourceRequest.getUrl().toString(), super.shouldInterceptRequest(webView2, webResourceRequest), webResourceRequest.getMethod());
        }

        public void onPageStarted(WebView webView2, String str, Bitmap bitmap) {
            super.onPageStarted(webView2, str, bitmap);
            if (!str.startsWith("http:") && !str.startsWith("https:") && !str.startsWith("file:")) {
                LOG.m40e(InAppBrowser.LOG_TAG, "Possible Uncaught/Unknown URI");
                str = "http://" + str;
            }
            if (!str.equals(this.edittext.getText().toString())) {
                this.edittext.setText(str);
            }
            try {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put(Globalization.TYPE, InAppBrowser.LOAD_START_EVENT);
                jSONObject.put(ImagesContract.URL, str);
                InAppBrowser.this.sendUpdate(jSONObject, true);
            } catch (JSONException unused) {
                LOG.m40e(InAppBrowser.LOG_TAG, "URI passed in has caused a JSON error.");
            }
        }

        public void onPageFinished(WebView webView2, String str) {
            super.onPageFinished(webView2, str);
            if (Build.VERSION.SDK_INT >= 17) {
                InAppBrowser.this.injectDeferredObject("window.webkit={messageHandlers:{cordova_iab:cordova_iab}}", (String) null);
            }
            if (Build.VERSION.SDK_INT >= 21) {
                CookieManager.getInstance().flush();
            } else {
                CookieSyncManager.getInstance().sync();
            }
            webView2.clearFocus();
            webView2.requestFocus();
            try {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put(Globalization.TYPE, InAppBrowser.LOAD_STOP_EVENT);
                jSONObject.put(ImagesContract.URL, str);
                InAppBrowser.this.sendUpdate(jSONObject, true);
            } catch (JSONException unused) {
                LOG.m37d(InAppBrowser.LOG_TAG, "Should never happen");
            }
        }

        public void onReceivedError(WebView webView2, int i, String str, String str2) {
            super.onReceivedError(webView2, i, str, str2);
            try {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put(Globalization.TYPE, InAppBrowser.LOAD_ERROR_EVENT);
                jSONObject.put(ImagesContract.URL, str2);
                jSONObject.put("code", i);
                jSONObject.put("message", str);
                InAppBrowser.this.sendUpdate(jSONObject, true, PluginResult.Status.ERROR);
            } catch (JSONException unused) {
                LOG.m37d(InAppBrowser.LOG_TAG, "Should never happen");
            }
        }

        public void onReceivedSslError(WebView webView2, SslErrorHandler sslErrorHandler, SslError sslError) {
            String str;
            super.onReceivedSslError(webView2, sslErrorHandler, sslError);
            try {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put(Globalization.TYPE, InAppBrowser.LOAD_ERROR_EVENT);
                jSONObject.put(ImagesContract.URL, sslError.getUrl());
                jSONObject.put("code", 0);
                jSONObject.put("sslerror", sslError.getPrimaryError());
                switch (sslError.getPrimaryError()) {
                    case 0:
                        str = "The certificate is not yet valid";
                        break;
                    case 1:
                        str = "The certificate has expired";
                        break;
                    case 2:
                        str = "Hostname mismatch";
                        break;
                    case 3:
                        str = "The certificate authority is not trusted";
                        break;
                    case 4:
                        str = "The date of the certificate is invalid";
                        break;
                    default:
                        str = "A generic error occurred";
                        break;
                }
                jSONObject.put("message", str);
                InAppBrowser.this.sendUpdate(jSONObject, true, PluginResult.Status.ERROR);
            } catch (JSONException unused) {
                LOG.m37d(InAppBrowser.LOG_TAG, "Should never happen");
            }
            sslErrorHandler.cancel();
        }

        /* JADX WARNING: Removed duplicated region for block: B:10:0x003d A[SYNTHETIC, Splitter:B:10:0x003d] */
        /* JADX WARNING: Removed duplicated region for block: B:20:0x0077 A[RETURN] */
        /* JADX WARNING: Removed duplicated region for block: B:21:0x0078  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onReceivedHttpAuthRequest(android.webkit.WebView r5, android.webkit.HttpAuthHandler r6, java.lang.String r7, java.lang.String r8) {
            /*
                r4 = this;
                org.apache.cordova.CordovaWebView r0 = r4.webView     // Catch:{ NoSuchMethodException -> 0x0030, IllegalAccessException -> 0x0025, InvocationTargetException -> 0x001a }
                java.lang.Class r0 = r0.getClass()     // Catch:{ NoSuchMethodException -> 0x0030, IllegalAccessException -> 0x0025, InvocationTargetException -> 0x001a }
                java.lang.String r1 = "getPluginManager"
                r2 = 0
                java.lang.Class[] r3 = new java.lang.Class[r2]     // Catch:{ NoSuchMethodException -> 0x0030, IllegalAccessException -> 0x0025, InvocationTargetException -> 0x001a }
                java.lang.reflect.Method r0 = r0.getMethod(r1, r3)     // Catch:{ NoSuchMethodException -> 0x0030, IllegalAccessException -> 0x0025, InvocationTargetException -> 0x001a }
                org.apache.cordova.CordovaWebView r1 = r4.webView     // Catch:{ NoSuchMethodException -> 0x0030, IllegalAccessException -> 0x0025, InvocationTargetException -> 0x001a }
                java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ NoSuchMethodException -> 0x0030, IllegalAccessException -> 0x0025, InvocationTargetException -> 0x001a }
                java.lang.Object r0 = r0.invoke(r1, r2)     // Catch:{ NoSuchMethodException -> 0x0030, IllegalAccessException -> 0x0025, InvocationTargetException -> 0x001a }
                org.apache.cordova.PluginManager r0 = (org.apache.cordova.PluginManager) r0     // Catch:{ NoSuchMethodException -> 0x0030, IllegalAccessException -> 0x0025, InvocationTargetException -> 0x001a }
                goto L_0x003b
            L_0x001a:
                r0 = move-exception
                java.lang.String r1 = "InAppBrowser"
                java.lang.String r0 = r0.getLocalizedMessage()
                org.apache.cordova.LOG.m37d(r1, r0)
                goto L_0x003a
            L_0x0025:
                r0 = move-exception
                java.lang.String r1 = "InAppBrowser"
                java.lang.String r0 = r0.getLocalizedMessage()
                org.apache.cordova.LOG.m37d(r1, r0)
                goto L_0x003a
            L_0x0030:
                r0 = move-exception
                java.lang.String r1 = "InAppBrowser"
                java.lang.String r0 = r0.getLocalizedMessage()
                org.apache.cordova.LOG.m37d(r1, r0)
            L_0x003a:
                r0 = 0
            L_0x003b:
                if (r0 != 0) goto L_0x0068
                org.apache.cordova.CordovaWebView r1 = r4.webView     // Catch:{ NoSuchFieldException -> 0x005e, IllegalAccessException -> 0x0053 }
                java.lang.Class r1 = r1.getClass()     // Catch:{ NoSuchFieldException -> 0x005e, IllegalAccessException -> 0x0053 }
                java.lang.String r2 = "pluginManager"
                java.lang.reflect.Field r1 = r1.getField(r2)     // Catch:{ NoSuchFieldException -> 0x005e, IllegalAccessException -> 0x0053 }
                org.apache.cordova.CordovaWebView r2 = r4.webView     // Catch:{ NoSuchFieldException -> 0x005e, IllegalAccessException -> 0x0053 }
                java.lang.Object r1 = r1.get(r2)     // Catch:{ NoSuchFieldException -> 0x005e, IllegalAccessException -> 0x0053 }
                org.apache.cordova.PluginManager r1 = (org.apache.cordova.PluginManager) r1     // Catch:{ NoSuchFieldException -> 0x005e, IllegalAccessException -> 0x0053 }
                r0 = r1
                goto L_0x0068
            L_0x0053:
                r1 = move-exception
                java.lang.String r2 = "InAppBrowser"
                java.lang.String r1 = r1.getLocalizedMessage()
                org.apache.cordova.LOG.m37d(r2, r1)
                goto L_0x0068
            L_0x005e:
                r1 = move-exception
                java.lang.String r2 = "InAppBrowser"
                java.lang.String r1 = r1.getLocalizedMessage()
                org.apache.cordova.LOG.m37d(r2, r1)
            L_0x0068:
                if (r0 == 0) goto L_0x0078
                org.apache.cordova.CordovaWebView r1 = r4.webView
                org.apache.cordova.CordovaHttpAuthHandler r2 = new org.apache.cordova.CordovaHttpAuthHandler
                r2.<init>(r6)
                boolean r0 = r0.onReceivedHttpAuthRequest(r1, r2, r7, r8)
                if (r0 == 0) goto L_0x0078
                return
            L_0x0078:
                super.onReceivedHttpAuthRequest(r5, r6, r7, r8)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.apache.cordova.inappbrowser.InAppBrowser.InAppBrowserClient.onReceivedHttpAuthRequest(android.webkit.WebView, android.webkit.HttpAuthHandler, java.lang.String, java.lang.String):void");
        }
    }
}
