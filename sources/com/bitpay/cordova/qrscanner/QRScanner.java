package com.bitpay.cordova.qrscanner;

import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.net.Uri;
import android.support.p000v4.app.ActivityCompat;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;
import com.journeyapps.barcodescanner.camera.CameraSettings;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PermissionHelper;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class QRScanner extends CordovaPlugin implements BarcodeCallback {
    private static Boolean flashAvailable;
    private boolean appPausedWithActivePreview = false;
    private boolean authorized;
    private CallbackContext callbackContext;
    /* access modifiers changed from: private */
    public boolean cameraClosing;
    /* access modifiers changed from: private */
    public boolean cameraPreviewing;
    /* access modifiers changed from: private */
    public int currentCameraId = 0;
    private boolean denied;
    private boolean keepDenied = false;
    /* access modifiers changed from: private */
    public boolean lightOn = false;
    /* access modifiers changed from: private */
    public BarcodeView mBarcodeView;
    private CallbackContext nextScanCallback;
    private boolean oneTime = true;
    private String[] permissions = {"android.permission.CAMERA"};
    private boolean prepared = false;
    /* access modifiers changed from: private */
    public boolean previewing = false;
    private boolean restricted;
    /* access modifiers changed from: private */
    public boolean scanning = false;
    private boolean shouldScanAgain;
    /* access modifiers changed from: private */
    public boolean showing = false;
    /* access modifiers changed from: private */
    public boolean switchFlashOff = false;
    /* access modifiers changed from: private */
    public boolean switchFlashOn = false;

    public void possibleResultPoints(List<ResultPoint> list) {
    }

    static class QRScannerError {
        private static final int BACK_CAMERA_UNAVAILABLE = 3;
        private static final int CAMERA_ACCESS_DENIED = 1;
        private static final int CAMERA_ACCESS_RESTRICTED = 2;
        private static final int CAMERA_UNAVAILABLE = 5;
        private static final int FRONT_CAMERA_UNAVAILABLE = 4;
        private static final int LIGHT_UNAVAILABLE = 7;
        private static final int OPEN_SETTINGS_UNAVAILABLE = 8;
        private static final int SCAN_CANCELED = 6;
        private static final int UNEXPECTED_ERROR = 0;

        QRScannerError() {
        }
    }

    public boolean execute(String str, final JSONArray jSONArray, final CallbackContext callbackContext2) throws JSONException {
        this.callbackContext = callbackContext2;
        try {
            if (str.equals("show")) {
                this.f59cordova.getThreadPool().execute(new Runnable() {
                    public void run() {
                        QRScanner.this.show(callbackContext2);
                    }
                });
                return true;
            } else if (str.equals("scan")) {
                this.f59cordova.getThreadPool().execute(new Runnable() {
                    public void run() {
                        QRScanner.this.scan(callbackContext2);
                    }
                });
                return true;
            } else if (str.equals("cancelScan")) {
                this.f59cordova.getThreadPool().execute(new Runnable() {
                    public void run() {
                        QRScanner.this.cancelScan(callbackContext2);
                    }
                });
                return true;
            } else if (str.equals("openSettings")) {
                this.f59cordova.getThreadPool().execute(new Runnable() {
                    public void run() {
                        QRScanner.this.openSettings(callbackContext2);
                    }
                });
                return true;
            } else if (str.equals("pausePreview")) {
                this.f59cordova.getThreadPool().execute(new Runnable() {
                    public void run() {
                        QRScanner.this.pausePreview(callbackContext2);
                    }
                });
                return true;
            } else if (str.equals("useCamera")) {
                this.f59cordova.getThreadPool().execute(new Runnable() {
                    public void run() {
                        QRScanner.this.switchCamera(callbackContext2, jSONArray);
                    }
                });
                return true;
            } else if (str.equals("resumePreview")) {
                this.f59cordova.getThreadPool().execute(new Runnable() {
                    public void run() {
                        QRScanner.this.resumePreview(callbackContext2);
                    }
                });
                return true;
            } else if (str.equals("hide")) {
                this.f59cordova.getThreadPool().execute(new Runnable() {
                    public void run() {
                        QRScanner.this.hide(callbackContext2);
                    }
                });
                return true;
            } else if (str.equals("enableLight")) {
                this.f59cordova.getThreadPool().execute(new Runnable() {
                    public void run() {
                        while (QRScanner.this.cameraClosing) {
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException unused) {
                            }
                        }
                        boolean unused2 = QRScanner.this.switchFlashOn = true;
                        if (!QRScanner.this.hasFlash()) {
                            callbackContext2.error(7);
                        } else if (!QRScanner.this.hasPermission()) {
                            QRScanner.this.requestPermission(33);
                        } else {
                            QRScanner.this.enableLight(callbackContext2);
                        }
                    }
                });
                return true;
            } else if (str.equals("disableLight")) {
                this.f59cordova.getThreadPool().execute(new Runnable() {
                    public void run() {
                        boolean unused = QRScanner.this.switchFlashOff = true;
                        if (!QRScanner.this.hasFlash()) {
                            callbackContext2.error(7);
                        } else if (!QRScanner.this.hasPermission()) {
                            QRScanner.this.requestPermission(33);
                        } else {
                            QRScanner.this.disableLight(callbackContext2);
                        }
                    }
                });
                return true;
            } else if (str.equals("prepare")) {
                this.f59cordova.getThreadPool().execute(new Runnable() {
                    public void run() {
                        QRScanner.this.f59cordova.getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                try {
                                    int unused = QRScanner.this.currentCameraId = jSONArray.getInt(0);
                                } catch (JSONException unused2) {
                                }
                                QRScanner.this.prepare(callbackContext2);
                            }
                        });
                    }
                });
                return true;
            } else if (str.equals("destroy")) {
                this.f59cordova.getThreadPool().execute(new Runnable() {
                    public void run() {
                        QRScanner.this.destroy(callbackContext2);
                    }
                });
                return true;
            } else if (!str.equals("getStatus")) {
                return false;
            } else {
                this.f59cordova.getThreadPool().execute(new Runnable() {
                    public void run() {
                        QRScanner.this.getStatus(callbackContext2);
                    }
                });
                return true;
            }
        } catch (Exception unused) {
            callbackContext2.error(0);
            return false;
        }
    }

    public void onPause(boolean z) {
        if (this.previewing) {
            this.appPausedWithActivePreview = true;
            pausePreview((CallbackContext) null);
        }
    }

    public void onResume(boolean z) {
        if (this.appPausedWithActivePreview) {
            this.appPausedWithActivePreview = false;
            resumePreview((CallbackContext) null);
        }
    }

    /* access modifiers changed from: private */
    public boolean hasFlash() {
        if (flashAvailable == null) {
            int i = 0;
            flashAvailable = false;
            FeatureInfo[] systemAvailableFeatures = this.f59cordova.getActivity().getPackageManager().getSystemAvailableFeatures();
            int length = systemAvailableFeatures.length;
            while (true) {
                if (i >= length) {
                    break;
                } else if ("android.hardware.camera.flash".equalsIgnoreCase(systemAvailableFeatures[i].name)) {
                    flashAvailable = true;
                    break;
                } else {
                    i++;
                }
            }
        }
        return flashAvailable.booleanValue();
    }

    private void switchFlash(boolean z, CallbackContext callbackContext2) {
        try {
            if (hasFlash()) {
                doswitchFlash(z, callbackContext2);
            } else {
                callbackContext2.error(7);
            }
        } catch (Exception unused) {
            this.lightOn = false;
            callbackContext2.error(7);
        }
    }

    private String boolToNumberString(Boolean bool) {
        return bool.booleanValue() ? "1" : "0";
    }

    private void doswitchFlash(final boolean z, final CallbackContext callbackContext2) throws IOException, CameraAccessException {
        if (getCurrentCameraId() == 1) {
            callbackContext2.error(7);
            return;
        }
        if (!this.prepared) {
            if (z) {
                this.lightOn = true;
            } else {
                this.lightOn = false;
            }
            prepare(callbackContext2);
        }
        this.f59cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                if (QRScanner.this.mBarcodeView != null) {
                    QRScanner.this.mBarcodeView.setTorch(z);
                    if (z) {
                        boolean unused = QRScanner.this.lightOn = true;
                    } else {
                        boolean unused2 = QRScanner.this.lightOn = false;
                    }
                }
                QRScanner.this.getStatus(callbackContext2);
            }
        });
    }

    public int getCurrentCameraId() {
        return this.currentCameraId;
    }

    private boolean canChangeCamera() {
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(i, cameraInfo);
            if (1 == cameraInfo.facing) {
                return true;
            }
        }
        return false;
    }

    public void switchCamera(CallbackContext callbackContext2, JSONArray jSONArray) {
        int i;
        try {
            i = jSONArray.getInt(0);
        } catch (JSONException unused) {
            callbackContext2.error(0);
            i = 0;
        }
        this.currentCameraId = i;
        if (this.scanning) {
            this.scanning = false;
            this.prepared = false;
            if (this.cameraPreviewing) {
                this.f59cordova.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        ((ViewGroup) QRScanner.this.mBarcodeView.getParent()).removeView(QRScanner.this.mBarcodeView);
                        boolean unused = QRScanner.this.cameraPreviewing = false;
                    }
                });
            }
            closeCamera();
            prepare(callbackContext2);
            scan(this.nextScanCallback);
            return;
        }
        prepare(callbackContext2);
    }

    public void onRequestPermissionResult(int i, String[] strArr, int[] iArr) throws JSONException {
        this.oneTime = false;
        if (i == 33) {
            int i2 = 0;
            while (i2 < strArr.length) {
                String str = strArr[i2];
                if (iArr[i2] != -1) {
                    if (iArr[i2] == 0) {
                        this.authorized = true;
                        this.denied = false;
                        if (i == 33) {
                            if (this.switchFlashOn && !this.scanning && !this.switchFlashOff) {
                                switchFlash(true, this.callbackContext);
                            } else if (!this.switchFlashOff || this.scanning) {
                                setupCamera(this.callbackContext);
                                if (!this.scanning) {
                                    getStatus(this.callbackContext);
                                }
                            } else {
                                switchFlash(false, this.callbackContext);
                            }
                        }
                    } else {
                        this.authorized = false;
                        this.denied = false;
                        this.restricted = false;
                    }
                    i2++;
                } else if (!ActivityCompat.shouldShowRequestPermissionRationale(this.f59cordova.getActivity(), str)) {
                    this.denied = true;
                    this.authorized = false;
                    this.callbackContext.error(1);
                    return;
                } else {
                    this.authorized = false;
                    this.denied = false;
                    this.callbackContext.error(1);
                    return;
                }
            }
        }
    }

    public boolean hasPermission() {
        for (String hasPermission : this.permissions) {
            if (!PermissionHelper.hasPermission(this, hasPermission)) {
                return false;
            }
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void requestPermission(int i) {
        PermissionHelper.requestPermissions(this, i, this.permissions);
    }

    private void closeCamera() {
        this.cameraClosing = true;
        this.f59cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                if (QRScanner.this.mBarcodeView != null) {
                    QRScanner.this.mBarcodeView.pause();
                }
                boolean unused = QRScanner.this.cameraClosing = false;
            }
        });
    }

    private void makeOpaque() {
        this.f59cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                QRScanner.this.webView.getView().setBackgroundColor(0);
            }
        });
        this.showing = false;
    }

    private boolean hasCamera() {
        return this.f59cordova.getActivity().getPackageManager().hasSystemFeature("android.hardware.camera");
    }

    private boolean hasFrontCamera() {
        return this.f59cordova.getActivity().getPackageManager().hasSystemFeature("android.hardware.camera.front");
    }

    private void setupCamera(CallbackContext callbackContext2) {
        this.f59cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                QRScanner qRScanner = QRScanner.this;
                BarcodeView unused = qRScanner.mBarcodeView = new BarcodeView(qRScanner.f59cordova.getActivity());
                ArrayList arrayList = new ArrayList();
                arrayList.add(BarcodeFormat.QR_CODE);
                QRScanner.this.mBarcodeView.setDecoderFactory(new DefaultDecoderFactory(arrayList, (Map<DecodeHintType, ?>) null, (String) null));
                CameraSettings cameraSettings = new CameraSettings();
                cameraSettings.setRequestedCameraId(QRScanner.this.getCurrentCameraId());
                QRScanner.this.mBarcodeView.setCameraSettings(cameraSettings);
                ((ViewGroup) QRScanner.this.webView.getView().getParent()).addView(QRScanner.this.mBarcodeView, new FrameLayout.LayoutParams(-2, -2));
                boolean unused2 = QRScanner.this.cameraPreviewing = true;
                QRScanner.this.webView.getView().bringToFront();
                QRScanner.this.mBarcodeView.resume();
            }
        });
        this.prepared = true;
        this.previewing = true;
        if (this.shouldScanAgain) {
            scan(callbackContext2);
        }
    }

    public void barcodeResult(BarcodeResult barcodeResult) {
        if (this.nextScanCallback != null) {
            if (barcodeResult.getText() != null) {
                this.scanning = false;
                this.nextScanCallback.success(barcodeResult.getText());
                this.nextScanCallback = null;
                return;
            }
            scan(this.nextScanCallback);
        }
    }

    /* access modifiers changed from: private */
    public void prepare(CallbackContext callbackContext2) {
        if (!this.prepared) {
            int i = this.currentCameraId;
            if (i == 0) {
                if (!hasCamera()) {
                    callbackContext2.error(3);
                } else if (!hasPermission()) {
                    requestPermission(33);
                } else {
                    setupCamera(callbackContext2);
                    if (!this.scanning) {
                        getStatus(callbackContext2);
                    }
                }
            } else if (i != 1) {
                callbackContext2.error(5);
            } else if (!hasFrontCamera()) {
                callbackContext2.error(4);
            } else if (!hasPermission()) {
                requestPermission(33);
            } else {
                setupCamera(callbackContext2);
                if (!this.scanning) {
                    getStatus(callbackContext2);
                }
            }
        } else {
            this.prepared = false;
            this.f59cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    QRScanner.this.mBarcodeView.pause();
                }
            });
            if (this.cameraPreviewing) {
                this.f59cordova.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        ((ViewGroup) QRScanner.this.mBarcodeView.getParent()).removeView(QRScanner.this.mBarcodeView);
                        boolean unused = QRScanner.this.cameraPreviewing = false;
                    }
                });
                this.previewing = true;
                this.lightOn = false;
            }
            setupCamera(callbackContext2);
            getStatus(callbackContext2);
        }
    }

    /* access modifiers changed from: private */
    public void scan(CallbackContext callbackContext2) {
        this.scanning = true;
        if (!this.prepared) {
            this.shouldScanAgain = true;
            if (!hasCamera()) {
                return;
            }
            if (!hasPermission()) {
                requestPermission(33);
            } else {
                setupCamera(callbackContext2);
            }
        } else {
            if (!this.previewing) {
                this.f59cordova.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        if (QRScanner.this.mBarcodeView != null) {
                            QRScanner.this.mBarcodeView.resume();
                            boolean unused = QRScanner.this.previewing = true;
                            if (QRScanner.this.switchFlashOn) {
                                boolean unused2 = QRScanner.this.lightOn = true;
                            }
                        }
                    }
                });
            }
            this.shouldScanAgain = false;
            this.nextScanCallback = callbackContext2;
            this.f59cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    if (QRScanner.this.mBarcodeView != null) {
                        QRScanner.this.mBarcodeView.decodeSingle(this);
                    }
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void cancelScan(CallbackContext callbackContext2) {
        this.f59cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                boolean unused = QRScanner.this.scanning = false;
                if (QRScanner.this.mBarcodeView != null) {
                    QRScanner.this.mBarcodeView.stopDecoding();
                }
            }
        });
        CallbackContext callbackContext3 = this.nextScanCallback;
        if (callbackContext3 != null) {
            callbackContext3.error(6);
        }
        this.nextScanCallback = null;
    }

    /* access modifiers changed from: private */
    public void show(final CallbackContext callbackContext2) {
        this.f59cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                QRScanner.this.webView.getView().setBackgroundColor(Color.argb(1, 0, 0, 0));
                boolean unused = QRScanner.this.showing = true;
                QRScanner.this.getStatus(callbackContext2);
            }
        });
    }

    /* access modifiers changed from: private */
    public void hide(CallbackContext callbackContext2) {
        makeOpaque();
        getStatus(callbackContext2);
    }

    /* access modifiers changed from: private */
    public void pausePreview(final CallbackContext callbackContext2) {
        this.f59cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                if (QRScanner.this.mBarcodeView != null) {
                    QRScanner.this.mBarcodeView.pause();
                    boolean unused = QRScanner.this.previewing = false;
                    if (QRScanner.this.lightOn) {
                        boolean unused2 = QRScanner.this.lightOn = false;
                    }
                }
                CallbackContext callbackContext = callbackContext2;
                if (callbackContext != null) {
                    QRScanner.this.getStatus(callbackContext);
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void resumePreview(final CallbackContext callbackContext2) {
        this.f59cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                if (QRScanner.this.mBarcodeView != null) {
                    QRScanner.this.mBarcodeView.resume();
                    boolean unused = QRScanner.this.previewing = true;
                    if (QRScanner.this.switchFlashOn) {
                        boolean unused2 = QRScanner.this.lightOn = true;
                    }
                }
                CallbackContext callbackContext = callbackContext2;
                if (callbackContext != null) {
                    QRScanner.this.getStatus(callbackContext);
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void enableLight(CallbackContext callbackContext2) {
        this.lightOn = true;
        if (hasPermission()) {
            switchFlash(true, callbackContext2);
        } else {
            callbackContext2.error(1);
        }
    }

    /* access modifiers changed from: private */
    public void disableLight(CallbackContext callbackContext2) {
        this.lightOn = false;
        this.switchFlashOn = false;
        if (hasPermission()) {
            switchFlash(false, callbackContext2);
        } else {
            callbackContext2.error(1);
        }
    }

    /* access modifiers changed from: private */
    public void openSettings(CallbackContext callbackContext2) {
        this.oneTime = true;
        if (this.denied) {
            this.keepDenied = true;
        }
        try {
            this.denied = false;
            this.authorized = false;
            boolean z = this.prepared;
            boolean z2 = this.lightOn;
            boolean z3 = this.showing;
            if (this.prepared) {
                destroy(callbackContext2);
            }
            this.lightOn = false;
            Intent intent = new Intent();
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setFlags(268435456);
            intent.setData(Uri.fromParts("package", this.f59cordova.getActivity().getPackageName(), (String) null));
            this.f59cordova.getActivity().getApplicationContext().startActivity(intent);
            getStatus(callbackContext2);
            if (z) {
                prepare(callbackContext2);
            }
            if (z2) {
                enableLight(callbackContext2);
            }
            if (z3) {
                show(callbackContext2);
            }
        } catch (Exception unused) {
            callbackContext2.error(8);
        }
    }

    /* access modifiers changed from: private */
    public void getStatus(CallbackContext callbackContext2) {
        if (this.oneTime) {
            boolean hasPermission = hasPermission();
            this.authorized = false;
            if (hasPermission) {
                this.authorized = true;
            }
            if (!this.keepDenied || this.authorized) {
                this.denied = false;
            } else {
                this.denied = true;
            }
            this.restricted = false;
        }
        boolean hasFlash = hasFlash();
        if (this.currentCameraId == 1) {
            hasFlash = false;
        }
        HashMap hashMap = new HashMap();
        hashMap.put("authorized", boolToNumberString(Boolean.valueOf(this.authorized)));
        hashMap.put("denied", boolToNumberString(Boolean.valueOf(this.denied)));
        hashMap.put("restricted", boolToNumberString(Boolean.valueOf(this.restricted)));
        hashMap.put("prepared", boolToNumberString(Boolean.valueOf(this.prepared)));
        hashMap.put("scanning", boolToNumberString(Boolean.valueOf(this.scanning)));
        hashMap.put("previewing", boolToNumberString(Boolean.valueOf(this.previewing)));
        hashMap.put("showing", boolToNumberString(Boolean.valueOf(this.showing)));
        hashMap.put("lightEnabled", boolToNumberString(Boolean.valueOf(this.lightOn)));
        hashMap.put("canOpenSettings", boolToNumberString(true));
        hashMap.put("canEnableLight", boolToNumberString(Boolean.valueOf(hasFlash)));
        hashMap.put("canChangeCamera", boolToNumberString(Boolean.valueOf(canChangeCamera())));
        hashMap.put("currentCamera", Integer.toString(getCurrentCameraId()));
        callbackContext2.sendPluginResult(new PluginResult(PluginResult.Status.OK, new JSONObject(hashMap)));
    }

    /* access modifiers changed from: private */
    public void destroy(CallbackContext callbackContext2) {
        this.prepared = false;
        makeOpaque();
        this.previewing = false;
        if (this.scanning) {
            this.f59cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    boolean unused = QRScanner.this.scanning = false;
                    if (QRScanner.this.mBarcodeView != null) {
                        QRScanner.this.mBarcodeView.stopDecoding();
                    }
                }
            });
            this.nextScanCallback = null;
        }
        if (this.cameraPreviewing) {
            this.f59cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    ((ViewGroup) QRScanner.this.mBarcodeView.getParent()).removeView(QRScanner.this.mBarcodeView);
                    boolean unused = QRScanner.this.cameraPreviewing = false;
                }
            });
        }
        if (this.currentCameraId != 1 && this.lightOn) {
            switchFlash(false, callbackContext2);
        }
        closeCamera();
        this.currentCameraId = 0;
        getStatus(callbackContext2);
    }
}
