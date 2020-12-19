package org.apache.cordova.filetransfer;

import android.net.Uri;
import android.webkit.CookieManager;
import com.adobe.phonegap.push.PushConstants;
import com.silkimen.http.HttpRequest;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaResourceApi;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginManager;
import org.apache.cordova.PluginResult;
import org.apache.cordova.Whitelist;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FileTransfer extends CordovaPlugin {
    public static int ABORTED_ERR = 4;
    private static final String BOUNDARY = "+++++";
    public static int CONNECTION_ERR = 3;
    public static int FILE_NOT_FOUND_ERR = 1;
    public static int INVALID_URL_ERR = 2;
    private static final String LINE_END = "\r\n";
    private static final String LINE_START = "--";
    private static final String LOG_TAG = "FileTransfer";
    private static final int MAX_BUFFER_SIZE = 16384;
    public static int NOT_MODIFIED_ERR = 5;
    /* access modifiers changed from: private */
    public static HashMap<String, RequestContext> activeRequests = new HashMap<>();

    private static final class RequestContext {
        boolean aborted;
        CallbackContext callbackContext;
        HttpURLConnection connection;
        String source;
        String target;
        File targetFile;

        RequestContext(String str, String str2, CallbackContext callbackContext2) {
            this.source = str;
            this.target = str2;
            this.callbackContext = callbackContext2;
        }

        /* access modifiers changed from: package-private */
        public void sendPluginResult(PluginResult pluginResult) {
            synchronized (this) {
                if (!this.aborted) {
                    this.callbackContext.sendPluginResult(pluginResult);
                }
            }
        }
    }

    private static abstract class TrackingInputStream extends FilterInputStream {
        public abstract long getTotalRawBytesRead();

        public TrackingInputStream(InputStream inputStream) {
            super(inputStream);
        }
    }

    private static class ExposedGZIPInputStream extends GZIPInputStream {
        public ExposedGZIPInputStream(InputStream inputStream) throws IOException {
            super(inputStream);
        }

        public Inflater getInflater() {
            return this.inf;
        }
    }

    private static class TrackingGZIPInputStream extends TrackingInputStream {
        private ExposedGZIPInputStream gzin;

        public TrackingGZIPInputStream(ExposedGZIPInputStream exposedGZIPInputStream) throws IOException {
            super(exposedGZIPInputStream);
            this.gzin = exposedGZIPInputStream;
        }

        public long getTotalRawBytesRead() {
            return this.gzin.getInflater().getBytesRead();
        }
    }

    private static class SimpleTrackingInputStream extends TrackingInputStream {
        private long bytesRead = 0;

        public SimpleTrackingInputStream(InputStream inputStream) {
            super(inputStream);
        }

        private int updateBytesRead(int i) {
            if (i != -1) {
                this.bytesRead += (long) i;
            }
            return i;
        }

        public int read() throws IOException {
            return updateBytesRead(super.read());
        }

        public int read(byte[] bArr, int i, int i2) throws IOException {
            return updateBytesRead(super.read(bArr, i, i2));
        }

        public long getTotalRawBytesRead() {
            return this.bytesRead;
        }
    }

    public boolean execute(String str, JSONArray jSONArray, CallbackContext callbackContext) throws JSONException {
        if (str.equals("upload") || str.equals("download")) {
            String string = jSONArray.getString(0);
            String string2 = jSONArray.getString(1);
            if (str.equals("upload")) {
                upload(string, string2, jSONArray, callbackContext);
            } else {
                download(string, string2, jSONArray, callbackContext);
            }
            return true;
        } else if (!str.equals("abort")) {
            return false;
        } else {
            abort(jSONArray.getString(0));
            callbackContext.success();
            return true;
        }
    }

    /* access modifiers changed from: private */
    public static void addHeadersToRequest(URLConnection uRLConnection, JSONObject jSONObject) {
        try {
            Iterator<String> keys = jSONObject.keys();
            while (keys.hasNext()) {
                String obj = keys.next().toString();
                String replaceAll = obj.replaceAll("\\n", "").replaceAll("\\s+", "").replaceAll(":", "").replaceAll("[^\\x20-\\x7E]+", "");
                JSONArray optJSONArray = jSONObject.optJSONArray(obj);
                if (optJSONArray == null) {
                    optJSONArray = new JSONArray();
                    optJSONArray.put(jSONObject.getString(obj).replaceAll("\\s+", " ").replaceAll("\\n", " ").replaceAll("[^\\x20-\\x7E]+", " "));
                }
                uRLConnection.setRequestProperty(replaceAll, optJSONArray.getString(0));
                for (int i = 1; i < optJSONArray.length(); i++) {
                    uRLConnection.addRequestProperty(obj, optJSONArray.getString(i));
                }
            }
        } catch (JSONException unused) {
        }
    }

    /* access modifiers changed from: private */
    public String getCookies(String str) {
        String str2;
        boolean z = true;
        try {
            Method method = this.webView.getClass().getMethod("getCookieManager", new Class[0]);
            Class<?> returnType = method.getReturnType();
            str2 = (String) returnType.getMethod("getCookie", new Class[]{String.class}).invoke(returnType.cast(method.invoke(this.webView, new Object[0])), new Object[]{str});
        } catch (ClassCastException | IllegalAccessException | NoSuchMethodException | InvocationTargetException unused) {
            str2 = null;
            z = false;
        }
        return (z || CookieManager.getInstance() == null) ? str2 : CookieManager.getInstance().getCookie(str);
    }

    private void upload(String str, String str2, JSONArray jSONArray, CallbackContext callbackContext) throws JSONException {
        boolean z;
        int i;
        String str3 = str;
        String str4 = str2;
        JSONArray jSONArray2 = jSONArray;
        CallbackContext callbackContext2 = callbackContext;
        LOG.m37d(LOG_TAG, "upload " + str3 + " to " + str4);
        final String argument = getArgument(jSONArray2, 2, "file");
        final String argument2 = getArgument(jSONArray2, 3, "image.jpg");
        final String argument3 = getArgument(jSONArray2, 4, "image/jpeg");
        final JSONObject jSONObject = jSONArray2.optJSONObject(5) == null ? new JSONObject() : jSONArray2.optJSONObject(5);
        final boolean z2 = jSONArray2.optBoolean(7) || jSONArray2.isNull(7);
        final JSONObject optJSONObject = jSONArray2.optJSONObject(8) == null ? jSONObject.optJSONObject("headers") : jSONArray2.optJSONObject(8);
        String string = jSONArray2.getString(9);
        String argument4 = getArgument(jSONArray2, 10, HttpRequest.METHOD_POST);
        CordovaResourceApi resourceApi = this.webView.getResourceApi();
        LOG.m37d(LOG_TAG, "fileKey: " + argument);
        LOG.m37d(LOG_TAG, "fileName: " + argument2);
        LOG.m37d(LOG_TAG, "mimeType: " + argument3);
        LOG.m37d(LOG_TAG, "params: " + jSONObject);
        LOG.m37d(LOG_TAG, "chunkedMode: " + z2);
        LOG.m37d(LOG_TAG, "headers: " + optJSONObject);
        LOG.m37d(LOG_TAG, "objectId: " + string);
        LOG.m37d(LOG_TAG, "httpMethod: " + argument4);
        final Uri remapUri = resourceApi.remapUri(Uri.parse(str2));
        int uriType = CordovaResourceApi.getUriType(remapUri);
        if (uriType == 6) {
            i = 5;
            z = true;
        } else {
            i = 5;
            z = false;
        }
        if (uriType == i || z) {
            RequestContext requestContext = new RequestContext(str3, str4, callbackContext2);
            synchronized (activeRequests) {
                activeRequests.put(string, requestContext);
            }
            C05721 r23 = r1;
            CordovaResourceApi cordovaResourceApi = resourceApi;
            final RequestContext requestContext2 = requestContext;
            String str5 = string;
            final String str6 = str;
            String str7 = argument4;
            final CordovaResourceApi cordovaResourceApi2 = cordovaResourceApi;
            ExecutorService threadPool = this.f59cordova.getThreadPool();
            final String str8 = str7;
            final String str9 = str2;
            final boolean z3 = z;
            final String str10 = str5;
            C05721 r1 = new Runnable() {
                /*  JADX ERROR: IndexOutOfBoundsException in pass: RegionMakerVisitor
                    java.lang.IndexOutOfBoundsException: Index 0 out of bounds for length 0
                    	at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:64)
                    	at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:70)
                    	at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:248)
                    	at java.base/java.util.Objects.checkIndex(Objects.java:372)
                    	at java.base/java.util.ArrayList.get(ArrayList.java:458)
                    	at jadx.core.dex.nodes.InsnNode.getArg(InsnNode.java:101)
                    	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:611)
                    	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
                    	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
                    	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
                    	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
                    	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
                    	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
                    	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
                    	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
                    	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:698)
                    	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
                    	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
                    	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:693)
                    	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
                    	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
                    	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:698)
                    	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
                    	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
                    	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:693)
                    	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
                    	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
                    	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:698)
                    	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
                    	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
                    	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:49)
                    */
                /* JADX WARNING: Removed duplicated region for block: B:79:0x0196 A[Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0160, JSONException -> 0x0448, Throwable -> 0x0400 }] */
                /* JADX WARNING: Removed duplicated region for block: B:80:0x01a1 A[SYNTHETIC, Splitter:B:80:0x01a1] */
                /* JADX WARNING: Unknown top exception splitter block from list: {B:339:0x04d3=Splitter:B:339:0x04d3, B:297:0x0414=Splitter:B:297:0x0414, B:325:0x0479=Splitter:B:325:0x0479} */
                public void run() {
                    /*
                        r21 = this;
                        r1 = r21
                        org.apache.cordova.filetransfer.FileTransfer$RequestContext r2 = r3
                        boolean r2 = r2.aborted
                        if (r2 == 0) goto L_0x0009
                        return
                    L_0x0009:
                        java.lang.String r2 = r4
                        android.net.Uri r2 = android.net.Uri.parse(r2)
                        org.apache.cordova.CordovaResourceApi r3 = r5
                        java.lang.String r4 = r2.getScheme()
                        if (r4 == 0) goto L_0x0018
                        goto L_0x0023
                    L_0x0018:
                        java.io.File r2 = new java.io.File
                        java.lang.String r4 = r4
                        r2.<init>(r4)
                        android.net.Uri r2 = android.net.Uri.fromFile(r2)
                    L_0x0023:
                        android.net.Uri r2 = r3.remapUri(r2)
                        r3 = -1
                        r5 = 0
                        org.apache.cordova.filetransfer.FileUploadResult r6 = new org.apache.cordova.filetransfer.FileUploadResult     // Catch:{ FileNotFoundException -> 0x04cf, IOException -> 0x0474, JSONException -> 0x0448, Throwable -> 0x0410 }
                        r6.<init>()     // Catch:{ FileNotFoundException -> 0x04cf, IOException -> 0x0474, JSONException -> 0x0448, Throwable -> 0x0410 }
                        org.apache.cordova.filetransfer.FileProgressResult r7 = new org.apache.cordova.filetransfer.FileProgressResult     // Catch:{ FileNotFoundException -> 0x04cf, IOException -> 0x0474, JSONException -> 0x0448, Throwable -> 0x0410 }
                        r7.<init>()     // Catch:{ FileNotFoundException -> 0x04cf, IOException -> 0x0474, JSONException -> 0x0448, Throwable -> 0x0410 }
                        org.apache.cordova.CordovaResourceApi r8 = r5     // Catch:{ FileNotFoundException -> 0x04cf, IOException -> 0x0474, JSONException -> 0x0448, Throwable -> 0x0410 }
                        android.net.Uri r9 = r6     // Catch:{ FileNotFoundException -> 0x04cf, IOException -> 0x0474, JSONException -> 0x0448, Throwable -> 0x0410 }
                        java.net.HttpURLConnection r8 = r8.createHttpConnection(r9)     // Catch:{ FileNotFoundException -> 0x04cf, IOException -> 0x0474, JSONException -> 0x0448, Throwable -> 0x0410 }
                        r9 = 1
                        r8.setDoInput(r9)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0403, JSONException -> 0x0448, Throwable -> 0x0400 }
                        r8.setDoOutput(r9)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0403, JSONException -> 0x0448, Throwable -> 0x0400 }
                        r8.setUseCaches(r5)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0403, JSONException -> 0x0448, Throwable -> 0x0400 }
                        java.lang.String r10 = r7     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0403, JSONException -> 0x0448, Throwable -> 0x0400 }
                        r8.setRequestMethod(r10)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0403, JSONException -> 0x0448, Throwable -> 0x0400 }
                        org.json.JSONObject r10 = r8     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0403, JSONException -> 0x0448, Throwable -> 0x0400 }
                        if (r10 == 0) goto L_0x0060
                        org.json.JSONObject r10 = r8     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x005b, JSONException -> 0x0448, Throwable -> 0x0400 }
                        java.lang.String r11 = "Content-Type"
                        boolean r10 = r10.has(r11)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x005b, JSONException -> 0x0448, Throwable -> 0x0400 }
                        if (r10 != 0) goto L_0x0059
                        goto L_0x0060
                    L_0x0059:
                        r10 = 0
                        goto L_0x0061
                    L_0x005b:
                        r0 = move-exception
                        r2 = r0
                    L_0x005d:
                        r10 = 0
                        goto L_0x0479
                    L_0x0060:
                        r10 = 1
                    L_0x0061:
                        if (r10 == 0) goto L_0x006a
                        java.lang.String r11 = "Content-Type"
                        java.lang.String r12 = "multipart/form-data; boundary=+++++"
                        r8.setRequestProperty(r11, r12)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x005b, JSONException -> 0x0448, Throwable -> 0x0400 }
                    L_0x006a:
                        org.apache.cordova.filetransfer.FileTransfer r11 = org.apache.cordova.filetransfer.FileTransfer.this     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0403, JSONException -> 0x0448, Throwable -> 0x0400 }
                        java.lang.String r12 = r9     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0403, JSONException -> 0x0448, Throwable -> 0x0400 }
                        java.lang.String r11 = r11.getCookies(r12)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0403, JSONException -> 0x0448, Throwable -> 0x0400 }
                        if (r11 == 0) goto L_0x0079
                        java.lang.String r12 = "Cookie"
                        r8.setRequestProperty(r12, r11)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x005b, JSONException -> 0x0448, Throwable -> 0x0400 }
                    L_0x0079:
                        org.json.JSONObject r11 = r8     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0403, JSONException -> 0x0448, Throwable -> 0x0400 }
                        if (r11 == 0) goto L_0x0082
                        org.json.JSONObject r11 = r8     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x005b, JSONException -> 0x0448, Throwable -> 0x0400 }
                        org.apache.cordova.filetransfer.FileTransfer.addHeadersToRequest(r8, r11)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x005b, JSONException -> 0x0448, Throwable -> 0x0400 }
                    L_0x0082:
                        java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0403, JSONException -> 0x0448, Throwable -> 0x0400 }
                        r11.<init>()     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0403, JSONException -> 0x0448, Throwable -> 0x0400 }
                        r12 = 34
                        org.json.JSONObject r13 = r10     // Catch:{ JSONException -> 0x00e0, FileNotFoundException -> 0x0408, IOException -> 0x005b, Throwable -> 0x0400 }
                        java.util.Iterator r13 = r13.keys()     // Catch:{ JSONException -> 0x00e0, FileNotFoundException -> 0x0408, IOException -> 0x005b, Throwable -> 0x0400 }
                    L_0x008f:
                        boolean r14 = r13.hasNext()     // Catch:{ JSONException -> 0x00e0, FileNotFoundException -> 0x0408, IOException -> 0x005b, Throwable -> 0x0400 }
                        if (r14 == 0) goto L_0x00eb
                        java.lang.Object r14 = r13.next()     // Catch:{ JSONException -> 0x00e0, FileNotFoundException -> 0x0408, IOException -> 0x005b, Throwable -> 0x0400 }
                        java.lang.String r15 = java.lang.String.valueOf(r14)     // Catch:{ JSONException -> 0x00e0, FileNotFoundException -> 0x0408, IOException -> 0x005b, Throwable -> 0x0400 }
                        java.lang.String r4 = "headers"
                        boolean r4 = r15.equals(r4)     // Catch:{ JSONException -> 0x00e0, FileNotFoundException -> 0x0408, IOException -> 0x005b, Throwable -> 0x0400 }
                        if (r4 != 0) goto L_0x008f
                        java.lang.String r4 = "--"
                        r11.append(r4)     // Catch:{ JSONException -> 0x00e0, FileNotFoundException -> 0x0408, IOException -> 0x005b, Throwable -> 0x0400 }
                        java.lang.String r4 = "+++++"
                        r11.append(r4)     // Catch:{ JSONException -> 0x00e0, FileNotFoundException -> 0x0408, IOException -> 0x005b, Throwable -> 0x0400 }
                        java.lang.String r4 = "\r\n"
                        r11.append(r4)     // Catch:{ JSONException -> 0x00e0, FileNotFoundException -> 0x0408, IOException -> 0x005b, Throwable -> 0x0400 }
                        java.lang.String r4 = "Content-Disposition: form-data; name=\""
                        r11.append(r4)     // Catch:{ JSONException -> 0x00e0, FileNotFoundException -> 0x0408, IOException -> 0x005b, Throwable -> 0x0400 }
                        java.lang.String r4 = r14.toString()     // Catch:{ JSONException -> 0x00e0, FileNotFoundException -> 0x0408, IOException -> 0x005b, Throwable -> 0x0400 }
                        r11.append(r4)     // Catch:{ JSONException -> 0x00e0, FileNotFoundException -> 0x0408, IOException -> 0x005b, Throwable -> 0x0400 }
                        r11.append(r12)     // Catch:{ JSONException -> 0x00e0, FileNotFoundException -> 0x0408, IOException -> 0x005b, Throwable -> 0x0400 }
                        java.lang.String r4 = "\r\n"
                        r11.append(r4)     // Catch:{ JSONException -> 0x00e0, FileNotFoundException -> 0x0408, IOException -> 0x005b, Throwable -> 0x0400 }
                        java.lang.String r4 = "\r\n"
                        r11.append(r4)     // Catch:{ JSONException -> 0x00e0, FileNotFoundException -> 0x0408, IOException -> 0x005b, Throwable -> 0x0400 }
                        org.json.JSONObject r4 = r10     // Catch:{ JSONException -> 0x00e0, FileNotFoundException -> 0x0408, IOException -> 0x005b, Throwable -> 0x0400 }
                        java.lang.String r14 = r14.toString()     // Catch:{ JSONException -> 0x00e0, FileNotFoundException -> 0x0408, IOException -> 0x005b, Throwable -> 0x0400 }
                        java.lang.String r4 = r4.getString(r14)     // Catch:{ JSONException -> 0x00e0, FileNotFoundException -> 0x0408, IOException -> 0x005b, Throwable -> 0x0400 }
                        r11.append(r4)     // Catch:{ JSONException -> 0x00e0, FileNotFoundException -> 0x0408, IOException -> 0x005b, Throwable -> 0x0400 }
                        java.lang.String r4 = "\r\n"
                        r11.append(r4)     // Catch:{ JSONException -> 0x00e0, FileNotFoundException -> 0x0408, IOException -> 0x005b, Throwable -> 0x0400 }
                        goto L_0x008f
                    L_0x00e0:
                        r0 = move-exception
                        r4 = r0
                        java.lang.String r13 = "FileTransfer"
                        java.lang.String r14 = r4.getMessage()     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0403, JSONException -> 0x0448, Throwable -> 0x0400 }
                        org.apache.cordova.LOG.m41e((java.lang.String) r13, (java.lang.String) r14, (java.lang.Throwable) r4)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0403, JSONException -> 0x0448, Throwable -> 0x0400 }
                    L_0x00eb:
                        java.lang.String r4 = "--"
                        r11.append(r4)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0403, JSONException -> 0x0448, Throwable -> 0x0400 }
                        java.lang.String r4 = "+++++"
                        r11.append(r4)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0403, JSONException -> 0x0448, Throwable -> 0x0400 }
                        java.lang.String r4 = "\r\n"
                        r11.append(r4)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0403, JSONException -> 0x0448, Throwable -> 0x0400 }
                        java.lang.String r4 = "Content-Disposition: form-data; name=\""
                        r11.append(r4)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0403, JSONException -> 0x0448, Throwable -> 0x0400 }
                        java.lang.String r4 = r11     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0403, JSONException -> 0x0448, Throwable -> 0x0400 }
                        r11.append(r4)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0403, JSONException -> 0x0448, Throwable -> 0x0400 }
                        java.lang.String r4 = "\";"
                        r11.append(r4)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0403, JSONException -> 0x0448, Throwable -> 0x0400 }
                        java.lang.String r4 = " filename=\""
                        r11.append(r4)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0403, JSONException -> 0x0448, Throwable -> 0x0400 }
                        java.lang.String r4 = r12     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0403, JSONException -> 0x0448, Throwable -> 0x0400 }
                        r11.append(r4)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0403, JSONException -> 0x0448, Throwable -> 0x0400 }
                        r11.append(r12)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0403, JSONException -> 0x0448, Throwable -> 0x0400 }
                        java.lang.String r4 = "\r\n"
                        r11.append(r4)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0403, JSONException -> 0x0448, Throwable -> 0x0400 }
                        java.lang.String r4 = "Content-Type: "
                        r11.append(r4)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0403, JSONException -> 0x0448, Throwable -> 0x0400 }
                        java.lang.String r4 = r13     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0403, JSONException -> 0x0448, Throwable -> 0x0400 }
                        r11.append(r4)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0403, JSONException -> 0x0448, Throwable -> 0x0400 }
                        java.lang.String r4 = "\r\n"
                        r11.append(r4)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0403, JSONException -> 0x0448, Throwable -> 0x0400 }
                        java.lang.String r4 = "\r\n"
                        r11.append(r4)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0403, JSONException -> 0x0448, Throwable -> 0x0400 }
                        java.lang.String r4 = r11.toString()     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0403, JSONException -> 0x0448, Throwable -> 0x0400 }
                        java.lang.String r11 = "UTF-8"
                        byte[] r4 = r4.getBytes(r11)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0403, JSONException -> 0x0448, Throwable -> 0x0400 }
                        java.lang.String r11 = "\r\n--+++++--\r\n"
                        java.lang.String r12 = "UTF-8"
                        byte[] r11 = r11.getBytes(r12)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0403, JSONException -> 0x0448, Throwable -> 0x0400 }
                        org.apache.cordova.CordovaResourceApi r12 = r5     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0403, JSONException -> 0x0448, Throwable -> 0x0400 }
                        org.apache.cordova.CordovaResourceApi$OpenForReadResult r2 = r12.openForRead(r2)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0403, JSONException -> 0x0448, Throwable -> 0x0400 }
                        int r12 = r4.length     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0403, JSONException -> 0x0448, Throwable -> 0x0400 }
                        int r13 = r11.length     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0403, JSONException -> 0x0448, Throwable -> 0x0400 }
                        int r12 = r12 + r13
                        long r13 = r2.length     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0403, JSONException -> 0x0448, Throwable -> 0x0400 }
                        r16 = 0
                        int r15 = (r13 > r16 ? 1 : (r13 == r16 ? 0 : -1))
                        if (r15 < 0) goto L_0x0165
                        long r13 = r2.length     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x005b, JSONException -> 0x0448, Throwable -> 0x0400 }
                        int r13 = (int) r13
                        if (r10 == 0) goto L_0x0158
                        int r13 = r13 + r12
                    L_0x0158:
                        r7.setLengthComputable(r9)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0160, JSONException -> 0x0448, Throwable -> 0x0400 }
                        long r14 = (long) r13     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0160, JSONException -> 0x0448, Throwable -> 0x0400 }
                        r7.setTotal(r14)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0160, JSONException -> 0x0448, Throwable -> 0x0400 }
                        goto L_0x0166
                    L_0x0160:
                        r0 = move-exception
                        r2 = r0
                        r3 = r13
                        goto L_0x005d
                    L_0x0165:
                        r13 = -1
                    L_0x0166:
                        java.lang.String r12 = "FileTransfer"
                        java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03f9, JSONException -> 0x0448, Throwable -> 0x0400 }
                        r14.<init>()     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03f9, JSONException -> 0x0448, Throwable -> 0x0400 }
                        java.lang.String r15 = "Content Length: "
                        r14.append(r15)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03f9, JSONException -> 0x0448, Throwable -> 0x0400 }
                        r14.append(r13)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03f9, JSONException -> 0x0448, Throwable -> 0x0400 }
                        java.lang.String r14 = r14.toString()     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03f9, JSONException -> 0x0448, Throwable -> 0x0400 }
                        org.apache.cordova.LOG.m37d(r12, r14)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03f9, JSONException -> 0x0448, Throwable -> 0x0400 }
                        boolean r12 = r14     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03f9, JSONException -> 0x0448, Throwable -> 0x0400 }
                        if (r12 != 0) goto L_0x0189
                        int r12 = android.os.Build.VERSION.SDK_INT     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0160, JSONException -> 0x0448, Throwable -> 0x0400 }
                        r14 = 8
                        if (r12 >= r14) goto L_0x0187
                        goto L_0x0189
                    L_0x0187:
                        r12 = 0
                        goto L_0x018a
                    L_0x0189:
                        r12 = 1
                    L_0x018a:
                        if (r12 != 0) goto L_0x0191
                        if (r13 != r3) goto L_0x018f
                        goto L_0x0191
                    L_0x018f:
                        r3 = 0
                        goto L_0x0192
                    L_0x0191:
                        r3 = 1
                    L_0x0192:
                        r12 = 16384(0x4000, float:2.2959E-41)
                        if (r3 == 0) goto L_0x01a1
                        r8.setChunkedStreamingMode(r12)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0160, JSONException -> 0x0448, Throwable -> 0x0400 }
                        java.lang.String r3 = "Transfer-Encoding"
                        java.lang.String r14 = "chunked"
                        r8.setRequestProperty(r3, r14)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0160, JSONException -> 0x0448, Throwable -> 0x0400 }
                        goto L_0x01af
                    L_0x01a1:
                        r8.setFixedLengthStreamingMode(r13)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03f9, JSONException -> 0x0448, Throwable -> 0x0400 }
                        boolean r3 = r15     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03f9, JSONException -> 0x0448, Throwable -> 0x0400 }
                        if (r3 == 0) goto L_0x01af
                        java.lang.String r3 = "FileTransfer"
                        java.lang.String r14 = "setFixedLengthStreamingMode could cause OutOfMemoryException - switch to chunkedMode=true to avoid it if this is an issue."
                        org.apache.cordova.LOG.m49w((java.lang.String) r3, (java.lang.String) r14)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0160, JSONException -> 0x0448, Throwable -> 0x0400 }
                    L_0x01af:
                        r8.connect()     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03f9, JSONException -> 0x0448, Throwable -> 0x0400 }
                        java.io.OutputStream r3 = r8.getOutputStream()     // Catch:{ all -> 0x03e2 }
                        org.apache.cordova.filetransfer.FileTransfer$RequestContext r14 = r3     // Catch:{ all -> 0x03da }
                        monitor-enter(r14)     // Catch:{ all -> 0x03da }
                        org.apache.cordova.filetransfer.FileTransfer$RequestContext r15 = r3     // Catch:{ all -> 0x03d0 }
                        boolean r15 = r15.aborted     // Catch:{ all -> 0x03d0 }
                        if (r15 == 0) goto L_0x01e2
                        monitor-exit(r14)     // Catch:{ all -> 0x01dc }
                        java.io.InputStream r2 = r2.inputStream     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0160, JSONException -> 0x0448, Throwable -> 0x0400 }
                        org.apache.cordova.filetransfer.FileTransfer.safeClose(r2)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0160, JSONException -> 0x0448, Throwable -> 0x0400 }
                        org.apache.cordova.filetransfer.FileTransfer.safeClose(r3)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x0160, JSONException -> 0x0448, Throwable -> 0x0400 }
                        java.util.HashMap r2 = org.apache.cordova.filetransfer.FileTransfer.activeRequests
                        monitor-enter(r2)
                        java.util.HashMap r3 = org.apache.cordova.filetransfer.FileTransfer.activeRequests     // Catch:{ all -> 0x01d8 }
                        java.lang.String r4 = r16     // Catch:{ all -> 0x01d8 }
                        r3.remove(r4)     // Catch:{ all -> 0x01d8 }
                        monitor-exit(r2)     // Catch:{ all -> 0x01d8 }
                        return
                    L_0x01d8:
                        r0 = move-exception
                        r3 = r0
                        monitor-exit(r2)     // Catch:{ all -> 0x01d8 }
                        throw r3
                    L_0x01dc:
                        r0 = move-exception
                        r4 = r0
                        r5 = r13
                        r10 = 0
                        goto L_0x03d4
                    L_0x01e2:
                        org.apache.cordova.filetransfer.FileTransfer$RequestContext r15 = r3     // Catch:{ all -> 0x03d0 }
                        r15.connection = r8     // Catch:{ all -> 0x03d0 }
                        monitor-exit(r14)     // Catch:{ all -> 0x03d0 }
                        if (r10 == 0) goto L_0x01f4
                        r3.write(r4)     // Catch:{ all -> 0x01ef }
                        int r4 = r4.length     // Catch:{ all -> 0x01ef }
                        int r4 = r4 + r5
                        goto L_0x01f5
                    L_0x01ef:
                        r0 = move-exception
                        r9 = r3
                        r5 = r13
                        goto L_0x03de
                    L_0x01f4:
                        r4 = 0
                    L_0x01f5:
                        java.io.InputStream r14 = r2.inputStream     // Catch:{ all -> 0x03ca }
                        int r14 = r14.available()     // Catch:{ all -> 0x03ca }
                        int r14 = java.lang.Math.min(r14, r12)     // Catch:{ all -> 0x03ca }
                        byte[] r15 = new byte[r14]     // Catch:{ all -> 0x03ca }
                        java.io.InputStream r9 = r2.inputStream     // Catch:{ all -> 0x03ca }
                        int r9 = r9.read(r15, r5, r14)     // Catch:{ all -> 0x03ca }
                    L_0x0207:
                        if (r9 <= 0) goto L_0x0288
                        int r4 = r4 + r9
                        r18 = r13
                        long r12 = (long) r4
                        r6.setBytesSent(r12)     // Catch:{ all -> 0x0280 }
                        r3.write(r15, r5, r9)     // Catch:{ all -> 0x0280 }
                        r19 = 102400(0x19000, double:5.05923E-319)
                        long r19 = r16 + r19
                        int r9 = (r12 > r19 ? 1 : (r12 == r19 ? 0 : -1))
                        if (r9 <= 0) goto L_0x0248
                        java.lang.String r9 = "FileTransfer"
                        java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ all -> 0x0280 }
                        r14.<init>()     // Catch:{ all -> 0x0280 }
                        java.lang.String r5 = "Uploaded "
                        r14.append(r5)     // Catch:{ all -> 0x0280 }
                        r14.append(r4)     // Catch:{ all -> 0x0280 }
                        java.lang.String r5 = " of "
                        r14.append(r5)     // Catch:{ all -> 0x0280 }
                        r5 = r18
                        r14.append(r5)     // Catch:{ all -> 0x0246 }
                        r18 = r4
                        java.lang.String r4 = " bytes"
                        r14.append(r4)     // Catch:{ all -> 0x027e }
                        java.lang.String r4 = r14.toString()     // Catch:{ all -> 0x027e }
                        org.apache.cordova.LOG.m37d(r9, r4)     // Catch:{ all -> 0x027e }
                        r16 = r12
                        goto L_0x024c
                    L_0x0246:
                        r0 = move-exception
                        goto L_0x0283
                    L_0x0248:
                        r5 = r18
                        r18 = r4
                    L_0x024c:
                        java.io.InputStream r4 = r2.inputStream     // Catch:{ all -> 0x027e }
                        int r4 = r4.available()     // Catch:{ all -> 0x027e }
                        r9 = 16384(0x4000, float:2.2959E-41)
                        int r4 = java.lang.Math.min(r4, r9)     // Catch:{ all -> 0x027e }
                        java.io.InputStream r14 = r2.inputStream     // Catch:{ all -> 0x027e }
                        r9 = 0
                        int r4 = r14.read(r15, r9, r4)     // Catch:{ all -> 0x027e }
                        r7.setLoaded(r12)     // Catch:{ all -> 0x027e }
                        org.apache.cordova.PluginResult r9 = new org.apache.cordova.PluginResult     // Catch:{ all -> 0x027e }
                        org.apache.cordova.PluginResult$Status r12 = org.apache.cordova.PluginResult.Status.OK     // Catch:{ all -> 0x027e }
                        org.json.JSONObject r13 = r7.toJSONObject()     // Catch:{ all -> 0x027e }
                        r9.<init>((org.apache.cordova.PluginResult.Status) r12, (org.json.JSONObject) r13)     // Catch:{ all -> 0x027e }
                        r12 = 1
                        r9.setKeepCallback(r12)     // Catch:{ all -> 0x027e }
                        org.apache.cordova.filetransfer.FileTransfer$RequestContext r13 = r3     // Catch:{ all -> 0x027e }
                        r13.sendPluginResult(r9)     // Catch:{ all -> 0x027e }
                        r9 = r4
                        r13 = r5
                        r4 = r18
                        r5 = 0
                        r12 = 16384(0x4000, float:2.2959E-41)
                        goto L_0x0207
                    L_0x027e:
                        r0 = move-exception
                        goto L_0x0285
                    L_0x0280:
                        r0 = move-exception
                        r5 = r18
                    L_0x0283:
                        r18 = r4
                    L_0x0285:
                        r9 = r3
                        goto L_0x03e0
                    L_0x0288:
                        r5 = r13
                        if (r10 == 0) goto L_0x0290
                        r3.write(r11)     // Catch:{ all -> 0x03c8 }
                        int r7 = r11.length     // Catch:{ all -> 0x03c8 }
                        int r4 = r4 + r7
                    L_0x0290:
                        r3.flush()     // Catch:{ all -> 0x03c8 }
                        java.io.InputStream r2 = r2.inputStream     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        org.apache.cordova.filetransfer.FileTransfer.safeClose(r2)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        org.apache.cordova.filetransfer.FileTransfer.safeClose(r3)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        org.apache.cordova.filetransfer.FileTransfer$RequestContext r2 = r3     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        monitor-enter(r2)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        org.apache.cordova.filetransfer.FileTransfer$RequestContext r3 = r3     // Catch:{ all -> 0x03c0 }
                        r7 = 0
                        r3.connection = r7     // Catch:{ all -> 0x03c0 }
                        monitor-exit(r2)     // Catch:{ all -> 0x03c0 }
                        java.lang.String r2 = "FileTransfer"
                        java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        r3.<init>()     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        java.lang.String r7 = "Sent "
                        r3.append(r7)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        r3.append(r4)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        java.lang.String r7 = " of "
                        r3.append(r7)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        r3.append(r5)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        java.lang.String r3 = r3.toString()     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        org.apache.cordova.LOG.m37d(r2, r3)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        int r2 = r8.getResponseCode()     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        java.lang.String r3 = "FileTransfer"
                        java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        r7.<init>()     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        java.lang.String r9 = "response code: "
                        r7.append(r9)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        r7.append(r2)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        java.lang.String r7 = r7.toString()     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        org.apache.cordova.LOG.m37d(r3, r7)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        java.lang.String r3 = "FileTransfer"
                        java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        r7.<init>()     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        java.lang.String r9 = "response headers: "
                        r7.append(r9)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        java.util.Map r9 = r8.getHeaderFields()     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        r7.append(r9)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        java.lang.String r7 = r7.toString()     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        org.apache.cordova.LOG.m37d(r3, r7)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        org.apache.cordova.filetransfer.FileTransfer$TrackingInputStream r3 = org.apache.cordova.filetransfer.FileTransfer.getInputStream(r8)     // Catch:{ all -> 0x03ac }
                        org.apache.cordova.filetransfer.FileTransfer$RequestContext r7 = r3     // Catch:{ all -> 0x03a9 }
                        monitor-enter(r7)     // Catch:{ all -> 0x03a9 }
                        org.apache.cordova.filetransfer.FileTransfer$RequestContext r9 = r3     // Catch:{ all -> 0x03a5 }
                        boolean r9 = r9.aborted     // Catch:{ all -> 0x03a5 }
                        if (r9 == 0) goto L_0x0328
                        monitor-exit(r7)     // Catch:{ all -> 0x03a5 }
                        org.apache.cordova.filetransfer.FileTransfer$RequestContext r2 = r3     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        monitor-enter(r2)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        org.apache.cordova.filetransfer.FileTransfer$RequestContext r6 = r3     // Catch:{ all -> 0x0324 }
                        r7 = 0
                        r6.connection = r7     // Catch:{ all -> 0x0324 }
                        monitor-exit(r2)     // Catch:{ all -> 0x0324 }
                        org.apache.cordova.filetransfer.FileTransfer.safeClose(r3)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        java.util.HashMap r2 = org.apache.cordova.filetransfer.FileTransfer.activeRequests
                        monitor-enter(r2)
                        java.util.HashMap r3 = org.apache.cordova.filetransfer.FileTransfer.activeRequests     // Catch:{ all -> 0x0320 }
                        java.lang.String r4 = r16     // Catch:{ all -> 0x0320 }
                        r3.remove(r4)     // Catch:{ all -> 0x0320 }
                        monitor-exit(r2)     // Catch:{ all -> 0x0320 }
                        return
                    L_0x0320:
                        r0 = move-exception
                        r3 = r0
                        monitor-exit(r2)     // Catch:{ all -> 0x0320 }
                        throw r3
                    L_0x0324:
                        r0 = move-exception
                        r3 = r0
                        monitor-exit(r2)     // Catch:{ all -> 0x0324 }
                        throw r3     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                    L_0x0328:
                        org.apache.cordova.filetransfer.FileTransfer$RequestContext r9 = r3     // Catch:{ all -> 0x03a5 }
                        r9.connection = r8     // Catch:{ all -> 0x03a5 }
                        monitor-exit(r7)     // Catch:{ all -> 0x03a5 }
                        java.io.ByteArrayOutputStream r7 = new java.io.ByteArrayOutputStream     // Catch:{ all -> 0x03a9 }
                        int r9 = r8.getContentLength()     // Catch:{ all -> 0x03a9 }
                        r10 = 1024(0x400, float:1.435E-42)
                        int r9 = java.lang.Math.max(r10, r9)     // Catch:{ all -> 0x03a9 }
                        r7.<init>(r9)     // Catch:{ all -> 0x03a9 }
                        byte[] r9 = new byte[r10]     // Catch:{ all -> 0x03a9 }
                    L_0x033e:
                        int r10 = r3.read(r9)     // Catch:{ all -> 0x03a9 }
                        if (r10 <= 0) goto L_0x0349
                        r11 = 0
                        r7.write(r9, r11, r10)     // Catch:{ all -> 0x03a9 }
                        goto L_0x033e
                    L_0x0349:
                        java.lang.String r9 = "UTF-8"
                        java.lang.String r7 = r7.toString(r9)     // Catch:{ all -> 0x03a9 }
                        org.apache.cordova.filetransfer.FileTransfer$RequestContext r9 = r3     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        monitor-enter(r9)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        org.apache.cordova.filetransfer.FileTransfer$RequestContext r10 = r3     // Catch:{ all -> 0x03a1 }
                        r11 = 0
                        r10.connection = r11     // Catch:{ all -> 0x03a1 }
                        monitor-exit(r9)     // Catch:{ all -> 0x03a1 }
                        org.apache.cordova.filetransfer.FileTransfer.safeClose(r3)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        java.lang.String r3 = "FileTransfer"
                        java.lang.String r9 = "got response from server"
                        org.apache.cordova.LOG.m37d(r3, r9)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        java.lang.String r3 = "FileTransfer"
                        r9 = 256(0x100, float:3.59E-43)
                        int r10 = r7.length()     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        int r9 = java.lang.Math.min(r9, r10)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        r10 = 0
                        java.lang.String r9 = r7.substring(r10, r9)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        org.apache.cordova.LOG.m37d(r3, r9)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        r6.setResponseCode(r2)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        r6.setResponse(r7)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        org.apache.cordova.filetransfer.FileTransfer$RequestContext r2 = r3     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        org.apache.cordova.PluginResult r3 = new org.apache.cordova.PluginResult     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        org.apache.cordova.PluginResult$Status r7 = org.apache.cordova.PluginResult.Status.OK     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        org.json.JSONObject r6 = r6.toJSONObject()     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        r3.<init>((org.apache.cordova.PluginResult.Status) r7, (org.json.JSONObject) r6)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        r2.sendPluginResult(r3)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        java.util.HashMap r2 = org.apache.cordova.filetransfer.FileTransfer.activeRequests
                        monitor-enter(r2)
                        java.util.HashMap r3 = org.apache.cordova.filetransfer.FileTransfer.activeRequests     // Catch:{ all -> 0x039d }
                        java.lang.String r4 = r16     // Catch:{ all -> 0x039d }
                        r3.remove(r4)     // Catch:{ all -> 0x039d }
                        monitor-exit(r2)     // Catch:{ all -> 0x039d }
                        goto L_0x0501
                    L_0x039d:
                        r0 = move-exception
                        r3 = r0
                        monitor-exit(r2)     // Catch:{ all -> 0x039d }
                        throw r3
                    L_0x03a1:
                        r0 = move-exception
                        r2 = r0
                        monitor-exit(r9)     // Catch:{ all -> 0x03a1 }
                        throw r2     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                    L_0x03a5:
                        r0 = move-exception
                        r2 = r0
                        monitor-exit(r7)     // Catch:{ all -> 0x03a5 }
                        throw r2     // Catch:{ all -> 0x03a9 }
                    L_0x03a9:
                        r0 = move-exception
                        r2 = r0
                        goto L_0x03af
                    L_0x03ac:
                        r0 = move-exception
                        r2 = r0
                        r3 = 0
                    L_0x03af:
                        org.apache.cordova.filetransfer.FileTransfer$RequestContext r6 = r3     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        monitor-enter(r6)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        org.apache.cordova.filetransfer.FileTransfer$RequestContext r7 = r3     // Catch:{ all -> 0x03bc }
                        r9 = 0
                        r7.connection = r9     // Catch:{ all -> 0x03bc }
                        monitor-exit(r6)     // Catch:{ all -> 0x03bc }
                        org.apache.cordova.filetransfer.FileTransfer.safeClose(r3)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                        throw r2     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                    L_0x03bc:
                        r0 = move-exception
                        r2 = r0
                        monitor-exit(r6)     // Catch:{ all -> 0x03bc }
                        throw r2     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                    L_0x03c0:
                        r0 = move-exception
                        r3 = r0
                        monitor-exit(r2)     // Catch:{ all -> 0x03c0 }
                        throw r3     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03c4, JSONException -> 0x0448, Throwable -> 0x0400 }
                    L_0x03c4:
                        r0 = move-exception
                        r2 = r0
                        r10 = r4
                        goto L_0x03fd
                    L_0x03c8:
                        r0 = move-exception
                        goto L_0x03cc
                    L_0x03ca:
                        r0 = move-exception
                        r5 = r13
                    L_0x03cc:
                        r9 = r3
                        r18 = r4
                        goto L_0x03e0
                    L_0x03d0:
                        r0 = move-exception
                        r5 = r13
                        r10 = 0
                    L_0x03d3:
                        r4 = r0
                    L_0x03d4:
                        monitor-exit(r14)     // Catch:{ all -> 0x03d8 }
                        throw r4     // Catch:{ all -> 0x03d6 }
                    L_0x03d6:
                        r0 = move-exception
                        goto L_0x03dd
                    L_0x03d8:
                        r0 = move-exception
                        goto L_0x03d3
                    L_0x03da:
                        r0 = move-exception
                        r5 = r13
                        r10 = 0
                    L_0x03dd:
                        r9 = r3
                    L_0x03de:
                        r18 = 0
                    L_0x03e0:
                        r3 = r0
                        goto L_0x03e9
                    L_0x03e2:
                        r0 = move-exception
                        r5 = r13
                        r9 = 0
                        r10 = 0
                        r3 = r0
                        r18 = 0
                    L_0x03e9:
                        java.io.InputStream r2 = r2.inputStream     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03f2, JSONException -> 0x0448, Throwable -> 0x0400 }
                        org.apache.cordova.filetransfer.FileTransfer.safeClose(r2)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03f2, JSONException -> 0x0448, Throwable -> 0x0400 }
                        org.apache.cordova.filetransfer.FileTransfer.safeClose(r9)     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03f2, JSONException -> 0x0448, Throwable -> 0x0400 }
                        throw r3     // Catch:{ FileNotFoundException -> 0x0408, IOException -> 0x03f2, JSONException -> 0x0448, Throwable -> 0x0400 }
                    L_0x03f2:
                        r0 = move-exception
                        r2 = r0
                        r3 = r5
                        r10 = r18
                        goto L_0x0479
                    L_0x03f9:
                        r0 = move-exception
                        r5 = r13
                        r10 = 0
                        r2 = r0
                    L_0x03fd:
                        r3 = r5
                        goto L_0x0479
                    L_0x0400:
                        r0 = move-exception
                        r2 = r0
                        goto L_0x0414
                    L_0x0403:
                        r0 = move-exception
                        r10 = 0
                        r2 = r0
                        goto L_0x0479
                    L_0x0408:
                        r0 = move-exception
                        r2 = r0
                        goto L_0x04d3
                    L_0x040c:
                        r0 = move-exception
                        r2 = r0
                        goto L_0x0506
                    L_0x0410:
                        r0 = move-exception
                        r9 = 0
                        r2 = r0
                        r8 = r9
                    L_0x0414:
                        int r3 = org.apache.cordova.filetransfer.FileTransfer.CONNECTION_ERR     // Catch:{ all -> 0x040c }
                        java.lang.String r4 = r4     // Catch:{ all -> 0x040c }
                        java.lang.String r5 = r9     // Catch:{ all -> 0x040c }
                        org.json.JSONObject r3 = org.apache.cordova.filetransfer.FileTransfer.createFileTransferError(r3, r4, r5, r8, r2)     // Catch:{ all -> 0x040c }
                        java.lang.String r4 = "FileTransfer"
                        java.lang.String r5 = r3.toString()     // Catch:{ all -> 0x040c }
                        org.apache.cordova.LOG.m41e((java.lang.String) r4, (java.lang.String) r5, (java.lang.Throwable) r2)     // Catch:{ all -> 0x040c }
                        org.apache.cordova.filetransfer.FileTransfer$RequestContext r2 = r3     // Catch:{ all -> 0x040c }
                        org.apache.cordova.PluginResult r4 = new org.apache.cordova.PluginResult     // Catch:{ all -> 0x040c }
                        org.apache.cordova.PluginResult$Status r5 = org.apache.cordova.PluginResult.Status.IO_EXCEPTION     // Catch:{ all -> 0x040c }
                        r4.<init>((org.apache.cordova.PluginResult.Status) r5, (org.json.JSONObject) r3)     // Catch:{ all -> 0x040c }
                        r2.sendPluginResult(r4)     // Catch:{ all -> 0x040c }
                        java.util.HashMap r2 = org.apache.cordova.filetransfer.FileTransfer.activeRequests
                        monitor-enter(r2)
                        java.util.HashMap r3 = org.apache.cordova.filetransfer.FileTransfer.activeRequests     // Catch:{ all -> 0x0444 }
                        java.lang.String r4 = r16     // Catch:{ all -> 0x0444 }
                        r3.remove(r4)     // Catch:{ all -> 0x0444 }
                        monitor-exit(r2)     // Catch:{ all -> 0x0444 }
                        goto L_0x0501
                    L_0x0444:
                        r0 = move-exception
                        r3 = r0
                        monitor-exit(r2)     // Catch:{ all -> 0x0444 }
                        throw r3
                    L_0x0448:
                        r0 = move-exception
                        r2 = r0
                        java.lang.String r3 = "FileTransfer"
                        java.lang.String r4 = r2.getMessage()     // Catch:{ all -> 0x040c }
                        org.apache.cordova.LOG.m41e((java.lang.String) r3, (java.lang.String) r4, (java.lang.Throwable) r2)     // Catch:{ all -> 0x040c }
                        org.apache.cordova.filetransfer.FileTransfer$RequestContext r2 = r3     // Catch:{ all -> 0x040c }
                        org.apache.cordova.PluginResult r3 = new org.apache.cordova.PluginResult     // Catch:{ all -> 0x040c }
                        org.apache.cordova.PluginResult$Status r4 = org.apache.cordova.PluginResult.Status.JSON_EXCEPTION     // Catch:{ all -> 0x040c }
                        r3.<init>(r4)     // Catch:{ all -> 0x040c }
                        r2.sendPluginResult(r3)     // Catch:{ all -> 0x040c }
                        java.util.HashMap r2 = org.apache.cordova.filetransfer.FileTransfer.activeRequests
                        monitor-enter(r2)
                        java.util.HashMap r3 = org.apache.cordova.filetransfer.FileTransfer.activeRequests     // Catch:{ all -> 0x0470 }
                        java.lang.String r4 = r16     // Catch:{ all -> 0x0470 }
                        r3.remove(r4)     // Catch:{ all -> 0x0470 }
                        monitor-exit(r2)     // Catch:{ all -> 0x0470 }
                        goto L_0x0501
                    L_0x0470:
                        r0 = move-exception
                        r3 = r0
                        monitor-exit(r2)     // Catch:{ all -> 0x0470 }
                        throw r3
                    L_0x0474:
                        r0 = move-exception
                        r9 = 0
                        r10 = 0
                        r2 = r0
                        r8 = r9
                    L_0x0479:
                        int r4 = org.apache.cordova.filetransfer.FileTransfer.CONNECTION_ERR     // Catch:{ all -> 0x040c }
                        java.lang.String r5 = r4     // Catch:{ all -> 0x040c }
                        java.lang.String r6 = r9     // Catch:{ all -> 0x040c }
                        org.json.JSONObject r4 = org.apache.cordova.filetransfer.FileTransfer.createFileTransferError(r4, r5, r6, r8, r2)     // Catch:{ all -> 0x040c }
                        java.lang.String r5 = "FileTransfer"
                        java.lang.String r6 = r4.toString()     // Catch:{ all -> 0x040c }
                        org.apache.cordova.LOG.m41e((java.lang.String) r5, (java.lang.String) r6, (java.lang.Throwable) r2)     // Catch:{ all -> 0x040c }
                        java.lang.String r2 = "FileTransfer"
                        java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x040c }
                        r5.<init>()     // Catch:{ all -> 0x040c }
                        java.lang.String r6 = "Failed after uploading "
                        r5.append(r6)     // Catch:{ all -> 0x040c }
                        r5.append(r10)     // Catch:{ all -> 0x040c }
                        java.lang.String r6 = " of "
                        r5.append(r6)     // Catch:{ all -> 0x040c }
                        r5.append(r3)     // Catch:{ all -> 0x040c }
                        java.lang.String r3 = " bytes."
                        r5.append(r3)     // Catch:{ all -> 0x040c }
                        java.lang.String r3 = r5.toString()     // Catch:{ all -> 0x040c }
                        org.apache.cordova.LOG.m40e(r2, r3)     // Catch:{ all -> 0x040c }
                        org.apache.cordova.filetransfer.FileTransfer$RequestContext r2 = r3     // Catch:{ all -> 0x040c }
                        org.apache.cordova.PluginResult r3 = new org.apache.cordova.PluginResult     // Catch:{ all -> 0x040c }
                        org.apache.cordova.PluginResult$Status r5 = org.apache.cordova.PluginResult.Status.IO_EXCEPTION     // Catch:{ all -> 0x040c }
                        r3.<init>((org.apache.cordova.PluginResult.Status) r5, (org.json.JSONObject) r4)     // Catch:{ all -> 0x040c }
                        r2.sendPluginResult(r3)     // Catch:{ all -> 0x040c }
                        java.util.HashMap r2 = org.apache.cordova.filetransfer.FileTransfer.activeRequests
                        monitor-enter(r2)
                        java.util.HashMap r3 = org.apache.cordova.filetransfer.FileTransfer.activeRequests     // Catch:{ all -> 0x04cb }
                        java.lang.String r4 = r16     // Catch:{ all -> 0x04cb }
                        r3.remove(r4)     // Catch:{ all -> 0x04cb }
                        monitor-exit(r2)     // Catch:{ all -> 0x04cb }
                        goto L_0x0501
                    L_0x04cb:
                        r0 = move-exception
                        r3 = r0
                        monitor-exit(r2)     // Catch:{ all -> 0x04cb }
                        throw r3
                    L_0x04cf:
                        r0 = move-exception
                        r9 = 0
                        r2 = r0
                        r8 = r9
                    L_0x04d3:
                        int r3 = org.apache.cordova.filetransfer.FileTransfer.FILE_NOT_FOUND_ERR     // Catch:{ all -> 0x040c }
                        java.lang.String r4 = r4     // Catch:{ all -> 0x040c }
                        java.lang.String r5 = r9     // Catch:{ all -> 0x040c }
                        org.json.JSONObject r3 = org.apache.cordova.filetransfer.FileTransfer.createFileTransferError(r3, r4, r5, r8, r2)     // Catch:{ all -> 0x040c }
                        java.lang.String r4 = "FileTransfer"
                        java.lang.String r5 = r3.toString()     // Catch:{ all -> 0x040c }
                        org.apache.cordova.LOG.m41e((java.lang.String) r4, (java.lang.String) r5, (java.lang.Throwable) r2)     // Catch:{ all -> 0x040c }
                        org.apache.cordova.filetransfer.FileTransfer$RequestContext r2 = r3     // Catch:{ all -> 0x040c }
                        org.apache.cordova.PluginResult r4 = new org.apache.cordova.PluginResult     // Catch:{ all -> 0x040c }
                        org.apache.cordova.PluginResult$Status r5 = org.apache.cordova.PluginResult.Status.IO_EXCEPTION     // Catch:{ all -> 0x040c }
                        r4.<init>((org.apache.cordova.PluginResult.Status) r5, (org.json.JSONObject) r3)     // Catch:{ all -> 0x040c }
                        r2.sendPluginResult(r4)     // Catch:{ all -> 0x040c }
                        java.util.HashMap r2 = org.apache.cordova.filetransfer.FileTransfer.activeRequests
                        monitor-enter(r2)
                        java.util.HashMap r3 = org.apache.cordova.filetransfer.FileTransfer.activeRequests     // Catch:{ all -> 0x0502 }
                        java.lang.String r4 = r16     // Catch:{ all -> 0x0502 }
                        r3.remove(r4)     // Catch:{ all -> 0x0502 }
                        monitor-exit(r2)     // Catch:{ all -> 0x0502 }
                    L_0x0501:
                        return
                    L_0x0502:
                        r0 = move-exception
                        r3 = r0
                        monitor-exit(r2)     // Catch:{ all -> 0x0502 }
                        throw r3
                    L_0x0506:
                        java.util.HashMap r3 = org.apache.cordova.filetransfer.FileTransfer.activeRequests
                        monitor-enter(r3)
                        java.util.HashMap r4 = org.apache.cordova.filetransfer.FileTransfer.activeRequests     // Catch:{ all -> 0x0516 }
                        java.lang.String r5 = r16     // Catch:{ all -> 0x0516 }
                        r4.remove(r5)     // Catch:{ all -> 0x0516 }
                        monitor-exit(r3)     // Catch:{ all -> 0x0516 }
                        throw r2
                    L_0x0516:
                        r0 = move-exception
                        r2 = r0
                        monitor-exit(r3)     // Catch:{ all -> 0x0516 }
                        throw r2
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.apache.cordova.filetransfer.FileTransfer.C05721.run():void");
                }
            };
            threadPool.execute(r1);
            return;
        }
        JSONObject createFileTransferError = createFileTransferError(INVALID_URL_ERR, str, str2, (String) null, 0, (Throwable) null);
        LOG.m40e(LOG_TAG, "Unsupported URI: " + remapUri);
        callbackContext2.sendPluginResult(new PluginResult(PluginResult.Status.IO_EXCEPTION, createFileTransferError));
    }

    /* access modifiers changed from: private */
    public static void safeClose(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException unused) {
            }
        }
    }

    /* access modifiers changed from: private */
    public static TrackingInputStream getInputStream(URLConnection uRLConnection) throws IOException {
        String contentEncoding = uRLConnection.getContentEncoding();
        if (contentEncoding == null || !contentEncoding.equalsIgnoreCase(HttpRequest.ENCODING_GZIP)) {
            return new SimpleTrackingInputStream(uRLConnection.getInputStream());
        }
        return new TrackingGZIPInputStream(new ExposedGZIPInputStream(uRLConnection.getInputStream()));
    }

    /* access modifiers changed from: private */
    public static JSONObject createFileTransferError(int i, String str, String str2, URLConnection uRLConnection, Throwable th) {
        String str3;
        BufferedReader bufferedReader;
        StringBuilder sb = new StringBuilder();
        int i2 = 0;
        String str4 = null;
        if (uRLConnection != null) {
            try {
                if (uRLConnection instanceof HttpURLConnection) {
                    i2 = ((HttpURLConnection) uRLConnection).getResponseCode();
                    InputStream errorStream = ((HttpURLConnection) uRLConnection).getErrorStream();
                    if (errorStream != null) {
                        bufferedReader = new BufferedReader(new InputStreamReader(errorStream, HttpRequest.CHARSET_UTF8));
                        String readLine = bufferedReader.readLine();
                        while (readLine != null) {
                            sb.append(readLine);
                            readLine = bufferedReader.readLine();
                            if (readLine != null) {
                                sb.append(10);
                            }
                        }
                        String sb2 = sb.toString();
                        try {
                            bufferedReader.close();
                            str4 = sb2;
                        } catch (Throwable th2) {
                            th = th2;
                            str4 = sb2;
                            LOG.m50w(LOG_TAG, "Error getting HTTP status code from connection.", th);
                            str3 = str4;
                            return createFileTransferError(i, str, str2, str3, Integer.valueOf(i2), th);
                        }
                    }
                }
                str3 = str4;
            } catch (Throwable th3) {
                th = th3;
                LOG.m50w(LOG_TAG, "Error getting HTTP status code from connection.", th);
                str3 = str4;
                return createFileTransferError(i, str, str2, str3, Integer.valueOf(i2), th);
            }
        } else {
            str3 = null;
        }
        return createFileTransferError(i, str, str2, str3, Integer.valueOf(i2), th);
    }

    /* access modifiers changed from: private */
    public static JSONObject createFileTransferError(int i, String str, String str2, String str3, Integer num, Throwable th) {
        JSONObject jSONObject;
        try {
            jSONObject = new JSONObject();
            try {
                jSONObject.put("code", i);
                jSONObject.put("source", str);
                jSONObject.put("target", str2);
                if (str3 != null) {
                    jSONObject.put(PushConstants.BODY, str3);
                }
                if (num != null) {
                    jSONObject.put("http_status", num);
                }
                if (th != null) {
                    String message = th.getMessage();
                    if (message == null || "".equals(message)) {
                        message = th.toString();
                    }
                    jSONObject.put("exception", message);
                }
            } catch (JSONException e) {
                e = e;
                LOG.m41e(LOG_TAG, e.getMessage(), (Throwable) e);
                return jSONObject;
            }
        } catch (JSONException e2) {
            e = e2;
            jSONObject = null;
            LOG.m41e(LOG_TAG, e.getMessage(), (Throwable) e);
            return jSONObject;
        }
        return jSONObject;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0006, code lost:
        r1 = r1.optString(r2);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.String getArgument(org.json.JSONArray r1, int r2, java.lang.String r3) {
        /*
            int r0 = r1.length()
            if (r0 <= r2) goto L_0x0014
            java.lang.String r1 = r1.optString(r2)
            if (r1 == 0) goto L_0x0014
            java.lang.String r2 = "null"
            boolean r2 = r2.equals(r1)
            if (r2 == 0) goto L_0x0015
        L_0x0014:
            r1 = r3
        L_0x0015:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.cordova.filetransfer.FileTransfer.getArgument(org.json.JSONArray, int, java.lang.String):java.lang.String");
    }

    private void download(String str, String str2, JSONArray jSONArray, CallbackContext callbackContext) throws JSONException {
        String str3 = str;
        String str4 = str2;
        JSONArray jSONArray2 = jSONArray;
        CallbackContext callbackContext2 = callbackContext;
        LOG.m37d(LOG_TAG, "download " + str3 + " to " + str4);
        final CordovaResourceApi resourceApi = this.webView.getResourceApi();
        final String string = jSONArray2.getString(3);
        final JSONObject optJSONObject = jSONArray2.optJSONObject(4);
        Uri remapUri = resourceApi.remapUri(Uri.parse(str));
        int uriType = CordovaResourceApi.getUriType(remapUri);
        boolean z = !(uriType == 6) && uriType != 5;
        if (uriType == -1) {
            JSONObject createFileTransferError = createFileTransferError(INVALID_URL_ERR, str, str2, (String) null, (Integer) null, (Throwable) null);
            LOG.m40e(LOG_TAG, "Unsupported URI: " + remapUri);
            callbackContext2.sendPluginResult(new PluginResult(PluginResult.Status.IO_EXCEPTION, createFileTransferError));
            return;
        }
        boolean z2 = null;
        if (z) {
            z2 = true;
        }
        if (z2 == null) {
            try {
                z2 = Boolean.valueOf(((Whitelist) this.webView.getClass().getMethod("getWhitelist", new Class[0]).invoke(this.webView, new Object[0])).isUrlWhiteListed(str3));
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException unused) {
            }
        }
        if (z2 == null) {
            try {
                PluginManager pluginManager = (PluginManager) this.webView.getClass().getMethod("getPluginManager", new Class[0]).invoke(this.webView, new Object[0]);
                z2 = (Boolean) pluginManager.getClass().getMethod("shouldAllowRequest", new Class[]{String.class}).invoke(pluginManager, new Object[]{str3});
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException unused2) {
            }
        }
        if (!Boolean.TRUE.equals(z2)) {
            LOG.m49w(LOG_TAG, "Source URL is not in white list: '" + str3 + "'");
            callbackContext2.sendPluginResult(new PluginResult(PluginResult.Status.IO_EXCEPTION, createFileTransferError(CONNECTION_ERR, str, str2, (String) null, 401, (Throwable) null)));
            return;
        }
        final RequestContext requestContext = new RequestContext(str3, str4, callbackContext2);
        synchronized (activeRequests) {
            activeRequests.put(string, requestContext);
        }
        final String str5 = str2;
        final Uri uri = remapUri;
        final boolean z3 = z;
        final String str6 = str;
        this.f59cordova.getThreadPool().execute(new Runnable() {
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v3, resolved type: org.apache.cordova.PluginResult} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v7, resolved type: org.apache.cordova.PluginResult} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v11, resolved type: org.apache.cordova.PluginResult} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v15, resolved type: org.apache.cordova.PluginResult} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v18, resolved type: org.apache.cordova.PluginResult} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v14, resolved type: java.net.HttpURLConnection} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v31, resolved type: org.apache.cordova.PluginResult} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v32, resolved type: org.apache.cordova.PluginResult} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v33, resolved type: org.apache.cordova.PluginResult} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v34, resolved type: org.apache.cordova.PluginResult} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v35, resolved type: org.apache.cordova.PluginResult} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v20, resolved type: org.apache.cordova.filetransfer.FileTransfer$SimpleTrackingInputStream} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v68, resolved type: org.apache.cordova.PluginResult} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v72, resolved type: org.apache.cordova.PluginResult} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v76, resolved type: org.apache.cordova.PluginResult} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v80, resolved type: org.apache.cordova.PluginResult} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v82, resolved type: org.apache.cordova.PluginResult} */
            /* JADX WARNING: type inference failed for: r7v0, types: [java.net.URLConnection] */
            /* JADX WARNING: type inference failed for: r7v2, types: [java.net.URLConnection] */
            /* JADX WARNING: type inference failed for: r7v6, types: [java.net.URLConnection] */
            /* JADX WARNING: type inference failed for: r7v10 */
            /* JADX WARNING: type inference failed for: r7v14, types: [java.net.URLConnection] */
            /* JADX WARNING: type inference failed for: r7v28, types: [java.net.HttpURLConnection, java.net.URLConnection] */
            /* JADX WARNING: type inference failed for: r7v43 */
            /* JADX WARNING: type inference failed for: r7v49 */
            /* JADX WARNING: type inference failed for: r7v50 */
            /* JADX WARNING: type inference failed for: r7v51 */
            /* JADX WARNING: type inference failed for: r7v52 */
            /* JADX WARNING: type inference failed for: r7v53 */
            /* JADX WARNING: type inference failed for: r7v57 */
            /* JADX WARNING: type inference failed for: r7v62 */
            /* JADX WARNING: type inference failed for: r7v64 */
            /* JADX WARNING: type inference failed for: r7v67 */
            /* JADX WARNING: type inference failed for: r7v71 */
            /* JADX WARNING: type inference failed for: r7v75 */
            /* JADX WARNING: type inference failed for: r7v79 */
            /* JADX WARNING: type inference failed for: r7v81 */
            /*  JADX ERROR: IndexOutOfBoundsException in pass: RegionMakerVisitor
                java.lang.IndexOutOfBoundsException: Index 0 out of bounds for length 0
                	at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:64)
                	at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:70)
                	at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:248)
                	at java.base/java.util.Objects.checkIndex(Objects.java:372)
                	at java.base/java.util.ArrayList.get(ArrayList.java:458)
                	at jadx.core.dex.nodes.InsnNode.getArg(InsnNode.java:101)
                	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:611)
                	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
                	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
                	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
                	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
                	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
                	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
                	at jadx.core.dex.visitors.regions.RegionMaker.processExcHandler(RegionMaker.java:1043)
                	at jadx.core.dex.visitors.regions.RegionMaker.processTryCatchBlocks(RegionMaker.java:975)
                	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:52)
                */
            /* JADX WARNING: Multi-variable type inference failed */
            /* JADX WARNING: Unknown top exception splitter block from list: {B:119:0x0210=Splitter:B:119:0x0210, B:153:0x0280=Splitter:B:153:0x0280} */
            /* JADX WARNING: Unknown top exception splitter block from list: {B:222:0x0319=Splitter:B:222:0x0319, B:263:0x039c=Splitter:B:263:0x039c, B:282:0x03de=Splitter:B:282:0x03de} */
            public void run() {
                /*
                    r15 = this;
                    org.apache.cordova.filetransfer.FileTransfer$RequestContext r0 = r3
                    boolean r0 = r0.aborted
                    if (r0 == 0) goto L_0x0007
                    return
                L_0x0007:
                    java.lang.String r0 = r4
                    android.net.Uri r0 = android.net.Uri.parse(r0)
                    org.apache.cordova.CordovaResourceApi r1 = r5
                    java.lang.String r2 = r0.getScheme()
                    if (r2 == 0) goto L_0x0016
                    goto L_0x0021
                L_0x0016:
                    java.io.File r0 = new java.io.File
                    java.lang.String r2 = r4
                    r0.<init>(r2)
                    android.net.Uri r0 = android.net.Uri.fromFile(r0)
                L_0x0021:
                    android.net.Uri r0 = r1.remapUri(r0)
                    r1 = 1
                    r2 = 0
                    r3 = 0
                    org.apache.cordova.CordovaResourceApi r4 = r5     // Catch:{ FileNotFoundException -> 0x03d9, IOException -> 0x0397, JSONException -> 0x0356, Throwable -> 0x0314, all -> 0x030d }
                    java.io.File r4 = r4.mapUriToFile(r0)     // Catch:{ FileNotFoundException -> 0x03d9, IOException -> 0x0397, JSONException -> 0x0356, Throwable -> 0x0314, all -> 0x030d }
                    org.apache.cordova.filetransfer.FileTransfer$RequestContext r5 = r3     // Catch:{ FileNotFoundException -> 0x0309, IOException -> 0x0305, JSONException -> 0x0302, Throwable -> 0x02ff, all -> 0x02fc }
                    r5.targetFile = r4     // Catch:{ FileNotFoundException -> 0x0309, IOException -> 0x0305, JSONException -> 0x0302, Throwable -> 0x02ff, all -> 0x02fc }
                    java.lang.String r5 = "FileTransfer"
                    java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ FileNotFoundException -> 0x0309, IOException -> 0x0305, JSONException -> 0x0302, Throwable -> 0x02ff, all -> 0x02fc }
                    r6.<init>()     // Catch:{ FileNotFoundException -> 0x0309, IOException -> 0x0305, JSONException -> 0x0302, Throwable -> 0x02ff, all -> 0x02fc }
                    java.lang.String r7 = "Download file:"
                    r6.append(r7)     // Catch:{ FileNotFoundException -> 0x0309, IOException -> 0x0305, JSONException -> 0x0302, Throwable -> 0x02ff, all -> 0x02fc }
                    android.net.Uri r7 = r6     // Catch:{ FileNotFoundException -> 0x0309, IOException -> 0x0305, JSONException -> 0x0302, Throwable -> 0x02ff, all -> 0x02fc }
                    r6.append(r7)     // Catch:{ FileNotFoundException -> 0x0309, IOException -> 0x0305, JSONException -> 0x0302, Throwable -> 0x02ff, all -> 0x02fc }
                    java.lang.String r6 = r6.toString()     // Catch:{ FileNotFoundException -> 0x0309, IOException -> 0x0305, JSONException -> 0x0302, Throwable -> 0x02ff, all -> 0x02fc }
                    org.apache.cordova.LOG.m37d(r5, r6)     // Catch:{ FileNotFoundException -> 0x0309, IOException -> 0x0305, JSONException -> 0x0302, Throwable -> 0x02ff, all -> 0x02fc }
                    org.apache.cordova.filetransfer.FileProgressResult r5 = new org.apache.cordova.filetransfer.FileProgressResult     // Catch:{ FileNotFoundException -> 0x0309, IOException -> 0x0305, JSONException -> 0x0302, Throwable -> 0x02ff, all -> 0x02fc }
                    r5.<init>()     // Catch:{ FileNotFoundException -> 0x0309, IOException -> 0x0305, JSONException -> 0x0302, Throwable -> 0x02ff, all -> 0x02fc }
                    boolean r6 = r7     // Catch:{ FileNotFoundException -> 0x0309, IOException -> 0x0305, JSONException -> 0x0302, Throwable -> 0x02ff, all -> 0x02fc }
                    if (r6 == 0) goto L_0x0078
                    org.apache.cordova.CordovaResourceApi r6 = r5     // Catch:{ FileNotFoundException -> 0x0309, IOException -> 0x0305, JSONException -> 0x0302, Throwable -> 0x02ff, all -> 0x02fc }
                    android.net.Uri r7 = r6     // Catch:{ FileNotFoundException -> 0x0309, IOException -> 0x0305, JSONException -> 0x0302, Throwable -> 0x02ff, all -> 0x02fc }
                    org.apache.cordova.CordovaResourceApi$OpenForReadResult r6 = r6.openForRead(r7)     // Catch:{ FileNotFoundException -> 0x0309, IOException -> 0x0305, JSONException -> 0x0302, Throwable -> 0x02ff, all -> 0x02fc }
                    long r7 = r6.length     // Catch:{ FileNotFoundException -> 0x0309, IOException -> 0x0305, JSONException -> 0x0302, Throwable -> 0x02ff, all -> 0x02fc }
                    r9 = -1
                    int r11 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
                    if (r11 == 0) goto L_0x006b
                    r5.setLengthComputable(r1)     // Catch:{ FileNotFoundException -> 0x0309, IOException -> 0x0305, JSONException -> 0x0302, Throwable -> 0x02ff, all -> 0x02fc }
                    long r7 = r6.length     // Catch:{ FileNotFoundException -> 0x0309, IOException -> 0x0305, JSONException -> 0x0302, Throwable -> 0x02ff, all -> 0x02fc }
                    r5.setTotal(r7)     // Catch:{ FileNotFoundException -> 0x0309, IOException -> 0x0305, JSONException -> 0x0302, Throwable -> 0x02ff, all -> 0x02fc }
                L_0x006b:
                    org.apache.cordova.filetransfer.FileTransfer$SimpleTrackingInputStream r7 = new org.apache.cordova.filetransfer.FileTransfer$SimpleTrackingInputStream     // Catch:{ FileNotFoundException -> 0x0309, IOException -> 0x0305, JSONException -> 0x0302, Throwable -> 0x02ff, all -> 0x02fc }
                    java.io.InputStream r6 = r6.inputStream     // Catch:{ FileNotFoundException -> 0x0309, IOException -> 0x0305, JSONException -> 0x0302, Throwable -> 0x02ff, all -> 0x02fc }
                    r7.<init>(r6)     // Catch:{ FileNotFoundException -> 0x0309, IOException -> 0x0305, JSONException -> 0x0302, Throwable -> 0x02ff, all -> 0x02fc }
                    r9 = r3
                    r8 = r7
                    r6 = 0
                    r7 = r9
                    goto L_0x012a
                L_0x0078:
                    org.apache.cordova.CordovaResourceApi r6 = r5     // Catch:{ FileNotFoundException -> 0x0309, IOException -> 0x0305, JSONException -> 0x0302, Throwable -> 0x02ff, all -> 0x02fc }
                    android.net.Uri r7 = r6     // Catch:{ FileNotFoundException -> 0x0309, IOException -> 0x0305, JSONException -> 0x0302, Throwable -> 0x02ff, all -> 0x02fc }
                    java.net.HttpURLConnection r6 = r6.createHttpConnection(r7)     // Catch:{ FileNotFoundException -> 0x0309, IOException -> 0x0305, JSONException -> 0x0302, Throwable -> 0x02ff, all -> 0x02fc }
                    java.lang.String r7 = "GET"
                    r6.setRequestMethod(r7)     // Catch:{ FileNotFoundException -> 0x02f7, IOException -> 0x02f2, JSONException -> 0x02ed, Throwable -> 0x02e9, all -> 0x02e5 }
                    org.apache.cordova.filetransfer.FileTransfer r7 = org.apache.cordova.filetransfer.FileTransfer.this     // Catch:{ FileNotFoundException -> 0x02f7, IOException -> 0x02f2, JSONException -> 0x02ed, Throwable -> 0x02e9, all -> 0x02e5 }
                    android.net.Uri r8 = r6     // Catch:{ FileNotFoundException -> 0x02f7, IOException -> 0x02f2, JSONException -> 0x02ed, Throwable -> 0x02e9, all -> 0x02e5 }
                    java.lang.String r8 = r8.toString()     // Catch:{ FileNotFoundException -> 0x02f7, IOException -> 0x02f2, JSONException -> 0x02ed, Throwable -> 0x02e9, all -> 0x02e5 }
                    java.lang.String r7 = r7.getCookies(r8)     // Catch:{ FileNotFoundException -> 0x02f7, IOException -> 0x02f2, JSONException -> 0x02ed, Throwable -> 0x02e9, all -> 0x02e5 }
                    if (r7 == 0) goto L_0x0098
                    java.lang.String r8 = "cookie"
                    r6.setRequestProperty(r8, r7)     // Catch:{ FileNotFoundException -> 0x02f7, IOException -> 0x02f2, JSONException -> 0x02ed, Throwable -> 0x02e9, all -> 0x02e5 }
                L_0x0098:
                    java.lang.String r7 = "Accept-Encoding"
                    java.lang.String r8 = "gzip"
                    r6.setRequestProperty(r7, r8)     // Catch:{ FileNotFoundException -> 0x02f7, IOException -> 0x02f2, JSONException -> 0x02ed, Throwable -> 0x02e9, all -> 0x02e5 }
                    org.json.JSONObject r7 = r8     // Catch:{ FileNotFoundException -> 0x02f7, IOException -> 0x02f2, JSONException -> 0x02ed, Throwable -> 0x02e9, all -> 0x02e5 }
                    if (r7 == 0) goto L_0x00a8
                    org.json.JSONObject r7 = r8     // Catch:{ FileNotFoundException -> 0x02f7, IOException -> 0x02f2, JSONException -> 0x02ed, Throwable -> 0x02e9, all -> 0x02e5 }
                    org.apache.cordova.filetransfer.FileTransfer.addHeadersToRequest(r6, r7)     // Catch:{ FileNotFoundException -> 0x02f7, IOException -> 0x02f2, JSONException -> 0x02ed, Throwable -> 0x02e9, all -> 0x02e5 }
                L_0x00a8:
                    r6.connect()     // Catch:{ FileNotFoundException -> 0x02f7, IOException -> 0x02f2, JSONException -> 0x02ed, Throwable -> 0x02e9, all -> 0x02e5 }
                    int r7 = r6.getResponseCode()     // Catch:{ FileNotFoundException -> 0x02f7, IOException -> 0x02f2, JSONException -> 0x02ed, Throwable -> 0x02e9, all -> 0x02e5 }
                    r8 = 304(0x130, float:4.26E-43)
                    if (r7 != r8) goto L_0x00fe
                    r6.disconnect()     // Catch:{ FileNotFoundException -> 0x00f9, IOException -> 0x00f4, JSONException -> 0x00ef, Throwable -> 0x00ea, all -> 0x00e4 }
                    java.lang.String r7 = "FileTransfer"
                    java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ FileNotFoundException -> 0x00f9, IOException -> 0x00f4, JSONException -> 0x00ef, Throwable -> 0x00ea, all -> 0x00e4 }
                    r8.<init>()     // Catch:{ FileNotFoundException -> 0x00f9, IOException -> 0x00f4, JSONException -> 0x00ef, Throwable -> 0x00ea, all -> 0x00e4 }
                    java.lang.String r9 = "Resource not modified: "
                    r8.append(r9)     // Catch:{ FileNotFoundException -> 0x00f9, IOException -> 0x00f4, JSONException -> 0x00ef, Throwable -> 0x00ea, all -> 0x00e4 }
                    java.lang.String r9 = r9     // Catch:{ FileNotFoundException -> 0x00f9, IOException -> 0x00f4, JSONException -> 0x00ef, Throwable -> 0x00ea, all -> 0x00e4 }
                    r8.append(r9)     // Catch:{ FileNotFoundException -> 0x00f9, IOException -> 0x00f4, JSONException -> 0x00ef, Throwable -> 0x00ea, all -> 0x00e4 }
                    java.lang.String r8 = r8.toString()     // Catch:{ FileNotFoundException -> 0x00f9, IOException -> 0x00f4, JSONException -> 0x00ef, Throwable -> 0x00ea, all -> 0x00e4 }
                    org.apache.cordova.LOG.m37d(r7, r8)     // Catch:{ FileNotFoundException -> 0x00f9, IOException -> 0x00f4, JSONException -> 0x00ef, Throwable -> 0x00ea, all -> 0x00e4 }
                    int r7 = org.apache.cordova.filetransfer.FileTransfer.NOT_MODIFIED_ERR     // Catch:{ FileNotFoundException -> 0x00f9, IOException -> 0x00f4, JSONException -> 0x00ef, Throwable -> 0x00ea, all -> 0x00e4 }
                    java.lang.String r8 = r9     // Catch:{ FileNotFoundException -> 0x00f9, IOException -> 0x00f4, JSONException -> 0x00ef, Throwable -> 0x00ea, all -> 0x00e4 }
                    java.lang.String r9 = r4     // Catch:{ FileNotFoundException -> 0x00f9, IOException -> 0x00f4, JSONException -> 0x00ef, Throwable -> 0x00ea, all -> 0x00e4 }
                    org.json.JSONObject r7 = org.apache.cordova.filetransfer.FileTransfer.createFileTransferError(r7, r8, r9, r6, r3)     // Catch:{ FileNotFoundException -> 0x00f9, IOException -> 0x00f4, JSONException -> 0x00ef, Throwable -> 0x00ea, all -> 0x00e4 }
                    org.apache.cordova.PluginResult r8 = new org.apache.cordova.PluginResult     // Catch:{ FileNotFoundException -> 0x00f9, IOException -> 0x00f4, JSONException -> 0x00ef, Throwable -> 0x00ea, all -> 0x00e4 }
                    org.apache.cordova.PluginResult$Status r9 = org.apache.cordova.PluginResult.Status.ERROR     // Catch:{ FileNotFoundException -> 0x00f9, IOException -> 0x00f4, JSONException -> 0x00ef, Throwable -> 0x00ea, all -> 0x00e4 }
                    r8.<init>((org.apache.cordova.PluginResult.Status) r9, (org.json.JSONObject) r7)     // Catch:{ FileNotFoundException -> 0x00f9, IOException -> 0x00f4, JSONException -> 0x00ef, Throwable -> 0x00ea, all -> 0x00e4 }
                    r7 = r6
                    r9 = r8
                    r6 = 1
                    r8 = r3
                    goto L_0x012a
                L_0x00e4:
                    r0 = move-exception
                    r9 = r3
                    r7 = r6
                    r6 = 1
                    goto L_0x041f
                L_0x00ea:
                    r0 = move-exception
                    r9 = r3
                    r7 = r6
                    goto L_0x0319
                L_0x00ef:
                    r0 = move-exception
                    r9 = r3
                    r7 = r6
                    goto L_0x035b
                L_0x00f4:
                    r0 = move-exception
                    r9 = r3
                    r7 = r6
                    goto L_0x039c
                L_0x00f9:
                    r0 = move-exception
                    r9 = r3
                    r7 = r6
                    goto L_0x03de
                L_0x00fe:
                    java.lang.String r7 = r6.getContentEncoding()     // Catch:{ FileNotFoundException -> 0x02f7, IOException -> 0x02f2, JSONException -> 0x02ed, Throwable -> 0x02e9, all -> 0x02e5 }
                    if (r7 == 0) goto L_0x0110
                    java.lang.String r7 = r6.getContentEncoding()     // Catch:{ FileNotFoundException -> 0x02f7, IOException -> 0x02f2, JSONException -> 0x02ed, Throwable -> 0x02e9, all -> 0x02e5 }
                    java.lang.String r8 = "gzip"
                    boolean r7 = r7.equalsIgnoreCase(r8)     // Catch:{ FileNotFoundException -> 0x02f7, IOException -> 0x02f2, JSONException -> 0x02ed, Throwable -> 0x02e9, all -> 0x02e5 }
                    if (r7 == 0) goto L_0x0122
                L_0x0110:
                    int r7 = r6.getContentLength()     // Catch:{ FileNotFoundException -> 0x02f7, IOException -> 0x02f2, JSONException -> 0x02ed, Throwable -> 0x02e9, all -> 0x02e5 }
                    r8 = -1
                    if (r7 == r8) goto L_0x0122
                    r5.setLengthComputable(r1)     // Catch:{ FileNotFoundException -> 0x02f7, IOException -> 0x02f2, JSONException -> 0x02ed, Throwable -> 0x02e9, all -> 0x02e5 }
                    int r7 = r6.getContentLength()     // Catch:{ FileNotFoundException -> 0x02f7, IOException -> 0x02f2, JSONException -> 0x02ed, Throwable -> 0x02e9, all -> 0x02e5 }
                    long r7 = (long) r7     // Catch:{ FileNotFoundException -> 0x02f7, IOException -> 0x02f2, JSONException -> 0x02ed, Throwable -> 0x02e9, all -> 0x02e5 }
                    r5.setTotal(r7)     // Catch:{ FileNotFoundException -> 0x02f7, IOException -> 0x02f2, JSONException -> 0x02ed, Throwable -> 0x02e9, all -> 0x02e5 }
                L_0x0122:
                    org.apache.cordova.filetransfer.FileTransfer$TrackingInputStream r7 = org.apache.cordova.filetransfer.FileTransfer.getInputStream(r6)     // Catch:{ FileNotFoundException -> 0x02f7, IOException -> 0x02f2, JSONException -> 0x02ed, Throwable -> 0x02e9, all -> 0x02e5 }
                    r9 = r3
                    r8 = r7
                    r7 = r6
                    r6 = 0
                L_0x012a:
                    if (r6 != 0) goto L_0x02a5
                    org.apache.cordova.filetransfer.FileTransfer$RequestContext r10 = r3     // Catch:{ all -> 0x027e }
                    monitor-enter(r10)     // Catch:{ all -> 0x027e }
                    org.apache.cordova.filetransfer.FileTransfer$RequestContext r11 = r3     // Catch:{ all -> 0x027b }
                    boolean r11 = r11.aborted     // Catch:{ all -> 0x027b }
                    if (r11 == 0) goto L_0x0185
                    monitor-exit(r10)     // Catch:{ all -> 0x027b }
                    org.apache.cordova.filetransfer.FileTransfer$RequestContext r0 = r3     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x029d, JSONException -> 0x0299, Throwable -> 0x0295, all -> 0x0292 }
                    monitor-enter(r0)     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x029d, JSONException -> 0x0299, Throwable -> 0x0295, all -> 0x0292 }
                    org.apache.cordova.filetransfer.FileTransfer$RequestContext r1 = r3     // Catch:{ all -> 0x0182 }
                    r1.connection = r3     // Catch:{ all -> 0x0182 }
                    monitor-exit(r0)     // Catch:{ all -> 0x0182 }
                    org.apache.cordova.filetransfer.FileTransfer.safeClose(r8)     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x029d, JSONException -> 0x0299, Throwable -> 0x0295, all -> 0x0292 }
                    org.apache.cordova.filetransfer.FileTransfer.safeClose(r3)     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x029d, JSONException -> 0x0299, Throwable -> 0x0295, all -> 0x0292 }
                    java.util.HashMap r0 = org.apache.cordova.filetransfer.FileTransfer.activeRequests
                    monitor-enter(r0)
                    java.util.HashMap r1 = org.apache.cordova.filetransfer.FileTransfer.activeRequests     // Catch:{ all -> 0x017f }
                    java.lang.String r2 = r10     // Catch:{ all -> 0x017f }
                    r1.remove(r2)     // Catch:{ all -> 0x017f }
                    monitor-exit(r0)     // Catch:{ all -> 0x017f }
                    if (r9 != 0) goto L_0x0166
                    org.apache.cordova.PluginResult r9 = new org.apache.cordova.PluginResult
                    org.apache.cordova.PluginResult$Status r0 = org.apache.cordova.PluginResult.Status.ERROR
                    int r1 = org.apache.cordova.filetransfer.FileTransfer.CONNECTION_ERR
                    java.lang.String r2 = r9
                    java.lang.String r5 = r4
                    org.json.JSONObject r1 = org.apache.cordova.filetransfer.FileTransfer.createFileTransferError(r1, r2, r5, r7, r3)
                    r9.<init>((org.apache.cordova.PluginResult.Status) r0, (org.json.JSONObject) r1)
                L_0x0166:
                    if (r6 != 0) goto L_0x0179
                    int r0 = r9.getStatus()
                    org.apache.cordova.PluginResult$Status r1 = org.apache.cordova.PluginResult.Status.OK
                    int r1 = r1.ordinal()
                    if (r0 == r1) goto L_0x0179
                    if (r4 == 0) goto L_0x0179
                    r4.delete()
                L_0x0179:
                    org.apache.cordova.filetransfer.FileTransfer$RequestContext r0 = r3
                    r0.sendPluginResult(r9)
                    return
                L_0x017f:
                    r1 = move-exception
                    monitor-exit(r0)     // Catch:{ all -> 0x017f }
                    throw r1
                L_0x0182:
                    r1 = move-exception
                    monitor-exit(r0)     // Catch:{ all -> 0x0182 }
                    throw r1     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x029d, JSONException -> 0x0299, Throwable -> 0x0295, all -> 0x0292 }
                L_0x0185:
                    org.apache.cordova.filetransfer.FileTransfer$RequestContext r11 = r3     // Catch:{ all -> 0x027b }
                    r11.connection = r7     // Catch:{ all -> 0x027b }
                    monitor-exit(r10)     // Catch:{ all -> 0x027b }
                    r10 = 16384(0x4000, float:2.2959E-41)
                    byte[] r10 = new byte[r10]     // Catch:{ all -> 0x027e }
                    org.apache.cordova.CordovaResourceApi r11 = r5     // Catch:{ all -> 0x027e }
                    java.io.OutputStream r11 = r11.openOutputStream(r0)     // Catch:{ all -> 0x027e }
                L_0x0194:
                    int r12 = r8.read(r10)     // Catch:{ all -> 0x0279 }
                    if (r12 <= 0) goto L_0x01b8
                    r11.write(r10, r2, r12)     // Catch:{ all -> 0x0279 }
                    long r12 = r8.getTotalRawBytesRead()     // Catch:{ all -> 0x0279 }
                    r5.setLoaded(r12)     // Catch:{ all -> 0x0279 }
                    org.apache.cordova.PluginResult r12 = new org.apache.cordova.PluginResult     // Catch:{ all -> 0x0279 }
                    org.apache.cordova.PluginResult$Status r13 = org.apache.cordova.PluginResult.Status.OK     // Catch:{ all -> 0x0279 }
                    org.json.JSONObject r14 = r5.toJSONObject()     // Catch:{ all -> 0x0279 }
                    r12.<init>((org.apache.cordova.PluginResult.Status) r13, (org.json.JSONObject) r14)     // Catch:{ all -> 0x0279 }
                    r12.setKeepCallback(r1)     // Catch:{ all -> 0x0279 }
                    org.apache.cordova.filetransfer.FileTransfer$RequestContext r13 = r3     // Catch:{ all -> 0x0279 }
                    r13.sendPluginResult(r12)     // Catch:{ all -> 0x0279 }
                    goto L_0x0194
                L_0x01b8:
                    org.apache.cordova.filetransfer.FileTransfer$RequestContext r1 = r3     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x029d, JSONException -> 0x0299, Throwable -> 0x0295, all -> 0x0292 }
                    monitor-enter(r1)     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x029d, JSONException -> 0x0299, Throwable -> 0x0295, all -> 0x0292 }
                    org.apache.cordova.filetransfer.FileTransfer$RequestContext r5 = r3     // Catch:{ all -> 0x0276 }
                    r5.connection = r3     // Catch:{ all -> 0x0276 }
                    monitor-exit(r1)     // Catch:{ all -> 0x0276 }
                    org.apache.cordova.filetransfer.FileTransfer.safeClose(r8)     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x029d, JSONException -> 0x0299, Throwable -> 0x0295, all -> 0x0292 }
                    org.apache.cordova.filetransfer.FileTransfer.safeClose(r11)     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x029d, JSONException -> 0x0299, Throwable -> 0x0295, all -> 0x0292 }
                    java.lang.String r1 = "FileTransfer"
                    java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x029d, JSONException -> 0x0299, Throwable -> 0x0295, all -> 0x0292 }
                    r5.<init>()     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x029d, JSONException -> 0x0299, Throwable -> 0x0295, all -> 0x0292 }
                    java.lang.String r8 = "Saved file: "
                    r5.append(r8)     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x029d, JSONException -> 0x0299, Throwable -> 0x0295, all -> 0x0292 }
                    java.lang.String r8 = r4     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x029d, JSONException -> 0x0299, Throwable -> 0x0295, all -> 0x0292 }
                    r5.append(r8)     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x029d, JSONException -> 0x0299, Throwable -> 0x0295, all -> 0x0292 }
                    java.lang.String r5 = r5.toString()     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x029d, JSONException -> 0x0299, Throwable -> 0x0295, all -> 0x0292 }
                    org.apache.cordova.LOG.m37d(r1, r5)     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x029d, JSONException -> 0x0299, Throwable -> 0x0295, all -> 0x0292 }
                    org.apache.cordova.filetransfer.FileTransfer r1 = org.apache.cordova.filetransfer.FileTransfer.this     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x029d, JSONException -> 0x0299, Throwable -> 0x0295, all -> 0x0292 }
                    org.apache.cordova.CordovaWebView r1 = r1.webView     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x029d, JSONException -> 0x0299, Throwable -> 0x0295, all -> 0x0292 }
                    java.lang.Class r1 = r1.getClass()     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x029d, JSONException -> 0x0299, Throwable -> 0x0295, all -> 0x0292 }
                    java.lang.String r5 = "getPluginManager"
                    java.lang.Class[] r8 = new java.lang.Class[r2]     // Catch:{ IllegalAccessException | NoSuchMethodException | InvocationTargetException -> 0x01fb }
                    java.lang.reflect.Method r5 = r1.getMethod(r5, r8)     // Catch:{ IllegalAccessException | NoSuchMethodException | InvocationTargetException -> 0x01fb }
                    org.apache.cordova.filetransfer.FileTransfer r8 = org.apache.cordova.filetransfer.FileTransfer.this     // Catch:{ IllegalAccessException | NoSuchMethodException | InvocationTargetException -> 0x01fb }
                    org.apache.cordova.CordovaWebView r8 = r8.webView     // Catch:{ IllegalAccessException | NoSuchMethodException | InvocationTargetException -> 0x01fb }
                    java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ IllegalAccessException | NoSuchMethodException | InvocationTargetException -> 0x01fb }
                    java.lang.Object r2 = r5.invoke(r8, r2)     // Catch:{ IllegalAccessException | NoSuchMethodException | InvocationTargetException -> 0x01fb }
                    org.apache.cordova.PluginManager r2 = (org.apache.cordova.PluginManager) r2     // Catch:{ IllegalAccessException | NoSuchMethodException | InvocationTargetException -> 0x01fb }
                    goto L_0x01fc
                L_0x01fb:
                    r2 = r3
                L_0x01fc:
                    if (r2 != 0) goto L_0x020f
                    java.lang.String r5 = "pluginManager"
                    java.lang.reflect.Field r1 = r1.getField(r5)     // Catch:{ IllegalAccessException | NoSuchFieldException -> 0x020f }
                    org.apache.cordova.filetransfer.FileTransfer r5 = org.apache.cordova.filetransfer.FileTransfer.this     // Catch:{ IllegalAccessException | NoSuchFieldException -> 0x020f }
                    org.apache.cordova.CordovaWebView r5 = r5.webView     // Catch:{ IllegalAccessException | NoSuchFieldException -> 0x020f }
                    java.lang.Object r1 = r1.get(r5)     // Catch:{ IllegalAccessException | NoSuchFieldException -> 0x020f }
                    org.apache.cordova.PluginManager r1 = (org.apache.cordova.PluginManager) r1     // Catch:{ IllegalAccessException | NoSuchFieldException -> 0x020f }
                    goto L_0x0210
                L_0x020f:
                    r1 = r2
                L_0x0210:
                    org.apache.cordova.CordovaResourceApi r2 = r5     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x029d, JSONException -> 0x0299, Throwable -> 0x0295, all -> 0x0292 }
                    java.io.File r0 = r2.mapUriToFile(r0)     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x029d, JSONException -> 0x0299, Throwable -> 0x0295, all -> 0x0292 }
                    org.apache.cordova.filetransfer.FileTransfer$RequestContext r2 = r3     // Catch:{ FileNotFoundException -> 0x0272, IOException -> 0x026e, JSONException -> 0x026a, Throwable -> 0x0266, all -> 0x0261 }
                    r2.targetFile = r0     // Catch:{ FileNotFoundException -> 0x0272, IOException -> 0x026e, JSONException -> 0x026a, Throwable -> 0x0266, all -> 0x0261 }
                    java.lang.String r2 = "File"
                    org.apache.cordova.CordovaPlugin r1 = r1.getPlugin(r2)     // Catch:{ FileNotFoundException -> 0x0272, IOException -> 0x026e, JSONException -> 0x026a, Throwable -> 0x0266, all -> 0x0261 }
                    org.apache.cordova.file.FileUtils r1 = (org.apache.cordova.file.FileUtils) r1     // Catch:{ FileNotFoundException -> 0x0272, IOException -> 0x026e, JSONException -> 0x026a, Throwable -> 0x0266, all -> 0x0261 }
                    if (r1 == 0) goto L_0x024f
                    org.json.JSONObject r1 = r1.getEntryForFile(r0)     // Catch:{ FileNotFoundException -> 0x0272, IOException -> 0x026e, JSONException -> 0x026a, Throwable -> 0x0266, all -> 0x0261 }
                    if (r1 == 0) goto L_0x0234
                    org.apache.cordova.PluginResult r2 = new org.apache.cordova.PluginResult     // Catch:{ FileNotFoundException -> 0x0272, IOException -> 0x026e, JSONException -> 0x026a, Throwable -> 0x0266, all -> 0x0261 }
                    org.apache.cordova.PluginResult$Status r4 = org.apache.cordova.PluginResult.Status.OK     // Catch:{ FileNotFoundException -> 0x0272, IOException -> 0x026e, JSONException -> 0x026a, Throwable -> 0x0266, all -> 0x0261 }
                    r2.<init>((org.apache.cordova.PluginResult.Status) r4, (org.json.JSONObject) r1)     // Catch:{ FileNotFoundException -> 0x0272, IOException -> 0x026e, JSONException -> 0x026a, Throwable -> 0x0266, all -> 0x0261 }
                    r9 = r2
                    goto L_0x02a6
                L_0x0234:
                    int r1 = org.apache.cordova.filetransfer.FileTransfer.CONNECTION_ERR     // Catch:{ FileNotFoundException -> 0x0272, IOException -> 0x026e, JSONException -> 0x026a, Throwable -> 0x0266, all -> 0x0261 }
                    java.lang.String r2 = r9     // Catch:{ FileNotFoundException -> 0x0272, IOException -> 0x026e, JSONException -> 0x026a, Throwable -> 0x0266, all -> 0x0261 }
                    java.lang.String r4 = r4     // Catch:{ FileNotFoundException -> 0x0272, IOException -> 0x026e, JSONException -> 0x026a, Throwable -> 0x0266, all -> 0x0261 }
                    org.json.JSONObject r1 = org.apache.cordova.filetransfer.FileTransfer.createFileTransferError(r1, r2, r4, r7, r3)     // Catch:{ FileNotFoundException -> 0x0272, IOException -> 0x026e, JSONException -> 0x026a, Throwable -> 0x0266, all -> 0x0261 }
                    java.lang.String r2 = "FileTransfer"
                    java.lang.String r4 = "File plugin cannot represent download path"
                    org.apache.cordova.LOG.m40e(r2, r4)     // Catch:{ FileNotFoundException -> 0x0272, IOException -> 0x026e, JSONException -> 0x026a, Throwable -> 0x0266, all -> 0x0261 }
                    org.apache.cordova.PluginResult r2 = new org.apache.cordova.PluginResult     // Catch:{ FileNotFoundException -> 0x0272, IOException -> 0x026e, JSONException -> 0x026a, Throwable -> 0x0266, all -> 0x0261 }
                    org.apache.cordova.PluginResult$Status r4 = org.apache.cordova.PluginResult.Status.IO_EXCEPTION     // Catch:{ FileNotFoundException -> 0x0272, IOException -> 0x026e, JSONException -> 0x026a, Throwable -> 0x0266, all -> 0x0261 }
                    r2.<init>((org.apache.cordova.PluginResult.Status) r4, (org.json.JSONObject) r1)     // Catch:{ FileNotFoundException -> 0x0272, IOException -> 0x026e, JSONException -> 0x026a, Throwable -> 0x0266, all -> 0x0261 }
                    r9 = r2
                    goto L_0x02a6
                L_0x024f:
                    java.lang.String r1 = "FileTransfer"
                    java.lang.String r2 = "File plugin not found; cannot save downloaded file"
                    org.apache.cordova.LOG.m40e(r1, r2)     // Catch:{ FileNotFoundException -> 0x0272, IOException -> 0x026e, JSONException -> 0x026a, Throwable -> 0x0266, all -> 0x0261 }
                    org.apache.cordova.PluginResult r1 = new org.apache.cordova.PluginResult     // Catch:{ FileNotFoundException -> 0x0272, IOException -> 0x026e, JSONException -> 0x026a, Throwable -> 0x0266, all -> 0x0261 }
                    org.apache.cordova.PluginResult$Status r2 = org.apache.cordova.PluginResult.Status.ERROR     // Catch:{ FileNotFoundException -> 0x0272, IOException -> 0x026e, JSONException -> 0x026a, Throwable -> 0x0266, all -> 0x0261 }
                    java.lang.String r4 = "File plugin not found; cannot save downloaded file"
                    r1.<init>((org.apache.cordova.PluginResult.Status) r2, (java.lang.String) r4)     // Catch:{ FileNotFoundException -> 0x0272, IOException -> 0x026e, JSONException -> 0x026a, Throwable -> 0x0266, all -> 0x0261 }
                    r9 = r1
                    goto L_0x02a6
                L_0x0261:
                    r1 = move-exception
                    r4 = r0
                    r0 = r1
                    goto L_0x041f
                L_0x0266:
                    r1 = move-exception
                    r4 = r0
                    r0 = r1
                    goto L_0x0296
                L_0x026a:
                    r1 = move-exception
                    r4 = r0
                    r0 = r1
                    goto L_0x029a
                L_0x026e:
                    r1 = move-exception
                    r4 = r0
                    r0 = r1
                    goto L_0x029e
                L_0x0272:
                    r1 = move-exception
                    r4 = r0
                    r0 = r1
                    goto L_0x02a2
                L_0x0276:
                    r0 = move-exception
                    monitor-exit(r1)     // Catch:{ all -> 0x0276 }
                    throw r0     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x029d, JSONException -> 0x0299, Throwable -> 0x0295, all -> 0x0292 }
                L_0x0279:
                    r0 = move-exception
                    goto L_0x0280
                L_0x027b:
                    r0 = move-exception
                    monitor-exit(r10)     // Catch:{ all -> 0x027b }
                    throw r0     // Catch:{ all -> 0x027e }
                L_0x027e:
                    r0 = move-exception
                    r11 = r3
                L_0x0280:
                    org.apache.cordova.filetransfer.FileTransfer$RequestContext r1 = r3     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x029d, JSONException -> 0x0299, Throwable -> 0x0295, all -> 0x0292 }
                    monitor-enter(r1)     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x029d, JSONException -> 0x0299, Throwable -> 0x0295, all -> 0x0292 }
                    org.apache.cordova.filetransfer.FileTransfer$RequestContext r2 = r3     // Catch:{ all -> 0x028f }
                    r2.connection = r3     // Catch:{ all -> 0x028f }
                    monitor-exit(r1)     // Catch:{ all -> 0x028f }
                    org.apache.cordova.filetransfer.FileTransfer.safeClose(r8)     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x029d, JSONException -> 0x0299, Throwable -> 0x0295, all -> 0x0292 }
                    org.apache.cordova.filetransfer.FileTransfer.safeClose(r11)     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x029d, JSONException -> 0x0299, Throwable -> 0x0295, all -> 0x0292 }
                    throw r0     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x029d, JSONException -> 0x0299, Throwable -> 0x0295, all -> 0x0292 }
                L_0x028f:
                    r0 = move-exception
                    monitor-exit(r1)     // Catch:{ all -> 0x028f }
                    throw r0     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x029d, JSONException -> 0x0299, Throwable -> 0x0295, all -> 0x0292 }
                L_0x0292:
                    r0 = move-exception
                    goto L_0x041f
                L_0x0295:
                    r0 = move-exception
                L_0x0296:
                    r1 = r6
                    goto L_0x0319
                L_0x0299:
                    r0 = move-exception
                L_0x029a:
                    r1 = r6
                    goto L_0x035b
                L_0x029d:
                    r0 = move-exception
                L_0x029e:
                    r1 = r6
                    goto L_0x039c
                L_0x02a1:
                    r0 = move-exception
                L_0x02a2:
                    r1 = r6
                    goto L_0x03de
                L_0x02a5:
                    r0 = r4
                L_0x02a6:
                    java.util.HashMap r1 = org.apache.cordova.filetransfer.FileTransfer.activeRequests
                    monitor-enter(r1)
                    java.util.HashMap r2 = org.apache.cordova.filetransfer.FileTransfer.activeRequests     // Catch:{ all -> 0x02e2 }
                    java.lang.String r4 = r10     // Catch:{ all -> 0x02e2 }
                    r2.remove(r4)     // Catch:{ all -> 0x02e2 }
                    monitor-exit(r1)     // Catch:{ all -> 0x02e2 }
                    if (r9 != 0) goto L_0x02c8
                    org.apache.cordova.PluginResult r9 = new org.apache.cordova.PluginResult
                    org.apache.cordova.PluginResult$Status r1 = org.apache.cordova.PluginResult.Status.ERROR
                    int r2 = org.apache.cordova.filetransfer.FileTransfer.CONNECTION_ERR
                    java.lang.String r4 = r9
                    java.lang.String r5 = r4
                    org.json.JSONObject r2 = org.apache.cordova.filetransfer.FileTransfer.createFileTransferError(r2, r4, r5, r7, r3)
                    r9.<init>((org.apache.cordova.PluginResult.Status) r1, (org.json.JSONObject) r2)
                L_0x02c8:
                    if (r6 != 0) goto L_0x02db
                    int r1 = r9.getStatus()
                    org.apache.cordova.PluginResult$Status r2 = org.apache.cordova.PluginResult.Status.OK
                    int r2 = r2.ordinal()
                    if (r1 == r2) goto L_0x02db
                    if (r0 == 0) goto L_0x02db
                    r0.delete()
                L_0x02db:
                    org.apache.cordova.filetransfer.FileTransfer$RequestContext r0 = r3
                    r0.sendPluginResult(r9)
                    goto L_0x0419
                L_0x02e2:
                    r0 = move-exception
                    monitor-exit(r1)     // Catch:{ all -> 0x02e2 }
                    throw r0
                L_0x02e5:
                    r0 = move-exception
                    r9 = r3
                    r7 = r6
                    goto L_0x0311
                L_0x02e9:
                    r0 = move-exception
                    r9 = r3
                    r7 = r6
                    goto L_0x0318
                L_0x02ed:
                    r0 = move-exception
                    r9 = r3
                    r7 = r6
                    goto L_0x035a
                L_0x02f2:
                    r0 = move-exception
                    r9 = r3
                    r7 = r6
                    goto L_0x039b
                L_0x02f7:
                    r0 = move-exception
                    r9 = r3
                    r7 = r6
                    goto L_0x03dd
                L_0x02fc:
                    r0 = move-exception
                    r7 = r3
                    goto L_0x0310
                L_0x02ff:
                    r0 = move-exception
                    r7 = r3
                    goto L_0x0317
                L_0x0302:
                    r0 = move-exception
                    r7 = r3
                    goto L_0x0359
                L_0x0305:
                    r0 = move-exception
                    r7 = r3
                    goto L_0x039a
                L_0x0309:
                    r0 = move-exception
                    r7 = r3
                    goto L_0x03dc
                L_0x030d:
                    r0 = move-exception
                    r4 = r3
                    r7 = r4
                L_0x0310:
                    r9 = r7
                L_0x0311:
                    r6 = 0
                    goto L_0x041f
                L_0x0314:
                    r0 = move-exception
                    r4 = r3
                    r7 = r4
                L_0x0317:
                    r9 = r7
                L_0x0318:
                    r1 = 0
                L_0x0319:
                    int r2 = org.apache.cordova.filetransfer.FileTransfer.CONNECTION_ERR     // Catch:{ all -> 0x041d }
                    java.lang.String r5 = r9     // Catch:{ all -> 0x041d }
                    java.lang.String r6 = r4     // Catch:{ all -> 0x041d }
                    org.json.JSONObject r2 = org.apache.cordova.filetransfer.FileTransfer.createFileTransferError(r2, r5, r6, r7, r0)     // Catch:{ all -> 0x041d }
                    java.lang.String r5 = "FileTransfer"
                    java.lang.String r6 = r2.toString()     // Catch:{ all -> 0x041d }
                    org.apache.cordova.LOG.m41e((java.lang.String) r5, (java.lang.String) r6, (java.lang.Throwable) r0)     // Catch:{ all -> 0x041d }
                    org.apache.cordova.PluginResult r0 = new org.apache.cordova.PluginResult     // Catch:{ all -> 0x041d }
                    org.apache.cordova.PluginResult$Status r5 = org.apache.cordova.PluginResult.Status.IO_EXCEPTION     // Catch:{ all -> 0x041d }
                    r0.<init>((org.apache.cordova.PluginResult.Status) r5, (org.json.JSONObject) r2)     // Catch:{ all -> 0x041d }
                    java.util.HashMap r2 = org.apache.cordova.filetransfer.FileTransfer.activeRequests
                    monitor-enter(r2)
                    java.util.HashMap r3 = org.apache.cordova.filetransfer.FileTransfer.activeRequests     // Catch:{ all -> 0x0353 }
                    java.lang.String r5 = r10     // Catch:{ all -> 0x0353 }
                    r3.remove(r5)     // Catch:{ all -> 0x0353 }
                    monitor-exit(r2)     // Catch:{ all -> 0x0353 }
                    if (r1 != 0) goto L_0x038d
                    int r1 = r0.getStatus()
                    org.apache.cordova.PluginResult$Status r2 = org.apache.cordova.PluginResult.Status.OK
                    int r2 = r2.ordinal()
                    if (r1 == r2) goto L_0x038d
                    if (r4 == 0) goto L_0x038d
                    goto L_0x038a
                L_0x0353:
                    r0 = move-exception
                    monitor-exit(r2)     // Catch:{ all -> 0x0353 }
                    throw r0
                L_0x0356:
                    r0 = move-exception
                    r4 = r3
                    r7 = r4
                L_0x0359:
                    r9 = r7
                L_0x035a:
                    r1 = 0
                L_0x035b:
                    java.lang.String r2 = "FileTransfer"
                    java.lang.String r5 = r0.getMessage()     // Catch:{ all -> 0x041d }
                    org.apache.cordova.LOG.m41e((java.lang.String) r2, (java.lang.String) r5, (java.lang.Throwable) r0)     // Catch:{ all -> 0x041d }
                    org.apache.cordova.PluginResult r0 = new org.apache.cordova.PluginResult     // Catch:{ all -> 0x041d }
                    org.apache.cordova.PluginResult$Status r2 = org.apache.cordova.PluginResult.Status.JSON_EXCEPTION     // Catch:{ all -> 0x041d }
                    r0.<init>(r2)     // Catch:{ all -> 0x041d }
                    java.util.HashMap r2 = org.apache.cordova.filetransfer.FileTransfer.activeRequests
                    monitor-enter(r2)
                    java.util.HashMap r3 = org.apache.cordova.filetransfer.FileTransfer.activeRequests     // Catch:{ all -> 0x0394 }
                    java.lang.String r5 = r10     // Catch:{ all -> 0x0394 }
                    r3.remove(r5)     // Catch:{ all -> 0x0394 }
                    monitor-exit(r2)     // Catch:{ all -> 0x0394 }
                    if (r1 != 0) goto L_0x038d
                    int r1 = r0.getStatus()
                    org.apache.cordova.PluginResult$Status r2 = org.apache.cordova.PluginResult.Status.OK
                    int r2 = r2.ordinal()
                    if (r1 == r2) goto L_0x038d
                    if (r4 == 0) goto L_0x038d
                L_0x038a:
                    r4.delete()
                L_0x038d:
                    org.apache.cordova.filetransfer.FileTransfer$RequestContext r1 = r3
                    r1.sendPluginResult(r0)
                    goto L_0x0419
                L_0x0394:
                    r0 = move-exception
                    monitor-exit(r2)     // Catch:{ all -> 0x0394 }
                    throw r0
                L_0x0397:
                    r0 = move-exception
                    r4 = r3
                    r7 = r4
                L_0x039a:
                    r9 = r7
                L_0x039b:
                    r1 = 0
                L_0x039c:
                    int r2 = org.apache.cordova.filetransfer.FileTransfer.CONNECTION_ERR     // Catch:{ all -> 0x041d }
                    java.lang.String r5 = r9     // Catch:{ all -> 0x041d }
                    java.lang.String r6 = r4     // Catch:{ all -> 0x041d }
                    org.json.JSONObject r2 = org.apache.cordova.filetransfer.FileTransfer.createFileTransferError(r2, r5, r6, r7, r0)     // Catch:{ all -> 0x041d }
                    java.lang.String r5 = "FileTransfer"
                    java.lang.String r6 = r2.toString()     // Catch:{ all -> 0x041d }
                    org.apache.cordova.LOG.m41e((java.lang.String) r5, (java.lang.String) r6, (java.lang.Throwable) r0)     // Catch:{ all -> 0x041d }
                    org.apache.cordova.PluginResult r0 = new org.apache.cordova.PluginResult     // Catch:{ all -> 0x041d }
                    org.apache.cordova.PluginResult$Status r5 = org.apache.cordova.PluginResult.Status.IO_EXCEPTION     // Catch:{ all -> 0x041d }
                    r0.<init>((org.apache.cordova.PluginResult.Status) r5, (org.json.JSONObject) r2)     // Catch:{ all -> 0x041d }
                    java.util.HashMap r2 = org.apache.cordova.filetransfer.FileTransfer.activeRequests
                    monitor-enter(r2)
                    java.util.HashMap r3 = org.apache.cordova.filetransfer.FileTransfer.activeRequests     // Catch:{ all -> 0x03d6 }
                    java.lang.String r5 = r10     // Catch:{ all -> 0x03d6 }
                    r3.remove(r5)     // Catch:{ all -> 0x03d6 }
                    monitor-exit(r2)     // Catch:{ all -> 0x03d6 }
                    if (r1 != 0) goto L_0x038d
                    int r1 = r0.getStatus()
                    org.apache.cordova.PluginResult$Status r2 = org.apache.cordova.PluginResult.Status.OK
                    int r2 = r2.ordinal()
                    if (r1 == r2) goto L_0x038d
                    if (r4 == 0) goto L_0x038d
                    goto L_0x038a
                L_0x03d6:
                    r0 = move-exception
                    monitor-exit(r2)     // Catch:{ all -> 0x03d6 }
                    throw r0
                L_0x03d9:
                    r0 = move-exception
                    r4 = r3
                    r7 = r4
                L_0x03dc:
                    r9 = r7
                L_0x03dd:
                    r1 = 0
                L_0x03de:
                    int r2 = org.apache.cordova.filetransfer.FileTransfer.FILE_NOT_FOUND_ERR     // Catch:{ all -> 0x041d }
                    java.lang.String r5 = r9     // Catch:{ all -> 0x041d }
                    java.lang.String r6 = r4     // Catch:{ all -> 0x041d }
                    org.json.JSONObject r2 = org.apache.cordova.filetransfer.FileTransfer.createFileTransferError(r2, r5, r6, r7, r0)     // Catch:{ all -> 0x041d }
                    java.lang.String r5 = "FileTransfer"
                    java.lang.String r6 = r2.toString()     // Catch:{ all -> 0x041d }
                    org.apache.cordova.LOG.m41e((java.lang.String) r5, (java.lang.String) r6, (java.lang.Throwable) r0)     // Catch:{ all -> 0x041d }
                    org.apache.cordova.PluginResult r0 = new org.apache.cordova.PluginResult     // Catch:{ all -> 0x041d }
                    org.apache.cordova.PluginResult$Status r5 = org.apache.cordova.PluginResult.Status.IO_EXCEPTION     // Catch:{ all -> 0x041d }
                    r0.<init>((org.apache.cordova.PluginResult.Status) r5, (org.json.JSONObject) r2)     // Catch:{ all -> 0x041d }
                    java.util.HashMap r2 = org.apache.cordova.filetransfer.FileTransfer.activeRequests
                    monitor-enter(r2)
                    java.util.HashMap r3 = org.apache.cordova.filetransfer.FileTransfer.activeRequests     // Catch:{ all -> 0x041a }
                    java.lang.String r5 = r10     // Catch:{ all -> 0x041a }
                    r3.remove(r5)     // Catch:{ all -> 0x041a }
                    monitor-exit(r2)     // Catch:{ all -> 0x041a }
                    if (r1 != 0) goto L_0x038d
                    int r1 = r0.getStatus()
                    org.apache.cordova.PluginResult$Status r2 = org.apache.cordova.PluginResult.Status.OK
                    int r2 = r2.ordinal()
                    if (r1 == r2) goto L_0x038d
                    if (r4 == 0) goto L_0x038d
                    goto L_0x038a
                L_0x0419:
                    return
                L_0x041a:
                    r0 = move-exception
                    monitor-exit(r2)     // Catch:{ all -> 0x041a }
                    throw r0
                L_0x041d:
                    r0 = move-exception
                    r6 = r1
                L_0x041f:
                    java.util.HashMap r1 = org.apache.cordova.filetransfer.FileTransfer.activeRequests
                    monitor-enter(r1)
                    java.util.HashMap r2 = org.apache.cordova.filetransfer.FileTransfer.activeRequests     // Catch:{ all -> 0x045a }
                    java.lang.String r5 = r10     // Catch:{ all -> 0x045a }
                    r2.remove(r5)     // Catch:{ all -> 0x045a }
                    monitor-exit(r1)     // Catch:{ all -> 0x045a }
                    if (r9 != 0) goto L_0x0441
                    org.apache.cordova.PluginResult r9 = new org.apache.cordova.PluginResult
                    org.apache.cordova.PluginResult$Status r1 = org.apache.cordova.PluginResult.Status.ERROR
                    int r2 = org.apache.cordova.filetransfer.FileTransfer.CONNECTION_ERR
                    java.lang.String r5 = r9
                    java.lang.String r8 = r4
                    org.json.JSONObject r2 = org.apache.cordova.filetransfer.FileTransfer.createFileTransferError(r2, r5, r8, r7, r3)
                    r9.<init>((org.apache.cordova.PluginResult.Status) r1, (org.json.JSONObject) r2)
                L_0x0441:
                    if (r6 != 0) goto L_0x0454
                    int r1 = r9.getStatus()
                    org.apache.cordova.PluginResult$Status r2 = org.apache.cordova.PluginResult.Status.OK
                    int r2 = r2.ordinal()
                    if (r1 == r2) goto L_0x0454
                    if (r4 == 0) goto L_0x0454
                    r4.delete()
                L_0x0454:
                    org.apache.cordova.filetransfer.FileTransfer$RequestContext r1 = r3
                    r1.sendPluginResult(r9)
                    throw r0
                L_0x045a:
                    r0 = move-exception
                    monitor-exit(r1)     // Catch:{ all -> 0x045a }
                    throw r0
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.apache.cordova.filetransfer.FileTransfer.C05732.run():void");
            }
        });
    }

    private void abort(String str) {
        final RequestContext remove;
        synchronized (activeRequests) {
            remove = activeRequests.remove(str);
        }
        if (remove != null) {
            this.f59cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    synchronized (remove) {
                        File file = remove.targetFile;
                        if (file != null) {
                            file.delete();
                        }
                        remove.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, FileTransfer.createFileTransferError(FileTransfer.ABORTED_ERR, remove.source, remove.target, (String) null, -1, (Throwable) null)));
                        remove.aborted = true;
                        if (remove.connection != null) {
                            try {
                                remove.connection.disconnect();
                            } catch (Exception e) {
                                LOG.m41e(FileTransfer.LOG_TAG, "CB-8431 Catch workaround for fatal exception", (Throwable) e);
                            }
                        }
                    }
                }
            });
        }
    }
}
