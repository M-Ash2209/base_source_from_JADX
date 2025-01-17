package com.journeyapps.barcodescanner;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import com.google.zxing.client.android.C0369R;

public class CaptureActivity extends Activity {
    private DecoratedBarcodeView barcodeScannerView;
    private CaptureManager capture;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.barcodeScannerView = initializeContent();
        this.capture = new CaptureManager(this, this.barcodeScannerView);
        this.capture.initializeFromIntent(getIntent(), bundle);
        this.capture.decode();
    }

    /* access modifiers changed from: protected */
    public DecoratedBarcodeView initializeContent() {
        setContentView(C0369R.layout.zxing_capture);
        return (DecoratedBarcodeView) findViewById(C0369R.C0370id.zxing_barcode_scanner);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        this.capture.onResume();
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        this.capture.onPause();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        this.capture.onDestroy();
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        this.capture.onSaveInstanceState(bundle);
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        this.capture.onRequestPermissionsResult(i, strArr, iArr);
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        return this.barcodeScannerView.onKeyDown(i, keyEvent) || super.onKeyDown(i, keyEvent);
    }
}
