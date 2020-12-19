package org.apache.cordova.mediacapture;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import com.adobe.phonegap.push.PushConstants;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
import org.apache.cordova.PermissionHelper;
import org.apache.cordova.mediacapture.PendingRequests;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Capture extends CordovaPlugin {
    private static final String AUDIO_3GPP = "audio/3gpp";
    private static final String[] AUDIO_TYPES = {AUDIO_3GPP, "audio/aac", "audio/amr", "audio/wav"};
    private static final int CAPTURE_AUDIO = 0;
    private static final int CAPTURE_IMAGE = 1;
    private static final int CAPTURE_INTERNAL_ERR = 0;
    private static final int CAPTURE_NOT_SUPPORTED = 20;
    private static final int CAPTURE_NO_MEDIA_FILES = 3;
    private static final int CAPTURE_PERMISSION_DENIED = 4;
    private static final int CAPTURE_VIDEO = 2;
    private static final String IMAGE_JPEG = "image/jpeg";
    private static final String LOG_TAG = "Capture";
    private static final String VIDEO_3GPP = "video/3gpp";
    private static final String VIDEO_MP4 = "video/mp4";
    private boolean cameraPermissionInManifest;
    private Uri imageUri;
    private int numPics;
    private final PendingRequests pendingRequests = new PendingRequests();

    /* access modifiers changed from: protected */
    public void pluginInitialize() {
        super.pluginInitialize();
        this.cameraPermissionInManifest = false;
        try {
            String[] strArr = this.f59cordova.getActivity().getPackageManager().getPackageInfo(this.f59cordova.getActivity().getPackageName(), 4096).requestedPermissions;
            if (strArr != null) {
                for (String equals : strArr) {
                    if (equals.equals("android.permission.CAMERA")) {
                        this.cameraPermissionInManifest = true;
                        return;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            LOG.m41e(LOG_TAG, "Failed checking for CAMERA permission in manifest", (Throwable) e);
        }
    }

    public boolean execute(String str, JSONArray jSONArray, CallbackContext callbackContext) throws JSONException {
        if (str.equals("getFormatData")) {
            callbackContext.success(getFormatData(jSONArray.getString(0), jSONArray.getString(1)));
            return true;
        }
        JSONObject optJSONObject = jSONArray.optJSONObject(0);
        if (str.equals("captureAudio")) {
            captureAudio(this.pendingRequests.createRequest(0, optJSONObject, callbackContext));
        } else if (str.equals("captureImage")) {
            captureImage(this.pendingRequests.createRequest(1, optJSONObject, callbackContext));
        } else if (!str.equals("captureVideo")) {
            return false;
        } else {
            captureVideo(this.pendingRequests.createRequest(2, optJSONObject, callbackContext));
        }
        return true;
    }

    private JSONObject getFormatData(String str, String str2) throws JSONException {
        Uri parse = str.startsWith("file:") ? Uri.parse(str) : Uri.fromFile(new File(str));
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("height", 0);
        jSONObject.put("width", 0);
        jSONObject.put("bitrate", 0);
        jSONObject.put("duration", 0);
        jSONObject.put("codecs", "");
        if (str2 == null || str2.equals("") || "null".equals(str2)) {
            str2 = FileHelper.getMimeType(parse, this.f59cordova);
        }
        LOG.m37d(LOG_TAG, "Mime type = " + str2);
        if (str2.equals(IMAGE_JPEG) || str.endsWith(".jpg")) {
            return getImageData(parse, jSONObject);
        }
        if (Arrays.asList(AUDIO_TYPES).contains(str2)) {
            return getAudioVideoData(str, jSONObject, false);
        }
        if (str2.equals(VIDEO_3GPP) || str2.equals(VIDEO_MP4)) {
            return getAudioVideoData(str, jSONObject, true);
        }
        return jSONObject;
    }

    private JSONObject getImageData(Uri uri, JSONObject jSONObject) throws JSONException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(uri.getPath(), options);
        jSONObject.put("height", options.outHeight);
        jSONObject.put("width", options.outWidth);
        return jSONObject;
    }

    private JSONObject getAudioVideoData(String str, JSONObject jSONObject, boolean z) throws JSONException {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(str);
            mediaPlayer.prepare();
            jSONObject.put("duration", mediaPlayer.getDuration() / 1000);
            if (z) {
                jSONObject.put("height", mediaPlayer.getVideoHeight());
                jSONObject.put("width", mediaPlayer.getVideoWidth());
            }
        } catch (IOException unused) {
            LOG.m37d(LOG_TAG, "Error: loading video file");
        }
        return jSONObject;
    }

    private void captureAudio(PendingRequests.Request request) {
        if (!PermissionHelper.hasPermission(this, "android.permission.READ_EXTERNAL_STORAGE")) {
            PermissionHelper.requestPermission(this, request.requestCode, "android.permission.READ_EXTERNAL_STORAGE");
            return;
        }
        try {
            this.f59cordova.startActivityForResult(this, new Intent("android.provider.MediaStore.RECORD_SOUND"), request.requestCode);
        } catch (ActivityNotFoundException unused) {
            this.pendingRequests.resolveWithFailure(request, createErrorObject(20, "No Activity found to handle Audio Capture."));
        }
    }

    private String getTempDirectoryPath() {
        File cacheDir = this.f59cordova.getActivity().getCacheDir();
        cacheDir.mkdirs();
        return cacheDir.getAbsolutePath();
    }

    private void captureImage(PendingRequests.Request request) {
        boolean z = true;
        boolean z2 = !PermissionHelper.hasPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE");
        if (!this.cameraPermissionInManifest || PermissionHelper.hasPermission(this, "android.permission.CAMERA")) {
            z = false;
        }
        if (!z2 && !z) {
            this.numPics = queryImgDB(whichContentStore()).getCount();
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            ContentResolver contentResolver = this.f59cordova.getActivity().getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put("mime_type", IMAGE_JPEG);
            this.imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            LOG.m37d(LOG_TAG, "Taking a picture and saving to: " + this.imageUri.toString());
            intent.putExtra("output", this.imageUri);
            this.f59cordova.startActivityForResult(this, intent, request.requestCode);
        } else if (z2 && z) {
            PermissionHelper.requestPermissions(this, request.requestCode, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"});
        } else if (z2) {
            PermissionHelper.requestPermission(this, request.requestCode, "android.permission.WRITE_EXTERNAL_STORAGE");
        } else {
            PermissionHelper.requestPermission(this, request.requestCode, "android.permission.CAMERA");
        }
    }

    private static void createWritableFile(File file) throws IOException {
        file.createNewFile();
        file.setWritable(true, false);
    }

    private void captureVideo(PendingRequests.Request request) {
        if (!this.cameraPermissionInManifest || PermissionHelper.hasPermission(this, "android.permission.CAMERA")) {
            Intent intent = new Intent("android.media.action.VIDEO_CAPTURE");
            if (Build.VERSION.SDK_INT > 7) {
                intent.putExtra("android.intent.extra.durationLimit", request.duration);
                intent.putExtra("android.intent.extra.videoQuality", request.quality);
            }
            this.f59cordova.startActivityForResult(this, intent, request.requestCode);
            return;
        }
        PermissionHelper.requestPermission(this, request.requestCode, "android.permission.CAMERA");
    }

    public void onActivityResult(int i, int i2, final Intent intent) {
        final PendingRequests.Request request = this.pendingRequests.get(i);
        if (i2 == -1) {
            this.f59cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    switch (request.action) {
                        case 0:
                            Capture.this.onAudioActivityResult(request, intent);
                            return;
                        case 1:
                            Capture.this.onImageActivityResult(request);
                            return;
                        case 2:
                            Capture.this.onVideoActivityResult(request, intent);
                            return;
                        default:
                            return;
                    }
                }
            });
        } else if (i2 == 0) {
            if (request.results.length() > 0) {
                this.pendingRequests.resolveWithSuccess(request);
            } else {
                this.pendingRequests.resolveWithFailure(request, createErrorObject(3, "Canceled."));
            }
        } else if (request.results.length() > 0) {
            this.pendingRequests.resolveWithSuccess(request);
        } else {
            this.pendingRequests.resolveWithFailure(request, createErrorObject(3, "Did not complete!"));
        }
    }

    public void onAudioActivityResult(PendingRequests.Request request, Intent intent) {
        request.results.put(createMediaFile(intent.getData()));
        if (((long) request.results.length()) >= request.limit) {
            this.pendingRequests.resolveWithSuccess(request);
        } else {
            captureAudio(request);
        }
    }

    public void onImageActivityResult(PendingRequests.Request request) {
        request.results.put(createMediaFile(this.imageUri));
        checkForDuplicateImage();
        if (((long) request.results.length()) >= request.limit) {
            this.pendingRequests.resolveWithSuccess(request);
        } else {
            captureImage(request);
        }
    }

    public void onVideoActivityResult(PendingRequests.Request request, Intent intent) {
        Uri data = intent != null ? intent.getData() : null;
        if (data == null) {
            data = Uri.fromFile(new File(getTempDirectoryPath(), "Capture.avi"));
        }
        if (data == null) {
            this.pendingRequests.resolveWithFailure(request, createErrorObject(3, "Error: data is null"));
            return;
        }
        request.results.put(createMediaFile(data));
        if (((long) request.results.length()) >= request.limit) {
            this.pendingRequests.resolveWithSuccess(request);
        } else {
            captureVideo(request);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:0x0060 A[Catch:{ JSONException -> 0x00c8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0079 A[Catch:{ JSONException -> 0x00c8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x00a6 A[Catch:{ JSONException -> 0x00c8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x00ae A[Catch:{ JSONException -> 0x00c8 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private org.json.JSONObject createMediaFile(android.net.Uri r7) {
        /*
            r6 = this;
            org.apache.cordova.CordovaWebView r0 = r6.webView
            org.apache.cordova.CordovaResourceApi r0 = r0.getResourceApi()
            java.io.File r0 = r0.mapUriToFile(r7)
            org.json.JSONObject r1 = new org.json.JSONObject
            r1.<init>()
            org.apache.cordova.CordovaWebView r2 = r6.webView
            java.lang.Class r2 = r2.getClass()
            java.lang.String r3 = "getPluginManager"
            r4 = 0
            java.lang.Class[] r5 = new java.lang.Class[r4]     // Catch:{ IllegalAccessException | NoSuchMethodException | InvocationTargetException -> 0x0029 }
            java.lang.reflect.Method r3 = r2.getMethod(r3, r5)     // Catch:{ IllegalAccessException | NoSuchMethodException | InvocationTargetException -> 0x0029 }
            org.apache.cordova.CordovaWebView r5 = r6.webView     // Catch:{ IllegalAccessException | NoSuchMethodException | InvocationTargetException -> 0x0029 }
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ IllegalAccessException | NoSuchMethodException | InvocationTargetException -> 0x0029 }
            java.lang.Object r3 = r3.invoke(r5, r4)     // Catch:{ IllegalAccessException | NoSuchMethodException | InvocationTargetException -> 0x0029 }
            org.apache.cordova.PluginManager r3 = (org.apache.cordova.PluginManager) r3     // Catch:{ IllegalAccessException | NoSuchMethodException | InvocationTargetException -> 0x0029 }
            goto L_0x002a
        L_0x0029:
            r3 = 0
        L_0x002a:
            if (r3 != 0) goto L_0x003b
            java.lang.String r4 = "pluginManager"
            java.lang.reflect.Field r2 = r2.getField(r4)     // Catch:{ IllegalAccessException | NoSuchFieldException -> 0x003b }
            org.apache.cordova.CordovaWebView r4 = r6.webView     // Catch:{ IllegalAccessException | NoSuchFieldException -> 0x003b }
            java.lang.Object r2 = r2.get(r4)     // Catch:{ IllegalAccessException | NoSuchFieldException -> 0x003b }
            org.apache.cordova.PluginManager r2 = (org.apache.cordova.PluginManager) r2     // Catch:{ IllegalAccessException | NoSuchFieldException -> 0x003b }
            goto L_0x003c
        L_0x003b:
            r2 = r3
        L_0x003c:
            java.lang.String r3 = "File"
            org.apache.cordova.CordovaPlugin r2 = r2.getPlugin(r3)
            org.apache.cordova.file.FileUtils r2 = (org.apache.cordova.file.FileUtils) r2
            java.lang.String r3 = r0.getAbsolutePath()
            org.apache.cordova.file.LocalFilesystemURL r2 = r2.filesystemURLforLocalPath(r3)
            java.lang.String r3 = "name"
            java.lang.String r4 = r0.getName()     // Catch:{ JSONException -> 0x00c8 }
            r1.put(r3, r4)     // Catch:{ JSONException -> 0x00c8 }
            java.lang.String r3 = "fullPath"
            android.net.Uri r4 = android.net.Uri.fromFile(r0)     // Catch:{ JSONException -> 0x00c8 }
            r1.put(r3, r4)     // Catch:{ JSONException -> 0x00c8 }
            if (r2 == 0) goto L_0x0069
            java.lang.String r3 = "localURL"
            java.lang.String r2 = r2.toString()     // Catch:{ JSONException -> 0x00c8 }
            r1.put(r3, r2)     // Catch:{ JSONException -> 0x00c8 }
        L_0x0069:
            java.io.File r2 = r0.getAbsoluteFile()     // Catch:{ JSONException -> 0x00c8 }
            java.lang.String r2 = r2.toString()     // Catch:{ JSONException -> 0x00c8 }
            java.lang.String r3 = ".3gp"
            boolean r2 = r2.endsWith(r3)     // Catch:{ JSONException -> 0x00c8 }
            if (r2 != 0) goto L_0x009a
            java.io.File r2 = r0.getAbsoluteFile()     // Catch:{ JSONException -> 0x00c8 }
            java.lang.String r2 = r2.toString()     // Catch:{ JSONException -> 0x00c8 }
            java.lang.String r3 = ".3gpp"
            boolean r2 = r2.endsWith(r3)     // Catch:{ JSONException -> 0x00c8 }
            if (r2 == 0) goto L_0x008a
            goto L_0x009a
        L_0x008a:
            java.lang.String r7 = "type"
            android.net.Uri r2 = android.net.Uri.fromFile(r0)     // Catch:{ JSONException -> 0x00c8 }
            org.apache.cordova.CordovaInterface r3 = r6.f59cordova     // Catch:{ JSONException -> 0x00c8 }
            java.lang.String r2 = org.apache.cordova.mediacapture.FileHelper.getMimeType(r2, r3)     // Catch:{ JSONException -> 0x00c8 }
            r1.put(r7, r2)     // Catch:{ JSONException -> 0x00c8 }
            goto L_0x00b5
        L_0x009a:
            java.lang.String r7 = r7.toString()     // Catch:{ JSONException -> 0x00c8 }
            java.lang.String r2 = "/audio/"
            boolean r7 = r7.contains(r2)     // Catch:{ JSONException -> 0x00c8 }
            if (r7 == 0) goto L_0x00ae
            java.lang.String r7 = "type"
            java.lang.String r2 = "audio/3gpp"
            r1.put(r7, r2)     // Catch:{ JSONException -> 0x00c8 }
            goto L_0x00b5
        L_0x00ae:
            java.lang.String r7 = "type"
            java.lang.String r2 = "video/3gpp"
            r1.put(r7, r2)     // Catch:{ JSONException -> 0x00c8 }
        L_0x00b5:
            java.lang.String r7 = "lastModifiedDate"
            long r2 = r0.lastModified()     // Catch:{ JSONException -> 0x00c8 }
            r1.put(r7, r2)     // Catch:{ JSONException -> 0x00c8 }
            java.lang.String r7 = "size"
            long r2 = r0.length()     // Catch:{ JSONException -> 0x00c8 }
            r1.put(r7, r2)     // Catch:{ JSONException -> 0x00c8 }
            goto L_0x00cc
        L_0x00c8:
            r7 = move-exception
            r7.printStackTrace()
        L_0x00cc:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.cordova.mediacapture.Capture.createMediaFile(android.net.Uri):org.json.JSONObject");
    }

    private JSONObject createErrorObject(int i, String str) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("code", i);
            jSONObject.put(PushConstants.MESSAGE, str);
        } catch (JSONException unused) {
        }
        return jSONObject;
    }

    private Cursor queryImgDB(Uri uri) {
        return this.f59cordova.getActivity().getContentResolver().query(uri, new String[]{"_id"}, (String) null, (String[]) null, (String) null);
    }

    private void checkForDuplicateImage() {
        Uri whichContentStore = whichContentStore();
        Cursor queryImgDB = queryImgDB(whichContentStore);
        if (queryImgDB.getCount() - this.numPics == 2) {
            queryImgDB.moveToLast();
            StringBuilder sb = new StringBuilder();
            sb.append(whichContentStore);
            sb.append("/");
            sb.append(Integer.valueOf(queryImgDB.getString(queryImgDB.getColumnIndex("_id"))).intValue() - 1);
            this.f59cordova.getActivity().getContentResolver().delete(Uri.parse(sb.toString()), (String) null, (String[]) null);
        }
    }

    private Uri whichContentStore() {
        if (Environment.getExternalStorageState().equals("mounted")) {
            return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
        return MediaStore.Images.Media.INTERNAL_CONTENT_URI;
    }

    private void executeRequest(PendingRequests.Request request) {
        switch (request.action) {
            case 0:
                captureAudio(request);
                return;
            case 1:
                captureImage(request);
                return;
            case 2:
                captureVideo(request);
                return;
            default:
                return;
        }
    }

    public void onRequestPermissionResult(int i, String[] strArr, int[] iArr) throws JSONException {
        PendingRequests.Request request = this.pendingRequests.get(i);
        if (request != null) {
            int length = iArr.length;
            boolean z = false;
            int i2 = 0;
            while (true) {
                if (i2 >= length) {
                    z = true;
                    break;
                } else if (iArr[i2] == -1) {
                    break;
                } else {
                    i2++;
                }
            }
            if (z) {
                executeRequest(request);
            } else {
                this.pendingRequests.resolveWithFailure(request, createErrorObject(4, "Permission denied."));
            }
        }
    }

    public Bundle onSaveInstanceState() {
        return this.pendingRequests.toBundle();
    }

    public void onRestoreStateForActivityResult(Bundle bundle, CallbackContext callbackContext) {
        this.pendingRequests.setLastSavedState(bundle, callbackContext);
    }
}
