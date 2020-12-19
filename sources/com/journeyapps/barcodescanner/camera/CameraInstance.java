package com.journeyapps.barcodescanner.camera;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import com.google.zxing.client.android.C0369R;
import com.journeyapps.barcodescanner.Size;
import com.journeyapps.barcodescanner.Util;

public class CameraInstance {
    /* access modifiers changed from: private */
    public static final String TAG = "CameraInstance";
    /* access modifiers changed from: private */
    public CameraManager cameraManager;
    private CameraSettings cameraSettings = new CameraSettings();
    /* access modifiers changed from: private */
    public CameraThread cameraThread;
    private Runnable closer = new Runnable() {
        public void run() {
            try {
                Log.d(CameraInstance.TAG, "Closing camera");
                CameraInstance.this.cameraManager.stopPreview();
                CameraInstance.this.cameraManager.close();
            } catch (Exception e) {
                Log.e(CameraInstance.TAG, "Failed to close camera", e);
            }
            CameraInstance.this.cameraThread.decrementInstances();
        }
    };
    private Runnable configure = new Runnable() {
        public void run() {
            try {
                Log.d(CameraInstance.TAG, "Configuring camera");
                CameraInstance.this.cameraManager.configure();
                if (CameraInstance.this.readyHandler != null) {
                    CameraInstance.this.readyHandler.obtainMessage(C0369R.C0370id.zxing_prewiew_size_ready, CameraInstance.this.getPreviewSize()).sendToTarget();
                }
            } catch (Exception e) {
                CameraInstance.this.notifyError(e);
                Log.e(CameraInstance.TAG, "Failed to configure camera", e);
            }
        }
    };
    private DisplayConfiguration displayConfiguration;
    private boolean open = false;
    private Runnable opener = new Runnable() {
        public void run() {
            try {
                Log.d(CameraInstance.TAG, "Opening camera");
                CameraInstance.this.cameraManager.open();
            } catch (Exception e) {
                CameraInstance.this.notifyError(e);
                Log.e(CameraInstance.TAG, "Failed to open camera", e);
            }
        }
    };
    private Runnable previewStarter = new Runnable() {
        public void run() {
            try {
                Log.d(CameraInstance.TAG, "Starting preview");
                CameraInstance.this.cameraManager.setPreviewDisplay(CameraInstance.this.surface);
                CameraInstance.this.cameraManager.startPreview();
            } catch (Exception e) {
                CameraInstance.this.notifyError(e);
                Log.e(CameraInstance.TAG, "Failed to start preview", e);
            }
        }
    };
    /* access modifiers changed from: private */
    public Handler readyHandler;
    /* access modifiers changed from: private */
    public CameraSurface surface;

    public CameraInstance(Context context) {
        Util.validateMainThread();
        this.cameraThread = CameraThread.getInstance();
        this.cameraManager = new CameraManager(context);
        this.cameraManager.setCameraSettings(this.cameraSettings);
    }

    public CameraInstance(CameraManager cameraManager2) {
        Util.validateMainThread();
        this.cameraManager = cameraManager2;
    }

    public void setDisplayConfiguration(DisplayConfiguration displayConfiguration2) {
        this.displayConfiguration = displayConfiguration2;
        this.cameraManager.setDisplayConfiguration(displayConfiguration2);
    }

    public DisplayConfiguration getDisplayConfiguration() {
        return this.displayConfiguration;
    }

    public void setReadyHandler(Handler handler) {
        this.readyHandler = handler;
    }

    public void setSurfaceHolder(SurfaceHolder surfaceHolder) {
        setSurface(new CameraSurface(surfaceHolder));
    }

    public void setSurface(CameraSurface cameraSurface) {
        this.surface = cameraSurface;
    }

    public CameraSettings getCameraSettings() {
        return this.cameraSettings;
    }

    public void setCameraSettings(CameraSettings cameraSettings2) {
        if (!this.open) {
            this.cameraSettings = cameraSettings2;
            this.cameraManager.setCameraSettings(cameraSettings2);
        }
    }

    /* access modifiers changed from: private */
    public Size getPreviewSize() {
        return this.cameraManager.getPreviewSize();
    }

    public int getCameraRotation() {
        return this.cameraManager.getCameraRotation();
    }

    public void open() {
        Util.validateMainThread();
        this.open = true;
        this.cameraThread.incrementAndEnqueue(this.opener);
    }

    public void configureCamera() {
        Util.validateMainThread();
        validateOpen();
        this.cameraThread.enqueue(this.configure);
    }

    public void startPreview() {
        Util.validateMainThread();
        validateOpen();
        this.cameraThread.enqueue(this.previewStarter);
    }

    public void setTorch(final boolean z) {
        Util.validateMainThread();
        if (this.open) {
            this.cameraThread.enqueue(new Runnable() {
                public void run() {
                    CameraInstance.this.cameraManager.setTorch(z);
                }
            });
        }
    }

    public void close() {
        Util.validateMainThread();
        if (this.open) {
            this.cameraThread.enqueue(this.closer);
        }
        this.open = false;
    }

    public boolean isOpen() {
        return this.open;
    }

    public void requestPreview(final PreviewCallback previewCallback) {
        validateOpen();
        this.cameraThread.enqueue(new Runnable() {
            public void run() {
                CameraInstance.this.cameraManager.requestPreviewFrame(previewCallback);
            }
        });
    }

    private void validateOpen() {
        if (!this.open) {
            throw new IllegalStateException("CameraInstance is not open");
        }
    }

    /* access modifiers changed from: private */
    public void notifyError(Exception exc) {
        Handler handler = this.readyHandler;
        if (handler != null) {
            handler.obtainMessage(C0369R.C0370id.zxing_camera_error, exc).sendToTarget();
        }
    }

    /* access modifiers changed from: protected */
    public CameraManager getCameraManager() {
        return this.cameraManager;
    }

    /* access modifiers changed from: protected */
    public CameraThread getCameraThread() {
        return this.cameraThread;
    }

    /* access modifiers changed from: protected */
    public CameraSurface getSurface() {
        return this.surface;
    }
}
