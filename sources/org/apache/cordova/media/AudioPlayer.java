package org.apache.cordova.media;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.p000v4.app.NotificationCompat;
import com.adobe.phonegap.push.PushConstants;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import org.apache.cordova.LOG;
import org.json.JSONException;
import org.json.JSONObject;

public class AudioPlayer implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    private static final String LOG_TAG = "AudioPlayer";
    private static int MEDIA_DURATION = 2;
    private static int MEDIA_ERROR = 9;
    private static int MEDIA_ERR_ABORTED = 1;
    private static int MEDIA_ERR_NONE_ACTIVE = 0;
    private static int MEDIA_POSITION = 3;
    private static int MEDIA_STATE = 1;
    private String audioFile = null;
    private float duration = -1.0f;
    private AudioHandler handler;

    /* renamed from: id */
    private String f57id;
    private MODE mode = MODE.NONE;
    private MediaPlayer player = null;
    private boolean prepareOnly = true;
    private MediaRecorder recorder = null;
    private int seekOnPrepared = 0;
    private STATE state = STATE.MEDIA_NONE;
    private String tempFile = null;
    private LinkedList<String> tempFiles = null;

    public enum MODE {
        NONE,
        PLAY,
        RECORD
    }

    public enum STATE {
        MEDIA_NONE,
        MEDIA_STARTING,
        MEDIA_RUNNING,
        MEDIA_PAUSED,
        MEDIA_STOPPED,
        MEDIA_LOADING
    }

    public AudioPlayer(AudioHandler audioHandler, String str, String str2) {
        this.handler = audioHandler;
        this.f57id = str;
        this.audioFile = str2;
        this.tempFiles = new LinkedList<>();
    }

    private String generateTempFile() {
        if (Environment.getExternalStorageState().equals("mounted")) {
            return Environment.getExternalStorageDirectory().getAbsolutePath() + "/tmprecording-" + System.currentTimeMillis() + ".3gp";
        }
        return "/data/data/" + this.handler.f59cordova.getActivity().getPackageName() + "/cache/tmprecording-" + System.currentTimeMillis() + ".3gp";
    }

    public void destroy() {
        if (this.player != null) {
            if (this.state == STATE.MEDIA_RUNNING || this.state == STATE.MEDIA_PAUSED) {
                this.player.stop();
                setState(STATE.MEDIA_STOPPED);
            }
            this.player.release();
            this.player = null;
        }
        if (this.recorder != null) {
            if (this.state != STATE.MEDIA_STOPPED) {
                stopRecording(true);
            }
            this.recorder.release();
            this.recorder = null;
        }
    }

    public void startRecording(String str) {
        switch (this.mode) {
            case PLAY:
                LOG.m37d(LOG_TAG, "AudioPlayer Error: Can't record in play mode.");
                sendErrorStatus(MEDIA_ERR_ABORTED);
                return;
            case NONE:
                this.audioFile = str;
                this.recorder = new MediaRecorder();
                this.recorder.setAudioSource(1);
                this.recorder.setOutputFormat(6);
                this.recorder.setAudioEncoder(3);
                this.tempFile = generateTempFile();
                this.recorder.setOutputFile(this.tempFile);
                try {
                    this.recorder.prepare();
                    this.recorder.start();
                    setState(STATE.MEDIA_RUNNING);
                    return;
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    sendErrorStatus(MEDIA_ERR_ABORTED);
                    return;
                } catch (IOException e2) {
                    e2.printStackTrace();
                    sendErrorStatus(MEDIA_ERR_ABORTED);
                    return;
                }
            case RECORD:
                LOG.m37d(LOG_TAG, "AudioPlayer Error: Already recording.");
                sendErrorStatus(MEDIA_ERR_ABORTED);
                return;
            default:
                return;
        }
    }

    /* JADX WARNING: type inference failed for: r3v1 */
    /* JADX WARNING: type inference failed for: r3v2, types: [java.io.FileOutputStream] */
    /* JADX WARNING: type inference failed for: r3v3 */
    /* JADX WARNING: type inference failed for: r3v6, types: [java.io.FileOutputStream] */
    /* JADX WARNING: type inference failed for: r3v7 */
    /* JADX WARNING: type inference failed for: r3v8, types: [java.io.FileInputStream] */
    /* JADX WARNING: type inference failed for: r3v10, types: [java.io.FileInputStream] */
    /* JADX WARNING: type inference failed for: r3v13 */
    /* JADX WARNING: type inference failed for: r3v14 */
    /* JADX WARNING: type inference failed for: r3v16 */
    /* JADX WARNING: Code restructure failed: missing block: B:100:?, code lost:
        org.apache.cordova.LOG.m41e(LOG_TAG, r5.getLocalizedMessage(), (java.lang.Throwable) r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:101:0x01fe, code lost:
        r5 = r6;
        r6 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:128:0x023d, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:131:?, code lost:
        org.apache.cordova.LOG.m41e(LOG_TAG, r0.getLocalizedMessage(), (java.lang.Throwable) r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:133:0x0248, code lost:
        r12 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:134:0x024a, code lost:
        r12 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:135:0x024b, code lost:
        r3 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:150:?, code lost:
        r4.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:151:0x026f, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:152:0x0270, code lost:
        org.apache.cordova.LOG.m41e(LOG_TAG, r0.getLocalizedMessage(), (java.lang.Throwable) r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x00e7, code lost:
        r1 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x00e8, code lost:
        r2 = LOG_TAG;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:?, code lost:
        r3 = r1.getLocalizedMessage();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x0111, code lost:
        r1 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x0112, code lost:
        r2 = LOG_TAG;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:?, code lost:
        r3 = r1.getLocalizedMessage();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x0146, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:?, code lost:
        org.apache.cordova.LOG.m41e(LOG_TAG, r2.getLocalizedMessage(), (java.lang.Throwable) r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x0151, code lost:
        r1 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:77:?, code lost:
        r12.close();
        org.apache.cordova.LOG.m37d(LOG_TAG, "OUTPUT FILE LENGTH: " + java.lang.String.valueOf(r0.length()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:78:0x01b8, code lost:
        r12 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:79:0x01b9, code lost:
        org.apache.cordova.LOG.m41e(LOG_TAG, r12.getLocalizedMessage(), (java.lang.Throwable) r12);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:97:0x01f4, code lost:
        r5 = move-exception;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:21:0x00e0, B:41:0x010a] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:21:0x00e0, B:51:0x013f] */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x021f A[SYNTHETIC, Splitter:B:117:0x021f] */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x0236 A[SYNTHETIC, Splitter:B:126:0x0236] */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x0248 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:94:0x01ec] */
    /* JADX WARNING: Removed duplicated region for block: B:149:0x026b A[SYNTHETIC, Splitter:B:149:0x026b] */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x0231 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:160:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x010a A[SYNTHETIC, Splitter:B:41:0x010a] */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x013f A[SYNTHETIC, Splitter:B:51:0x013f] */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x0151 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:21:0x00e0] */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x0163 A[SYNTHETIC, Splitter:B:69:0x0163] */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0196 A[SYNTHETIC, Splitter:B:76:0x0196] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void moveFile(java.lang.String r12) {
        /*
            r11 = this;
            java.lang.String r0 = "/"
            boolean r0 = r12.startsWith(r0)
            if (r0 != 0) goto L_0x0056
            java.lang.String r0 = android.os.Environment.getExternalStorageState()
            java.lang.String r1 = "mounted"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0031
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.io.File r1 = android.os.Environment.getExternalStorageDirectory()
            java.lang.String r1 = r1.getAbsolutePath()
            r0.append(r1)
            java.lang.String r1 = java.io.File.separator
            r0.append(r1)
            r0.append(r12)
            java.lang.String r12 = r0.toString()
            goto L_0x0056
        L_0x0031:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "/data/data/"
            r0.append(r1)
            org.apache.cordova.media.AudioHandler r1 = r11.handler
            org.apache.cordova.CordovaInterface r1 = r1.f59cordova
            android.app.Activity r1 = r1.getActivity()
            java.lang.String r1 = r1.getPackageName()
            r0.append(r1)
            java.lang.String r1 = "/cache/"
            r0.append(r1)
            r0.append(r12)
            java.lang.String r12 = r0.toString()
        L_0x0056:
            java.util.LinkedList<java.lang.String> r0 = r11.tempFiles
            int r0 = r0.size()
            java.lang.String r1 = "AudioPlayer"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "size = "
            r2.append(r3)
            r2.append(r0)
            java.lang.String r2 = r2.toString()
            org.apache.cordova.LOG.m37d(r1, r2)
            r1 = 0
            r2 = 1
            r3 = 0
            if (r0 != r2) goto L_0x01c3
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "renaming "
            r0.append(r2)
            java.lang.String r2 = r11.tempFile
            r0.append(r2)
            java.lang.String r2 = " to "
            r0.append(r2)
            r0.append(r12)
            java.lang.String r0 = r0.toString()
            java.lang.String r2 = "AudioPlayer"
            org.apache.cordova.LOG.m37d(r2, r0)
            java.io.File r0 = new java.io.File
            java.lang.String r2 = r11.tempFile
            r0.<init>(r2)
            java.io.File r2 = new java.io.File
            r2.<init>(r12)
            boolean r0 = r0.renameTo(r2)
            if (r0 != 0) goto L_0x0268
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x015c, all -> 0x0158 }
            r0.<init>(r12)     // Catch:{ Exception -> 0x015c, all -> 0x0158 }
            java.io.FileOutputStream r12 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x0156 }
            r12.<init>(r0)     // Catch:{ Exception -> 0x0156 }
            java.io.File r2 = new java.io.File     // Catch:{ Exception -> 0x00fd, all -> 0x00fa }
            java.lang.String r4 = r11.tempFile     // Catch:{ Exception -> 0x00fd, all -> 0x00fa }
            r2.<init>(r4)     // Catch:{ Exception -> 0x00fd, all -> 0x00fa }
            java.lang.String r4 = "AudioPlayer"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00f8 }
            r5.<init>()     // Catch:{ Exception -> 0x00f8 }
            java.lang.String r6 = "INPUT FILE LENGTH: "
            r5.append(r6)     // Catch:{ Exception -> 0x00f8 }
            long r6 = r2.length()     // Catch:{ Exception -> 0x00f8 }
            java.lang.String r6 = java.lang.String.valueOf(r6)     // Catch:{ Exception -> 0x00f8 }
            r5.append(r6)     // Catch:{ Exception -> 0x00f8 }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x00f8 }
            org.apache.cordova.LOG.m37d(r4, r5)     // Catch:{ Exception -> 0x00f8 }
            java.io.FileInputStream r4 = new java.io.FileInputStream     // Catch:{ Exception -> 0x00f8 }
            r4.<init>(r2)     // Catch:{ Exception -> 0x00f8 }
            copy(r4, r12, r1)     // Catch:{ Exception -> 0x00f5, all -> 0x00f2 }
            r4.close()     // Catch:{ Exception -> 0x00e7, all -> 0x0151 }
            r2.delete()     // Catch:{ Exception -> 0x00e7, all -> 0x0151 }
            goto L_0x0119
        L_0x00e7:
            r1 = move-exception
            java.lang.String r2 = "AudioPlayer"
            java.lang.String r3 = r1.getLocalizedMessage()     // Catch:{ Exception -> 0x0153, all -> 0x0151 }
        L_0x00ee:
            org.apache.cordova.LOG.m41e((java.lang.String) r2, (java.lang.String) r3, (java.lang.Throwable) r1)     // Catch:{ Exception -> 0x0153, all -> 0x0151 }
            goto L_0x0119
        L_0x00f2:
            r1 = move-exception
            r3 = r4
            goto L_0x013d
        L_0x00f5:
            r1 = move-exception
            r3 = r4
            goto L_0x00ff
        L_0x00f8:
            r1 = move-exception
            goto L_0x00ff
        L_0x00fa:
            r1 = move-exception
            r2 = r3
            goto L_0x013d
        L_0x00fd:
            r1 = move-exception
            r2 = r3
        L_0x00ff:
            java.lang.String r4 = "AudioPlayer"
            java.lang.String r5 = r1.getLocalizedMessage()     // Catch:{ all -> 0x013c }
            org.apache.cordova.LOG.m41e((java.lang.String) r4, (java.lang.String) r5, (java.lang.Throwable) r1)     // Catch:{ all -> 0x013c }
            if (r3 == 0) goto L_0x0119
            r3.close()     // Catch:{ Exception -> 0x0111, all -> 0x0151 }
            r2.delete()     // Catch:{ Exception -> 0x0111, all -> 0x0151 }
            goto L_0x0119
        L_0x0111:
            r1 = move-exception
            java.lang.String r2 = "AudioPlayer"
            java.lang.String r3 = r1.getLocalizedMessage()     // Catch:{ Exception -> 0x0153, all -> 0x0151 }
            goto L_0x00ee
        L_0x0119:
            r12.close()     // Catch:{ Exception -> 0x0186 }
            java.lang.String r12 = "AudioPlayer"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0186 }
            r1.<init>()     // Catch:{ Exception -> 0x0186 }
            java.lang.String r2 = "OUTPUT FILE LENGTH: "
            r1.append(r2)     // Catch:{ Exception -> 0x0186 }
            long r2 = r0.length()     // Catch:{ Exception -> 0x0186 }
            java.lang.String r0 = java.lang.String.valueOf(r2)     // Catch:{ Exception -> 0x0186 }
            r1.append(r0)     // Catch:{ Exception -> 0x0186 }
            java.lang.String r0 = r1.toString()     // Catch:{ Exception -> 0x0186 }
            org.apache.cordova.LOG.m37d(r12, r0)     // Catch:{ Exception -> 0x0186 }
            goto L_0x0268
        L_0x013c:
            r1 = move-exception
        L_0x013d:
            if (r3 == 0) goto L_0x0150
            r3.close()     // Catch:{ Exception -> 0x0146, all -> 0x0151 }
            r2.delete()     // Catch:{ Exception -> 0x0146, all -> 0x0151 }
            goto L_0x0150
        L_0x0146:
            r2 = move-exception
            java.lang.String r3 = "AudioPlayer"
            java.lang.String r4 = r2.getLocalizedMessage()     // Catch:{ Exception -> 0x0153, all -> 0x0151 }
            org.apache.cordova.LOG.m41e((java.lang.String) r3, (java.lang.String) r4, (java.lang.Throwable) r2)     // Catch:{ Exception -> 0x0153, all -> 0x0151 }
        L_0x0150:
            throw r1     // Catch:{ Exception -> 0x0153, all -> 0x0151 }
        L_0x0151:
            r1 = move-exception
            goto L_0x0194
        L_0x0153:
            r1 = move-exception
            r3 = r12
            goto L_0x015e
        L_0x0156:
            r1 = move-exception
            goto L_0x015e
        L_0x0158:
            r1 = move-exception
            r12 = r3
            r0 = r12
            goto L_0x0194
        L_0x015c:
            r1 = move-exception
            r0 = r3
        L_0x015e:
            r1.printStackTrace()     // Catch:{ all -> 0x0192 }
            if (r3 == 0) goto L_0x0268
            r3.close()     // Catch:{ Exception -> 0x0186 }
            java.lang.String r12 = "AudioPlayer"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0186 }
            r1.<init>()     // Catch:{ Exception -> 0x0186 }
            java.lang.String r2 = "OUTPUT FILE LENGTH: "
            r1.append(r2)     // Catch:{ Exception -> 0x0186 }
            long r2 = r0.length()     // Catch:{ Exception -> 0x0186 }
            java.lang.String r0 = java.lang.String.valueOf(r2)     // Catch:{ Exception -> 0x0186 }
            r1.append(r0)     // Catch:{ Exception -> 0x0186 }
            java.lang.String r0 = r1.toString()     // Catch:{ Exception -> 0x0186 }
            org.apache.cordova.LOG.m37d(r12, r0)     // Catch:{ Exception -> 0x0186 }
            goto L_0x0268
        L_0x0186:
            r12 = move-exception
            java.lang.String r0 = "AudioPlayer"
            java.lang.String r1 = r12.getLocalizedMessage()
            org.apache.cordova.LOG.m41e((java.lang.String) r0, (java.lang.String) r1, (java.lang.Throwable) r12)
            goto L_0x0268
        L_0x0192:
            r1 = move-exception
            r12 = r3
        L_0x0194:
            if (r12 == 0) goto L_0x01c2
            r12.close()     // Catch:{ Exception -> 0x01b8 }
            java.lang.String r12 = "AudioPlayer"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01b8 }
            r2.<init>()     // Catch:{ Exception -> 0x01b8 }
            java.lang.String r3 = "OUTPUT FILE LENGTH: "
            r2.append(r3)     // Catch:{ Exception -> 0x01b8 }
            long r3 = r0.length()     // Catch:{ Exception -> 0x01b8 }
            java.lang.String r0 = java.lang.String.valueOf(r3)     // Catch:{ Exception -> 0x01b8 }
            r2.append(r0)     // Catch:{ Exception -> 0x01b8 }
            java.lang.String r0 = r2.toString()     // Catch:{ Exception -> 0x01b8 }
            org.apache.cordova.LOG.m37d(r12, r0)     // Catch:{ Exception -> 0x01b8 }
            goto L_0x01c2
        L_0x01b8:
            r12 = move-exception
            java.lang.String r0 = r12.getLocalizedMessage()
            java.lang.String r2 = "AudioPlayer"
            org.apache.cordova.LOG.m41e((java.lang.String) r2, (java.lang.String) r0, (java.lang.Throwable) r12)
        L_0x01c2:
            throw r1
        L_0x01c3:
            java.io.FileOutputStream r4 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x0254 }
            java.io.File r5 = new java.io.File     // Catch:{ Exception -> 0x0254 }
            r5.<init>(r12)     // Catch:{ Exception -> 0x0254 }
            r4.<init>(r5)     // Catch:{ Exception -> 0x0254 }
            r5 = r3
            r6 = r5
            r12 = 0
        L_0x01d0:
            if (r12 >= r0) goto L_0x024d
            java.io.File r7 = new java.io.File     // Catch:{ Exception -> 0x0213 }
            java.util.LinkedList<java.lang.String> r8 = r11.tempFiles     // Catch:{ Exception -> 0x0213 }
            java.lang.Object r8 = r8.get(r12)     // Catch:{ Exception -> 0x0213 }
            java.lang.String r8 = (java.lang.String) r8     // Catch:{ Exception -> 0x0213 }
            r7.<init>(r8)     // Catch:{ Exception -> 0x0213 }
            java.io.FileInputStream r6 = new java.io.FileInputStream     // Catch:{ Exception -> 0x020c, all -> 0x0209 }
            r6.<init>(r7)     // Catch:{ Exception -> 0x020c, all -> 0x0209 }
            if (r12 <= 0) goto L_0x01e8
            r5 = 1
            goto L_0x01e9
        L_0x01e8:
            r5 = 0
        L_0x01e9:
            copy(r6, r4, r5)     // Catch:{ Exception -> 0x0204, all -> 0x0201 }
            r6.close()     // Catch:{ Exception -> 0x01f4, all -> 0x0248 }
            r7.delete()     // Catch:{ Exception -> 0x01f4, all -> 0x0248 }
            r5 = r6
            goto L_0x0225
        L_0x01f4:
            r5 = move-exception
            java.lang.String r8 = "AudioPlayer"
            java.lang.String r9 = r5.getLocalizedMessage()     // Catch:{ Exception -> 0x024a, all -> 0x0248 }
            org.apache.cordova.LOG.m41e((java.lang.String) r8, (java.lang.String) r9, (java.lang.Throwable) r5)     // Catch:{ Exception -> 0x024a, all -> 0x0248 }
            r5 = r6
            r6 = r7
            goto L_0x0231
        L_0x0201:
            r12 = move-exception
            r5 = r6
            goto L_0x020a
        L_0x0204:
            r5 = move-exception
            r10 = r7
            r7 = r5
            r5 = r6
            goto L_0x020f
        L_0x0209:
            r12 = move-exception
        L_0x020a:
            r6 = r7
            goto L_0x0234
        L_0x020c:
            r6 = move-exception
            r10 = r7
            r7 = r6
        L_0x020f:
            r6 = r10
            goto L_0x0214
        L_0x0211:
            r12 = move-exception
            goto L_0x0234
        L_0x0213:
            r7 = move-exception
        L_0x0214:
            java.lang.String r8 = "AudioPlayer"
            java.lang.String r9 = r7.getLocalizedMessage()     // Catch:{ all -> 0x0211 }
            org.apache.cordova.LOG.m41e((java.lang.String) r8, (java.lang.String) r9, (java.lang.Throwable) r7)     // Catch:{ all -> 0x0211 }
            if (r5 == 0) goto L_0x0231
            r5.close()     // Catch:{ Exception -> 0x0227, all -> 0x0248 }
            r6.delete()     // Catch:{ Exception -> 0x0227, all -> 0x0248 }
        L_0x0225:
            r6 = r3
            goto L_0x0231
        L_0x0227:
            r7 = move-exception
            java.lang.String r8 = "AudioPlayer"
            java.lang.String r9 = r7.getLocalizedMessage()     // Catch:{ Exception -> 0x024a, all -> 0x0248 }
            org.apache.cordova.LOG.m41e((java.lang.String) r8, (java.lang.String) r9, (java.lang.Throwable) r7)     // Catch:{ Exception -> 0x024a, all -> 0x0248 }
        L_0x0231:
            int r12 = r12 + 1
            goto L_0x01d0
        L_0x0234:
            if (r5 == 0) goto L_0x0247
            r5.close()     // Catch:{ Exception -> 0x023d, all -> 0x0248 }
            r6.delete()     // Catch:{ Exception -> 0x023d, all -> 0x0248 }
            goto L_0x0247
        L_0x023d:
            r0 = move-exception
            java.lang.String r1 = "AudioPlayer"
            java.lang.String r2 = r0.getLocalizedMessage()     // Catch:{ Exception -> 0x024a, all -> 0x0248 }
            org.apache.cordova.LOG.m41e((java.lang.String) r1, (java.lang.String) r2, (java.lang.Throwable) r0)     // Catch:{ Exception -> 0x024a, all -> 0x0248 }
        L_0x0247:
            throw r12     // Catch:{ Exception -> 0x024a, all -> 0x0248 }
        L_0x0248:
            r12 = move-exception
            goto L_0x0269
        L_0x024a:
            r12 = move-exception
            r3 = r4
            goto L_0x0255
        L_0x024d:
            r4.close()     // Catch:{ Exception -> 0x025e }
            goto L_0x0268
        L_0x0251:
            r12 = move-exception
            r4 = r3
            goto L_0x0269
        L_0x0254:
            r12 = move-exception
        L_0x0255:
            r12.printStackTrace()     // Catch:{ all -> 0x0251 }
            if (r3 == 0) goto L_0x0268
            r3.close()     // Catch:{ Exception -> 0x025e }
            goto L_0x0268
        L_0x025e:
            r12 = move-exception
            java.lang.String r0 = "AudioPlayer"
            java.lang.String r1 = r12.getLocalizedMessage()
            org.apache.cordova.LOG.m41e((java.lang.String) r0, (java.lang.String) r1, (java.lang.Throwable) r12)
        L_0x0268:
            return
        L_0x0269:
            if (r4 == 0) goto L_0x0279
            r4.close()     // Catch:{ Exception -> 0x026f }
            goto L_0x0279
        L_0x026f:
            r0 = move-exception
            java.lang.String r1 = r0.getLocalizedMessage()
            java.lang.String r2 = "AudioPlayer"
            org.apache.cordova.LOG.m41e((java.lang.String) r2, (java.lang.String) r1, (java.lang.Throwable) r0)
        L_0x0279:
            throw r12
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.cordova.media.AudioPlayer.moveFile(java.lang.String):void");
    }

    private static long copy(InputStream inputStream, OutputStream outputStream, boolean z) throws IOException {
        byte[] bArr = new byte[8096];
        if (z) {
            inputStream.skip(6);
        }
        long j = 0;
        while (true) {
            int read = inputStream.read(bArr);
            if (read == -1) {
                return j;
            }
            outputStream.write(bArr, 0, read);
            j += (long) read;
        }
    }

    public void stopRecording(boolean z) {
        if (this.recorder != null) {
            try {
                if (this.state == STATE.MEDIA_RUNNING) {
                    this.recorder.stop();
                }
                this.recorder.reset();
                if (!this.tempFiles.contains(this.tempFile)) {
                    this.tempFiles.add(this.tempFile);
                }
                if (z) {
                    LOG.m37d(LOG_TAG, "stopping recording");
                    setState(STATE.MEDIA_STOPPED);
                    moveFile(this.audioFile);
                    return;
                }
                LOG.m37d(LOG_TAG, "pause recording");
                setState(STATE.MEDIA_PAUSED);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void resumeRecording() {
        startRecording(this.audioFile);
    }

    public void startPlaying(String str) {
        MediaPlayer mediaPlayer;
        if (!readyPlayer(str) || (mediaPlayer = this.player) == null) {
            this.prepareOnly = false;
            return;
        }
        mediaPlayer.start();
        setState(STATE.MEDIA_RUNNING);
        this.seekOnPrepared = 0;
    }

    public void seekToPlaying(int i) {
        if (readyPlayer(this.audioFile)) {
            if (i > 0) {
                this.player.seekTo(i);
            }
            LOG.m37d(LOG_TAG, "Send a onStatus update for the new seek");
            sendStatusChange(MEDIA_POSITION, (Integer) null, Float.valueOf(((float) i) / 1000.0f));
            return;
        }
        this.seekOnPrepared = i;
    }

    public void pausePlaying() {
        MediaPlayer mediaPlayer;
        if (this.state != STATE.MEDIA_RUNNING || (mediaPlayer = this.player) == null) {
            LOG.m37d(LOG_TAG, "AudioPlayer Error: pausePlaying() called during invalid state: " + this.state.ordinal());
            sendErrorStatus(MEDIA_ERR_NONE_ACTIVE);
            return;
        }
        mediaPlayer.pause();
        setState(STATE.MEDIA_PAUSED);
    }

    public void stopPlaying() {
        if (this.state == STATE.MEDIA_RUNNING || this.state == STATE.MEDIA_PAUSED) {
            this.player.pause();
            this.player.seekTo(0);
            LOG.m37d(LOG_TAG, "stopPlaying is calling stopped");
            setState(STATE.MEDIA_STOPPED);
            return;
        }
        LOG.m37d(LOG_TAG, "AudioPlayer Error: stopPlaying() called during invalid state: " + this.state.ordinal());
        sendErrorStatus(MEDIA_ERR_NONE_ACTIVE);
    }

    public void resumePlaying() {
        startPlaying(this.audioFile);
    }

    public void onCompletion(MediaPlayer mediaPlayer) {
        LOG.m37d(LOG_TAG, "on completion is calling stopped");
        setState(STATE.MEDIA_STOPPED);
    }

    public long getCurrentPosition() {
        if (this.state != STATE.MEDIA_RUNNING && this.state != STATE.MEDIA_PAUSED) {
            return -1;
        }
        int currentPosition = this.player.getCurrentPosition();
        sendStatusChange(MEDIA_POSITION, (Integer) null, Float.valueOf(((float) currentPosition) / 1000.0f));
        return (long) currentPosition;
    }

    public boolean isStreaming(String str) {
        return str.contains("http://") || str.contains("https://") || str.contains("rtsp://");
    }

    public float getDuration(String str) {
        if (this.recorder != null) {
            return -2.0f;
        }
        if (this.player != null) {
            return this.duration;
        }
        this.prepareOnly = true;
        startPlaying(str);
        return this.duration;
    }

    public void onPrepared(MediaPlayer mediaPlayer) {
        this.player.setOnCompletionListener(this);
        seekToPlaying(this.seekOnPrepared);
        if (!this.prepareOnly) {
            this.player.start();
            setState(STATE.MEDIA_RUNNING);
            this.seekOnPrepared = 0;
        } else {
            setState(STATE.MEDIA_STARTING);
        }
        this.duration = getDurationInSeconds();
        this.prepareOnly = true;
        sendStatusChange(MEDIA_DURATION, (Integer) null, Float.valueOf(this.duration));
    }

    private float getDurationInSeconds() {
        return ((float) this.player.getDuration()) / 1000.0f;
    }

    public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
        LOG.m37d(LOG_TAG, "AudioPlayer.onError(" + i + ", " + i2 + ")");
        this.state = STATE.MEDIA_STOPPED;
        destroy();
        sendErrorStatus(i);
        return false;
    }

    private void setState(STATE state2) {
        if (this.state != state2) {
            sendStatusChange(MEDIA_STATE, (Integer) null, Float.valueOf((float) state2.ordinal()));
        }
        this.state = state2;
    }

    private void setMode(MODE mode2) {
        MODE mode3 = this.mode;
        this.mode = mode2;
    }

    public int getState() {
        return this.state.ordinal();
    }

    public void setVolume(float f) {
        MediaPlayer mediaPlayer = this.player;
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(f, f);
            return;
        }
        LOG.m37d(LOG_TAG, "AudioPlayer Error: Cannot set volume until the audio file is initialized.");
        sendErrorStatus(MEDIA_ERR_NONE_ACTIVE);
    }

    private boolean playMode() {
        switch (this.mode) {
            case NONE:
                setMode(MODE.PLAY);
                return true;
            case RECORD:
                LOG.m37d(LOG_TAG, "AudioPlayer Error: Can't play in record mode.");
                sendErrorStatus(MEDIA_ERR_ABORTED);
                return false;
            default:
                return true;
        }
    }

    private boolean readyPlayer(String str) {
        if (playMode()) {
            switch (this.state) {
                case MEDIA_NONE:
                    if (this.player == null) {
                        this.player = new MediaPlayer();
                        this.player.setOnErrorListener(this);
                    }
                    try {
                        loadAudioFile(str);
                    } catch (Exception unused) {
                        sendErrorStatus(MEDIA_ERR_ABORTED);
                    }
                    return false;
                case MEDIA_LOADING:
                    LOG.m37d(LOG_TAG, "AudioPlayer Loading: startPlaying() called during media preparation: " + STATE.MEDIA_STARTING.ordinal());
                    this.prepareOnly = false;
                    return false;
                case MEDIA_STARTING:
                case MEDIA_RUNNING:
                case MEDIA_PAUSED:
                    return true;
                case MEDIA_STOPPED:
                    if (str == null || this.audioFile.compareTo(str) != 0) {
                        this.player.reset();
                        try {
                            loadAudioFile(str);
                        } catch (Exception unused2) {
                            sendErrorStatus(MEDIA_ERR_ABORTED);
                        }
                        return false;
                    }
                    MediaPlayer mediaPlayer = this.player;
                    if (mediaPlayer == null) {
                        this.player = new MediaPlayer();
                        this.player.setOnErrorListener(this);
                        this.prepareOnly = false;
                        try {
                            loadAudioFile(str);
                        } catch (Exception unused3) {
                            sendErrorStatus(MEDIA_ERR_ABORTED);
                        }
                        return false;
                    }
                    mediaPlayer.seekTo(0);
                    this.player.pause();
                    return true;
                default:
                    LOG.m37d(LOG_TAG, "AudioPlayer Error: startPlaying() called during invalid state: " + this.state);
                    sendErrorStatus(MEDIA_ERR_ABORTED);
                    break;
            }
        }
        return false;
    }

    private void loadAudioFile(String str) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException {
        if (isStreaming(str)) {
            this.player.setDataSource(str);
            this.player.setAudioStreamType(3);
            setMode(MODE.PLAY);
            setState(STATE.MEDIA_STARTING);
            this.player.setOnPreparedListener(this);
            this.player.prepareAsync();
            return;
        }
        if (str.startsWith("/android_asset/")) {
            AssetFileDescriptor openFd = this.handler.f59cordova.getActivity().getAssets().openFd(str.substring(15));
            this.player.setDataSource(openFd.getFileDescriptor(), openFd.getStartOffset(), openFd.getLength());
        } else if (new File(str).exists()) {
            FileInputStream fileInputStream = new FileInputStream(str);
            this.player.setDataSource(fileInputStream.getFD());
            fileInputStream.close();
        } else {
            MediaPlayer mediaPlayer = this.player;
            mediaPlayer.setDataSource(Environment.getExternalStorageDirectory().getPath() + "/" + str);
        }
        setState(STATE.MEDIA_STARTING);
        this.player.setOnPreparedListener(this);
        this.player.prepare();
        this.duration = getDurationInSeconds();
    }

    private void sendErrorStatus(int i) {
        sendStatusChange(MEDIA_ERROR, Integer.valueOf(i), (Float) null);
    }

    private void sendStatusChange(int i, Integer num, Float f) {
        if (num == null || f == null) {
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put(PushConstants.CHANNEL_ID, this.f57id);
                jSONObject.put("msgType", i);
                if (num != null) {
                    JSONObject jSONObject2 = new JSONObject();
                    jSONObject2.put("code", num.intValue());
                    jSONObject.put("value", jSONObject2);
                } else if (f != null) {
                    jSONObject.put("value", (double) f.floatValue());
                }
            } catch (JSONException e) {
                LOG.m41e(LOG_TAG, "Failed to create status details", (Throwable) e);
            }
            this.handler.sendEventMessage(NotificationCompat.CATEGORY_STATUS, jSONObject);
            return;
        }
        throw new IllegalArgumentException("Only one of additionalCode or value can be specified, not both");
    }

    public float getCurrentAmplitude() {
        if (this.recorder == null) {
            return 0.0f;
        }
        try {
            if (this.state == STATE.MEDIA_RUNNING) {
                return ((float) this.recorder.getMaxAmplitude()) / 32762.0f;
            }
            return 0.0f;
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0f;
        }
    }
}
