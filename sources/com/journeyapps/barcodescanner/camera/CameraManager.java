package com.journeyapps.barcodescanner.camera;

import android.content.Context;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceHolder;
import com.google.zxing.client.android.AmbientLightManager;
import com.google.zxing.client.android.camera.CameraConfigurationUtils;
import com.google.zxing.client.android.camera.open.OpenCameraInterface;
import com.journeyapps.barcodescanner.Size;
import com.journeyapps.barcodescanner.SourceData;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class CameraManager {
    /* access modifiers changed from: private */
    public static final String TAG = "CameraManager";
    private AmbientLightManager ambientLightManager;
    private AutoFocusManager autoFocusManager;
    private Camera camera;
    private Camera.CameraInfo cameraInfo;
    private final CameraPreviewCallback cameraPreviewCallback;
    private Context context;
    private String defaultParameters;
    private DisplayConfiguration displayConfiguration;
    private Size previewSize;
    private boolean previewing;
    private Size requestedPreviewSize;
    private int rotationDegrees = -1;
    private CameraSettings settings = new CameraSettings();

    private final class CameraPreviewCallback implements Camera.PreviewCallback {
        private PreviewCallback callback;
        private Size resolution;

        public CameraPreviewCallback() {
        }

        public void setResolution(Size size) {
            this.resolution = size;
        }

        public void setCallback(PreviewCallback previewCallback) {
            this.callback = previewCallback;
        }

        public void onPreviewFrame(byte[] bArr, Camera camera) {
            Size size = this.resolution;
            PreviewCallback previewCallback = this.callback;
            if (size == null || previewCallback == null) {
                Log.d(CameraManager.TAG, "Got preview callback, but no handler or resolution available");
                return;
            }
            byte[] bArr2 = bArr;
            previewCallback.onPreview(new SourceData(bArr2, size.width, size.height, camera.getParameters().getPreviewFormat(), CameraManager.this.getCameraRotation()));
        }
    }

    public CameraManager(Context context2) {
        this.context = context2;
        this.cameraPreviewCallback = new CameraPreviewCallback();
    }

    public void open() {
        this.camera = OpenCameraInterface.open(this.settings.getRequestedCameraId());
        if (this.camera != null) {
            int cameraId = OpenCameraInterface.getCameraId(this.settings.getRequestedCameraId());
            this.cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(cameraId, this.cameraInfo);
            return;
        }
        throw new RuntimeException("Failed to open camera");
    }

    public void configure() {
        if (this.camera != null) {
            setParameters();
            return;
        }
        throw new RuntimeException("Camera not open");
    }

    public void setPreviewDisplay(SurfaceHolder surfaceHolder) throws IOException {
        setPreviewDisplay(new CameraSurface(surfaceHolder));
    }

    public void setPreviewDisplay(CameraSurface cameraSurface) throws IOException {
        cameraSurface.setPreview(this.camera);
    }

    public void startPreview() {
        Camera camera2 = this.camera;
        if (camera2 != null && !this.previewing) {
            camera2.startPreview();
            this.previewing = true;
            this.autoFocusManager = new AutoFocusManager(this.camera, this.settings);
            this.ambientLightManager = new AmbientLightManager(this.context, this, this.settings);
            this.ambientLightManager.start();
        }
    }

    public void stopPreview() {
        AutoFocusManager autoFocusManager2 = this.autoFocusManager;
        if (autoFocusManager2 != null) {
            autoFocusManager2.stop();
            this.autoFocusManager = null;
        }
        AmbientLightManager ambientLightManager2 = this.ambientLightManager;
        if (ambientLightManager2 != null) {
            ambientLightManager2.stop();
            this.ambientLightManager = null;
        }
        Camera camera2 = this.camera;
        if (camera2 != null && this.previewing) {
            camera2.stopPreview();
            this.cameraPreviewCallback.setCallback((PreviewCallback) null);
            this.previewing = false;
        }
    }

    public void close() {
        Camera camera2 = this.camera;
        if (camera2 != null) {
            camera2.release();
            this.camera = null;
        }
    }

    public boolean isCameraRotated() {
        int i = this.rotationDegrees;
        if (i != -1) {
            return i % 180 != 0;
        }
        throw new IllegalStateException("Rotation not calculated yet. Call configure() first.");
    }

    public int getCameraRotation() {
        return this.rotationDegrees;
    }

    private Camera.Parameters getDefaultCameraParameters() {
        Camera.Parameters parameters = this.camera.getParameters();
        String str = this.defaultParameters;
        if (str == null) {
            this.defaultParameters = parameters.flatten();
        } else {
            parameters.unflatten(str);
        }
        return parameters;
    }

    private void setDesiredParameters(boolean z) {
        Camera.Parameters defaultCameraParameters = getDefaultCameraParameters();
        if (defaultCameraParameters == null) {
            Log.w(TAG, "Device error: no camera parameters are available. Proceeding without configuration.");
            return;
        }
        String str = TAG;
        Log.i(str, "Initial camera parameters: " + defaultCameraParameters.flatten());
        if (z) {
            Log.w(TAG, "In camera config safe mode -- most settings will not be honored");
        }
        CameraConfigurationUtils.setFocus(defaultCameraParameters, this.settings.getFocusMode(), z);
        if (!z) {
            CameraConfigurationUtils.setTorch(defaultCameraParameters, false);
            if (this.settings.isScanInverted()) {
                CameraConfigurationUtils.setInvertColor(defaultCameraParameters);
            }
            if (this.settings.isBarcodeSceneModeEnabled()) {
                CameraConfigurationUtils.setBarcodeSceneMode(defaultCameraParameters);
            }
            if (this.settings.isMeteringEnabled() && Build.VERSION.SDK_INT >= 15) {
                CameraConfigurationUtils.setVideoStabilization(defaultCameraParameters);
                CameraConfigurationUtils.setFocusArea(defaultCameraParameters);
                CameraConfigurationUtils.setMetering(defaultCameraParameters);
            }
        }
        List<Size> previewSizes = getPreviewSizes(defaultCameraParameters);
        if (previewSizes.size() == 0) {
            this.requestedPreviewSize = null;
        } else {
            this.requestedPreviewSize = this.displayConfiguration.getBestPreviewSize(previewSizes, isCameraRotated());
            defaultCameraParameters.setPreviewSize(this.requestedPreviewSize.width, this.requestedPreviewSize.height);
        }
        if (Build.DEVICE.equals("glass-1")) {
            CameraConfigurationUtils.setBestPreviewFPS(defaultCameraParameters);
        }
        String str2 = TAG;
        Log.i(str2, "Final camera parameters: " + defaultCameraParameters.flatten());
        this.camera.setParameters(defaultCameraParameters);
    }

    private static List<Size> getPreviewSizes(Camera.Parameters parameters) {
        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        ArrayList arrayList = new ArrayList();
        if (supportedPreviewSizes == null) {
            Camera.Size previewSize2 = parameters.getPreviewSize();
            if (previewSize2 != null) {
                arrayList.add(new Size(previewSize2.width, previewSize2.height));
            }
            return arrayList;
        }
        for (Camera.Size next : supportedPreviewSizes) {
            arrayList.add(new Size(next.width, next.height));
        }
        return arrayList;
    }

    private int calculateDisplayRotation() {
        int i;
        int i2 = 0;
        switch (this.displayConfiguration.getRotation()) {
            case 1:
                i2 = 90;
                break;
            case 2:
                i2 = 180;
                break;
            case 3:
                i2 = 270;
                break;
        }
        if (this.cameraInfo.facing == 1) {
            i = (360 - ((this.cameraInfo.orientation + i2) % 360)) % 360;
        } else {
            i = ((this.cameraInfo.orientation - i2) + 360) % 360;
        }
        String str = TAG;
        Log.i(str, "Camera Display Orientation: " + i);
        return i;
    }

    private void setCameraDisplayOrientation(int i) {
        this.camera.setDisplayOrientation(i);
    }

    private void setParameters() {
        try {
            this.rotationDegrees = calculateDisplayRotation();
            setCameraDisplayOrientation(this.rotationDegrees);
        } catch (Exception unused) {
            Log.w(TAG, "Failed to set rotation.");
        }
        try {
            setDesiredParameters(false);
        } catch (Exception unused2) {
            try {
                setDesiredParameters(true);
            } catch (Exception unused3) {
                Log.w(TAG, "Camera rejected even safe-mode parameters! No configuration");
            }
        }
        Camera.Size previewSize2 = this.camera.getParameters().getPreviewSize();
        if (previewSize2 == null) {
            this.previewSize = this.requestedPreviewSize;
        } else {
            this.previewSize = new Size(previewSize2.width, previewSize2.height);
        }
        this.cameraPreviewCallback.setResolution(this.previewSize);
    }

    public boolean isOpen() {
        return this.camera != null;
    }

    public Size getNaturalPreviewSize() {
        return this.previewSize;
    }

    public Size getPreviewSize() {
        if (this.previewSize == null) {
            return null;
        }
        if (isCameraRotated()) {
            return this.previewSize.rotate();
        }
        return this.previewSize;
    }

    public void requestPreviewFrame(PreviewCallback previewCallback) {
        Camera camera2 = this.camera;
        if (camera2 != null && this.previewing) {
            this.cameraPreviewCallback.setCallback(previewCallback);
            camera2.setOneShotPreviewCallback(this.cameraPreviewCallback);
        }
    }

    public CameraSettings getCameraSettings() {
        return this.settings;
    }

    public void setCameraSettings(CameraSettings cameraSettings) {
        this.settings = cameraSettings;
    }

    public DisplayConfiguration getDisplayConfiguration() {
        return this.displayConfiguration;
    }

    public void setDisplayConfiguration(DisplayConfiguration displayConfiguration2) {
        this.displayConfiguration = displayConfiguration2;
    }

    public void setTorch(boolean z) {
        if (this.camera != null && z != isTorchOn()) {
            AutoFocusManager autoFocusManager2 = this.autoFocusManager;
            if (autoFocusManager2 != null) {
                autoFocusManager2.stop();
            }
            Camera.Parameters parameters = this.camera.getParameters();
            CameraConfigurationUtils.setTorch(parameters, z);
            if (this.settings.isExposureEnabled()) {
                CameraConfigurationUtils.setBestExposure(parameters, z);
            }
            this.camera.setParameters(parameters);
            AutoFocusManager autoFocusManager3 = this.autoFocusManager;
            if (autoFocusManager3 != null) {
                autoFocusManager3.start();
            }
        }
    }

    public boolean isTorchOn() {
        String flashMode;
        Camera.Parameters parameters = this.camera.getParameters();
        if (parameters == null || (flashMode = parameters.getFlashMode()) == null) {
            return false;
        }
        if ("on".equals(flashMode) || "torch".equals(flashMode)) {
            return true;
        }
        return false;
    }

    public Camera getCamera() {
        return this.camera;
    }
}
