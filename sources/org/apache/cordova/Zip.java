package org.apache.cordova;

import android.net.Uri;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONObject;

public class Zip extends CordovaPlugin {
    private static final String LOG_TAG = "Zip";

    public boolean execute(String str, CordovaArgs cordovaArgs, CallbackContext callbackContext) throws JSONException {
        if (!"unzip".equals(str)) {
            return false;
        }
        unzip(cordovaArgs, callbackContext);
        return true;
    }

    private void unzip(final CordovaArgs cordovaArgs, final CallbackContext callbackContext) {
        this.f59cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                Zip.this.unzipSync(cordovaArgs, callbackContext);
            }
        });
    }

    private static int readInt(InputStream inputStream) throws IOException {
        int read = inputStream.read();
        int read2 = inputStream.read();
        int read3 = inputStream.read();
        return (inputStream.read() << 24) | read | (read2 << 8) | (read3 << 16);
    }

    /* JADX WARNING: type inference failed for: r4v2, types: [java.io.BufferedInputStream, java.io.InputStream] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x0179 A[SYNTHETIC, Splitter:B:68:0x0179] */
    /* JADX WARNING: Removed duplicated region for block: B:78:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void unzipSync(org.apache.cordova.CordovaArgs r11, org.apache.cordova.CallbackContext r12) {
        /*
            r10 = this;
            r0 = 0
            r1 = 0
            java.lang.String r2 = r11.getString(r1)     // Catch:{ Exception -> 0x0168 }
            r3 = 1
            java.lang.String r11 = r11.getString(r3)     // Catch:{ Exception -> 0x0168 }
            android.net.Uri r2 = r10.getUriForArg(r2)     // Catch:{ Exception -> 0x0168 }
            android.net.Uri r11 = r10.getUriForArg(r11)     // Catch:{ Exception -> 0x0168 }
            org.apache.cordova.CordovaWebView r4 = r10.webView     // Catch:{ Exception -> 0x0168 }
            org.apache.cordova.CordovaResourceApi r4 = r4.getResourceApi()     // Catch:{ Exception -> 0x0168 }
            java.io.File r5 = r4.mapUriToFile(r2)     // Catch:{ Exception -> 0x0168 }
            if (r5 == 0) goto L_0x015b
            boolean r5 = r5.exists()     // Catch:{ Exception -> 0x0168 }
            if (r5 != 0) goto L_0x0027
            goto L_0x015b
        L_0x0027:
            java.io.File r11 = r4.mapUriToFile(r11)     // Catch:{ Exception -> 0x0168 }
            java.lang.String r5 = r11.getAbsolutePath()     // Catch:{ Exception -> 0x0168 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0168 }
            r6.<init>()     // Catch:{ Exception -> 0x0168 }
            r6.append(r5)     // Catch:{ Exception -> 0x0168 }
            java.lang.String r7 = java.io.File.separator     // Catch:{ Exception -> 0x0168 }
            boolean r5 = r5.endsWith(r7)     // Catch:{ Exception -> 0x0168 }
            if (r5 == 0) goto L_0x0042
            java.lang.String r5 = ""
            goto L_0x0044
        L_0x0042:
            java.lang.String r5 = java.io.File.separator     // Catch:{ Exception -> 0x0168 }
        L_0x0044:
            r6.append(r5)     // Catch:{ Exception -> 0x0168 }
            java.lang.String r5 = r6.toString()     // Catch:{ Exception -> 0x0168 }
            if (r11 == 0) goto L_0x0150
            boolean r6 = r11.exists()     // Catch:{ Exception -> 0x0168 }
            if (r6 != 0) goto L_0x005b
            boolean r11 = r11.mkdirs()     // Catch:{ Exception -> 0x0168 }
            if (r11 != 0) goto L_0x005b
            goto L_0x0150
        L_0x005b:
            org.apache.cordova.CordovaResourceApi$OpenForReadResult r11 = r4.openForRead(r2)     // Catch:{ Exception -> 0x0168 }
            org.apache.cordova.Zip$ProgressEvent r2 = new org.apache.cordova.Zip$ProgressEvent     // Catch:{ Exception -> 0x0168 }
            r2.<init>()     // Catch:{ Exception -> 0x0168 }
            long r6 = r11.length     // Catch:{ Exception -> 0x0168 }
            r2.setTotal(r6)     // Catch:{ Exception -> 0x0168 }
            java.io.BufferedInputStream r4 = new java.io.BufferedInputStream     // Catch:{ Exception -> 0x0168 }
            java.io.InputStream r11 = r11.inputStream     // Catch:{ Exception -> 0x0168 }
            r4.<init>(r11)     // Catch:{ Exception -> 0x0168 }
            r11 = 10
            r4.mark(r11)     // Catch:{ Exception -> 0x014d, all -> 0x014a }
            int r11 = readInt(r4)     // Catch:{ Exception -> 0x014d, all -> 0x014a }
            r0 = 875721283(0x34327243, float:1.661911E-7)
            if (r11 == r0) goto L_0x0082
            r4.reset()     // Catch:{ Exception -> 0x014d, all -> 0x014a }
            goto L_0x009a
        L_0x0082:
            readInt(r4)     // Catch:{ Exception -> 0x014d, all -> 0x014a }
            int r11 = readInt(r4)     // Catch:{ Exception -> 0x014d, all -> 0x014a }
            int r0 = readInt(r4)     // Catch:{ Exception -> 0x014d, all -> 0x014a }
            int r6 = r11 + r0
            long r6 = (long) r6     // Catch:{ Exception -> 0x014d, all -> 0x014a }
            r4.skip(r6)     // Catch:{ Exception -> 0x014d, all -> 0x014a }
            int r11 = r11 + 16
            int r11 = r11 + r0
            long r6 = (long) r11     // Catch:{ Exception -> 0x014d, all -> 0x014a }
            r2.setLoaded(r6)     // Catch:{ Exception -> 0x014d, all -> 0x014a }
        L_0x009a:
            java.util.zip.ZipInputStream r0 = new java.util.zip.ZipInputStream     // Catch:{ Exception -> 0x014d, all -> 0x014a }
            r0.<init>(r4)     // Catch:{ Exception -> 0x014d, all -> 0x014a }
            r11 = 32768(0x8000, float:4.5918E-41)
            byte[] r11 = new byte[r11]     // Catch:{ Exception -> 0x0168 }
            r4 = 0
        L_0x00a5:
            java.util.zip.ZipEntry r6 = r0.getNextEntry()     // Catch:{ Exception -> 0x0168 }
            if (r6 == 0) goto L_0x0131
            java.lang.String r4 = r6.getName()     // Catch:{ Exception -> 0x0168 }
            boolean r7 = r6.isDirectory()     // Catch:{ Exception -> 0x0168 }
            if (r7 == 0) goto L_0x00cd
            java.io.File r7 = new java.io.File     // Catch:{ Exception -> 0x0168 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0168 }
            r8.<init>()     // Catch:{ Exception -> 0x0168 }
            r8.append(r5)     // Catch:{ Exception -> 0x0168 }
            r8.append(r4)     // Catch:{ Exception -> 0x0168 }
            java.lang.String r4 = r8.toString()     // Catch:{ Exception -> 0x0168 }
            r7.<init>(r4)     // Catch:{ Exception -> 0x0168 }
            r7.mkdirs()     // Catch:{ Exception -> 0x0168 }
            goto L_0x0121
        L_0x00cd:
            java.io.File r7 = new java.io.File     // Catch:{ Exception -> 0x0168 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0168 }
            r8.<init>()     // Catch:{ Exception -> 0x0168 }
            r8.append(r5)     // Catch:{ Exception -> 0x0168 }
            r8.append(r4)     // Catch:{ Exception -> 0x0168 }
            java.lang.String r4 = r8.toString()     // Catch:{ Exception -> 0x0168 }
            r7.<init>(r4)     // Catch:{ Exception -> 0x0168 }
            java.io.File r4 = r7.getParentFile()     // Catch:{ Exception -> 0x0168 }
            r4.mkdirs()     // Catch:{ Exception -> 0x0168 }
            boolean r4 = r7.exists()     // Catch:{ Exception -> 0x0168 }
            if (r4 != 0) goto L_0x00f4
            boolean r4 = r7.createNewFile()     // Catch:{ Exception -> 0x0168 }
            if (r4 == 0) goto L_0x0121
        L_0x00f4:
            java.lang.String r4 = "Zip"
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0168 }
            r8.<init>()     // Catch:{ Exception -> 0x0168 }
            java.lang.String r9 = "extracting: "
            r8.append(r9)     // Catch:{ Exception -> 0x0168 }
            java.lang.String r9 = r7.getPath()     // Catch:{ Exception -> 0x0168 }
            r8.append(r9)     // Catch:{ Exception -> 0x0168 }
            java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x0168 }
            android.util.Log.w(r4, r8)     // Catch:{ Exception -> 0x0168 }
            java.io.FileOutputStream r4 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x0168 }
            r4.<init>(r7)     // Catch:{ Exception -> 0x0168 }
        L_0x0113:
            int r7 = r0.read(r11)     // Catch:{ Exception -> 0x0168 }
            r8 = -1
            if (r7 == r8) goto L_0x011e
            r4.write(r11, r1, r7)     // Catch:{ Exception -> 0x0168 }
            goto L_0x0113
        L_0x011e:
            r4.close()     // Catch:{ Exception -> 0x0168 }
        L_0x0121:
            long r6 = r6.getCompressedSize()     // Catch:{ Exception -> 0x0168 }
            r2.addLoaded(r6)     // Catch:{ Exception -> 0x0168 }
            r10.updateProgress(r12, r2)     // Catch:{ Exception -> 0x0168 }
            r0.closeEntry()     // Catch:{ Exception -> 0x0168 }
            r4 = 1
            goto L_0x00a5
        L_0x0131:
            long r5 = r2.getTotal()     // Catch:{ Exception -> 0x0168 }
            r2.setLoaded(r5)     // Catch:{ Exception -> 0x0168 }
            r10.updateProgress(r12, r2)     // Catch:{ Exception -> 0x0168 }
            if (r4 == 0) goto L_0x0141
            r12.success()     // Catch:{ Exception -> 0x0168 }
            goto L_0x0146
        L_0x0141:
            java.lang.String r11 = "Bad zip file"
            r12.error((java.lang.String) r11)     // Catch:{ Exception -> 0x0168 }
        L_0x0146:
            r0.close()     // Catch:{ IOException -> 0x0176 }
            goto L_0x0176
        L_0x014a:
            r11 = move-exception
            r0 = r4
            goto L_0x0177
        L_0x014d:
            r11 = move-exception
            r0 = r4
            goto L_0x0169
        L_0x0150:
            java.lang.String r11 = "Could not create output directory"
            r12.error((java.lang.String) r11)     // Catch:{ Exception -> 0x0168 }
            java.lang.String r1 = "Zip"
            android.util.Log.e(r1, r11)     // Catch:{ Exception -> 0x0168 }
            return
        L_0x015b:
            java.lang.String r11 = "Zip file does not exist"
            r12.error((java.lang.String) r11)     // Catch:{ Exception -> 0x0168 }
            java.lang.String r1 = "Zip"
            android.util.Log.e(r1, r11)     // Catch:{ Exception -> 0x0168 }
            return
        L_0x0166:
            r11 = move-exception
            goto L_0x0177
        L_0x0168:
            r11 = move-exception
        L_0x0169:
            java.lang.String r1 = "An error occurred while unzipping."
            r12.error((java.lang.String) r1)     // Catch:{ all -> 0x0166 }
            java.lang.String r12 = "Zip"
            android.util.Log.e(r12, r1, r11)     // Catch:{ all -> 0x0166 }
            if (r0 == 0) goto L_0x0176
            goto L_0x0146
        L_0x0176:
            return
        L_0x0177:
            if (r0 == 0) goto L_0x017c
            r0.close()     // Catch:{ IOException -> 0x017c }
        L_0x017c:
            throw r11
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.cordova.Zip.unzipSync(org.apache.cordova.CordovaArgs, org.apache.cordova.CallbackContext):void");
    }

    private void updateProgress(CallbackContext callbackContext, ProgressEvent progressEvent) throws JSONException {
        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, progressEvent.toJSONObject());
        pluginResult.setKeepCallback(true);
        callbackContext.sendPluginResult(pluginResult);
    }

    private Uri getUriForArg(String str) {
        CordovaResourceApi resourceApi = this.webView.getResourceApi();
        Uri parse = Uri.parse(str);
        if (parse.getScheme() == null) {
            parse = Uri.fromFile(new File(str));
        }
        return resourceApi.remapUri(parse);
    }

    private static class ProgressEvent {
        private long loaded;
        private long total;

        private ProgressEvent() {
        }

        public long getLoaded() {
            return this.loaded;
        }

        public void setLoaded(long j) {
            this.loaded = j;
        }

        public void addLoaded(long j) {
            this.loaded += j;
        }

        public long getTotal() {
            return this.total;
        }

        public void setTotal(long j) {
            this.total = j;
        }

        public JSONObject toJSONObject() throws JSONException {
            return new JSONObject("{loaded:" + this.loaded + ",total:" + this.total + "}");
        }
    }
}
