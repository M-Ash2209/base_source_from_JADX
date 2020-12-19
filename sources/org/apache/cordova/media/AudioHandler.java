package org.apache.cordova.media;

import android.media.AudioManager;
import android.net.Uri;
import android.support.p000v4.provider.FontsContractCompat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaResourceApi;
import org.apache.cordova.LOG;
import org.apache.cordova.PermissionHelper;
import org.apache.cordova.PluginResult;
import org.apache.cordova.media.AudioPlayer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AudioHandler extends CordovaPlugin {
    public static final int PERMISSION_DENIED_ERROR = 20;
    public static int RECORD_AUDIO = 0;
    public static String TAG = "AudioHandler";
    public static int WRITE_EXTERNAL_STORAGE = 1;
    public static String[] permissions = {"android.permission.RECORD_AUDIO", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private String fileUriStr;
    private AudioManager.OnAudioFocusChangeListener focusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int i) {
            if (i != 1) {
                switch (i) {
                    case FontsContractCompat.FontRequestCallback.FAIL_REASON_FONT_LOAD_ERROR:
                    case -2:
                    case -1:
                        AudioHandler.this.pauseAllLostFocus();
                        return;
                    default:
                        return;
                }
            } else {
                AudioHandler.this.resumeAllGainedFocus();
            }
        }
    };
    private CallbackContext messageChannel;
    private int origVolumeStream = -1;
    ArrayList<AudioPlayer> pausedForFocus = new ArrayList<>();
    ArrayList<AudioPlayer> pausedForPhone = new ArrayList<>();
    HashMap<String, AudioPlayer> players = new HashMap<>();
    private String recordId;

    /* access modifiers changed from: protected */
    public void getWritePermission(int i) {
        PermissionHelper.requestPermission(this, i, permissions[WRITE_EXTERNAL_STORAGE]);
    }

    /* access modifiers changed from: protected */
    public void getMicPermission(int i) {
        PermissionHelper.requestPermission(this, i, permissions[RECORD_AUDIO]);
    }

    public boolean execute(String str, JSONArray jSONArray, CallbackContext callbackContext) throws JSONException {
        CordovaResourceApi resourceApi = this.webView.getResourceApi();
        PluginResult.Status status = PluginResult.Status.OK;
        if (str.equals("startRecordingAudio")) {
            this.recordId = jSONArray.getString(0);
            String string = jSONArray.getString(1);
            try {
                this.fileUriStr = resourceApi.remapUri(Uri.parse(string)).toString();
            } catch (IllegalArgumentException unused) {
                this.fileUriStr = string;
            }
            promptForRecord();
        } else if (str.equals("stopRecordingAudio")) {
            stopRecordingAudio(jSONArray.getString(0), true);
        } else if (str.equals("pauseRecordingAudio")) {
            stopRecordingAudio(jSONArray.getString(0), false);
        } else if (str.equals("resumeRecordingAudio")) {
            resumeRecordingAudio(jSONArray.getString(0));
        } else if (str.equals("startPlayingAudio")) {
            String string2 = jSONArray.getString(1);
            try {
                string2 = resourceApi.remapUri(Uri.parse(string2)).toString();
            } catch (IllegalArgumentException unused2) {
            }
            startPlayingAudio(jSONArray.getString(0), FileHelper.stripFileProtocol(string2));
        } else if (str.equals("seekToAudio")) {
            seekToAudio(jSONArray.getString(0), jSONArray.getInt(1));
        } else if (str.equals("pausePlayingAudio")) {
            pausePlayingAudio(jSONArray.getString(0));
        } else if (str.equals("stopPlayingAudio")) {
            stopPlayingAudio(jSONArray.getString(0));
        } else if (str.equals("setVolume")) {
            try {
                setVolume(jSONArray.getString(0), Float.parseFloat(jSONArray.getString(1)));
            } catch (NumberFormatException unused3) {
            }
        } else if (str.equals("getCurrentPositionAudio")) {
            callbackContext.sendPluginResult(new PluginResult(status, getCurrentPositionAudio(jSONArray.getString(0))));
            return true;
        } else if (str.equals("getDurationAudio")) {
            callbackContext.sendPluginResult(new PluginResult(status, getDurationAudio(jSONArray.getString(0), jSONArray.getString(1))));
            return true;
        } else if (str.equals("create")) {
            getOrCreatePlayer(jSONArray.getString(0), FileHelper.stripFileProtocol(jSONArray.getString(1)));
        } else if (str.equals("release")) {
            callbackContext.sendPluginResult(new PluginResult(status, release(jSONArray.getString(0))));
            return true;
        } else if (str.equals("messageChannel")) {
            this.messageChannel = callbackContext;
            return true;
        } else if (!str.equals("getCurrentAmplitudeAudio")) {
            return false;
        } else {
            callbackContext.sendPluginResult(new PluginResult(status, getCurrentAmplitudeAudio(jSONArray.getString(0))));
            return true;
        }
        callbackContext.sendPluginResult(new PluginResult(status, ""));
        return true;
    }

    public void onDestroy() {
        if (!this.players.isEmpty()) {
            onLastPlayerReleased();
        }
        for (AudioPlayer destroy : this.players.values()) {
            destroy.destroy();
        }
        this.players.clear();
    }

    public void onReset() {
        onDestroy();
    }

    public Object onMessage(String str, Object obj) {
        if (str.equals("telephone")) {
            if ("ringing".equals(obj) || "offhook".equals(obj)) {
                for (AudioPlayer next : this.players.values()) {
                    if (next.getState() == AudioPlayer.STATE.MEDIA_RUNNING.ordinal()) {
                        this.pausedForPhone.add(next);
                        next.pausePlaying();
                    }
                }
            } else if ("idle".equals(obj)) {
                Iterator<AudioPlayer> it = this.pausedForPhone.iterator();
                while (it.hasNext()) {
                    it.next().startPlaying((String) null);
                }
                this.pausedForPhone.clear();
            }
        }
        return null;
    }

    private AudioPlayer getOrCreatePlayer(String str, String str2) {
        AudioPlayer audioPlayer = this.players.get(str);
        if (audioPlayer != null) {
            return audioPlayer;
        }
        if (this.players.isEmpty()) {
            onFirstPlayerCreated();
        }
        AudioPlayer audioPlayer2 = new AudioPlayer(this, str, str2);
        this.players.put(str, audioPlayer2);
        return audioPlayer2;
    }

    private boolean release(String str) {
        AudioPlayer remove = this.players.remove(str);
        if (remove == null) {
            return false;
        }
        if (this.players.isEmpty()) {
            onLastPlayerReleased();
        }
        remove.destroy();
        return true;
    }

    public void startRecordingAudio(String str, String str2) {
        getOrCreatePlayer(str, str2).startRecording(str2);
    }

    public void stopRecordingAudio(String str, boolean z) {
        AudioPlayer audioPlayer = this.players.get(str);
        if (audioPlayer != null) {
            audioPlayer.stopRecording(z);
        }
    }

    public void resumeRecordingAudio(String str) {
        AudioPlayer audioPlayer = this.players.get(str);
        if (audioPlayer != null) {
            audioPlayer.resumeRecording();
        }
    }

    public void startPlayingAudio(String str, String str2) {
        getOrCreatePlayer(str, str2).startPlaying(str2);
        getAudioFocus();
    }

    public void seekToAudio(String str, int i) {
        AudioPlayer audioPlayer = this.players.get(str);
        if (audioPlayer != null) {
            audioPlayer.seekToPlaying(i);
        }
    }

    public void pausePlayingAudio(String str) {
        AudioPlayer audioPlayer = this.players.get(str);
        if (audioPlayer != null) {
            audioPlayer.pausePlaying();
        }
    }

    public void stopPlayingAudio(String str) {
        AudioPlayer audioPlayer = this.players.get(str);
        if (audioPlayer != null) {
            audioPlayer.stopPlaying();
        }
    }

    public float getCurrentPositionAudio(String str) {
        AudioPlayer audioPlayer = this.players.get(str);
        if (audioPlayer != null) {
            return ((float) audioPlayer.getCurrentPosition()) / 1000.0f;
        }
        return -1.0f;
    }

    public float getDurationAudio(String str, String str2) {
        return getOrCreatePlayer(str, str2).getDuration(str2);
    }

    public void setAudioOutputDevice(int i) {
        AudioManager audioManager = (AudioManager) this.f59cordova.getActivity().getSystemService("audio");
        if (i == 2) {
            audioManager.setRouting(0, 2, -1);
        } else if (i == 1) {
            audioManager.setRouting(0, 1, -1);
        } else {
            LOG.m40e("AudioHandler.setAudioOutputDevice(): Error : ", " Unknown output device");
        }
    }

    public void pauseAllLostFocus() {
        for (AudioPlayer next : this.players.values()) {
            if (next.getState() == AudioPlayer.STATE.MEDIA_RUNNING.ordinal()) {
                this.pausedForFocus.add(next);
                next.pausePlaying();
            }
        }
    }

    public void resumeAllGainedFocus() {
        Iterator<AudioPlayer> it = this.pausedForFocus.iterator();
        while (it.hasNext()) {
            it.next().resumePlaying();
        }
        this.pausedForFocus.clear();
    }

    public void getAudioFocus() {
        int requestAudioFocus = ((AudioManager) this.f59cordova.getActivity().getSystemService("audio")).requestAudioFocus(this.focusChangeListener, 3, 1);
        if (requestAudioFocus != 1) {
            LOG.m40e("AudioHandler.getAudioFocus(): Error : ", requestAudioFocus + " instead of " + 1);
        }
    }

    public int getAudioOutputDevice() {
        AudioManager audioManager = (AudioManager) this.f59cordova.getActivity().getSystemService("audio");
        if (audioManager.getRouting(0) == 1) {
            return 1;
        }
        return audioManager.getRouting(0) == 2 ? 2 : -1;
    }

    public void setVolume(String str, float f) {
        AudioPlayer audioPlayer = this.players.get(str);
        if (audioPlayer != null) {
            audioPlayer.setVolume(f);
            return;
        }
        LOG.m40e("AudioHandler.setVolume(): Error : ", "Unknown Audio Player " + str);
    }

    private void onFirstPlayerCreated() {
        this.origVolumeStream = this.f59cordova.getActivity().getVolumeControlStream();
        this.f59cordova.getActivity().setVolumeControlStream(3);
    }

    private void onLastPlayerReleased() {
        if (this.origVolumeStream != -1) {
            this.f59cordova.getActivity().setVolumeControlStream(this.origVolumeStream);
            this.origVolumeStream = -1;
        }
    }

    /* access modifiers changed from: package-private */
    public void sendEventMessage(String str, JSONObject jSONObject) {
        JSONObject jSONObject2 = new JSONObject();
        try {
            jSONObject2.put("action", str);
            if (jSONObject != null) {
                jSONObject2.put(str, jSONObject);
            }
        } catch (JSONException e) {
            LOG.m41e(TAG, "Failed to create event message", (Throwable) e);
        }
        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jSONObject2);
        pluginResult.setKeepCallback(true);
        CallbackContext callbackContext = this.messageChannel;
        if (callbackContext != null) {
            callbackContext.sendPluginResult(pluginResult);
        }
    }

    public void onRequestPermissionResult(int i, String[] strArr, int[] iArr) throws JSONException {
        for (int i2 : iArr) {
            if (i2 == -1) {
                this.messageChannel.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, 20));
                return;
            }
        }
        promptForRecord();
    }

    private void promptForRecord() {
        if (PermissionHelper.hasPermission(this, permissions[WRITE_EXTERNAL_STORAGE]) && PermissionHelper.hasPermission(this, permissions[RECORD_AUDIO])) {
            startRecordingAudio(this.recordId, FileHelper.stripFileProtocol(this.fileUriStr));
        } else if (PermissionHelper.hasPermission(this, permissions[RECORD_AUDIO])) {
            getWritePermission(WRITE_EXTERNAL_STORAGE);
        } else {
            getMicPermission(RECORD_AUDIO);
        }
    }

    public float getCurrentAmplitudeAudio(String str) {
        AudioPlayer audioPlayer = this.players.get(str);
        if (audioPlayer != null) {
            return audioPlayer.getCurrentAmplitude();
        }
        return 0.0f;
    }
}
