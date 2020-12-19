package org.apache.cordova.mediacapture;

import android.os.Bundle;
import android.util.SparseArray;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PendingRequests {
    private static final String CURRENT_ID_KEY = "currentReqId";
    private static final String LOG_TAG = "PendingCaptureRequests";
    private static final String REQUEST_KEY_PREFIX = "request_";
    private int currentReqId = 0;
    private Bundle lastSavedState;
    private SparseArray<Request> requests = new SparseArray<>();
    private CallbackContext resumeContext;

    public synchronized Request createRequest(int i, JSONObject jSONObject, CallbackContext callbackContext) throws JSONException {
        Request request;
        request = new Request(i, jSONObject, callbackContext);
        this.requests.put(request.requestCode, request);
        return request;
    }

    public synchronized Request get(int i) {
        if (this.lastSavedState != null) {
            Bundle bundle = this.lastSavedState;
            if (bundle.containsKey(REQUEST_KEY_PREFIX + i)) {
                Bundle bundle2 = this.lastSavedState;
                Request request = new Request(bundle2.getBundle(REQUEST_KEY_PREFIX + i), this.resumeContext, i);
                this.requests.put(i, request);
                this.lastSavedState = null;
                this.resumeContext = null;
                return request;
            }
        }
        return this.requests.get(i);
    }

    public synchronized void resolveWithFailure(Request request, JSONObject jSONObject) {
        request.callbackContext.error(jSONObject);
        this.requests.remove(request.requestCode);
    }

    public synchronized void resolveWithSuccess(Request request) {
        request.callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, request.results));
        this.requests.remove(request.requestCode);
    }

    /* access modifiers changed from: private */
    public synchronized int incrementCurrentReqId() {
        int i;
        i = this.currentReqId;
        this.currentReqId = i + 1;
        return i;
    }

    public synchronized void setLastSavedState(Bundle bundle, CallbackContext callbackContext) {
        this.lastSavedState = bundle;
        this.resumeContext = callbackContext;
        this.currentReqId = bundle.getInt(CURRENT_ID_KEY);
    }

    public synchronized Bundle toBundle() {
        Bundle bundle;
        bundle = new Bundle();
        bundle.putInt(CURRENT_ID_KEY, this.currentReqId);
        for (int i = 0; i < this.requests.size(); i++) {
            int keyAt = this.requests.keyAt(i);
            bundle.putBundle(REQUEST_KEY_PREFIX + keyAt, this.requests.valueAt(i).toBundle());
        }
        if (this.requests.size() > 1) {
            LOG.m49w(LOG_TAG, "More than one media capture request pending on Activity destruction. Some requests will be dropped!");
        }
        return bundle;
    }

    public class Request {
        private static final String ACTION_KEY = "action";
        private static final String DURATION_KEY = "duration";
        private static final String LIMIT_KEY = "limit";
        private static final String QUALITY_KEY = "quality";
        private static final String RESULTS_KEY = "results";
        public int action;
        /* access modifiers changed from: private */
        public CallbackContext callbackContext;
        public int duration;
        public long limit;
        public int quality;
        public int requestCode;
        public JSONArray results;

        private Request(int i, JSONObject jSONObject, CallbackContext callbackContext2) throws JSONException {
            this.limit = 1;
            this.duration = 0;
            this.quality = 1;
            this.results = new JSONArray();
            this.callbackContext = callbackContext2;
            this.action = i;
            if (jSONObject != null) {
                this.limit = jSONObject.optLong(LIMIT_KEY, 1);
                this.duration = jSONObject.optInt(DURATION_KEY, 0);
                this.quality = jSONObject.optInt(QUALITY_KEY, 1);
            }
            this.requestCode = PendingRequests.this.incrementCurrentReqId();
        }

        private Request(Bundle bundle, CallbackContext callbackContext2, int i) {
            this.limit = 1;
            this.duration = 0;
            this.quality = 1;
            this.results = new JSONArray();
            this.callbackContext = callbackContext2;
            this.requestCode = i;
            this.action = bundle.getInt(ACTION_KEY);
            this.limit = bundle.getLong(LIMIT_KEY);
            this.duration = bundle.getInt(DURATION_KEY);
            this.quality = bundle.getInt(QUALITY_KEY);
            try {
                this.results = new JSONArray(bundle.getString(RESULTS_KEY));
            } catch (JSONException e) {
                LOG.m41e(PendingRequests.LOG_TAG, "Error parsing results for request from saved bundle", (Throwable) e);
            }
        }

        /* access modifiers changed from: private */
        public Bundle toBundle() {
            Bundle bundle = new Bundle();
            bundle.putInt(ACTION_KEY, this.action);
            bundle.putLong(LIMIT_KEY, this.limit);
            bundle.putInt(DURATION_KEY, this.duration);
            bundle.putInt(QUALITY_KEY, this.quality);
            bundle.putString(RESULTS_KEY, this.results.toString());
            return bundle;
        }
    }
}
