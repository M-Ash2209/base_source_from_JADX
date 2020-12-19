package com.silkimen.cordovahttp;

import android.util.Base64;
import android.util.Log;
import com.adobe.phonegap.push.PushConstants;
import com.silkimen.http.HttpBodyDecoder;
import com.silkimen.http.HttpRequest;
import com.silkimen.http.JsonUtils;
import com.silkimen.http.OkConnectionFactory;
import com.silkimen.http.TLSConfiguration;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Map;
import javax.net.ssl.SSLException;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.networkinformation.NetworkManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

abstract class CordovaHttpBase implements Runnable {
    protected static final String TAG = "Cordova-Plugin-HTTP";
    protected CallbackContext callbackContext;
    protected Object data;
    protected boolean followRedirects;
    protected JSONObject headers;
    protected String method;
    protected String responseType;
    protected String serializer = NetworkManager.TYPE_NONE;
    protected int timeout;
    protected TLSConfiguration tlsConfiguration;
    protected String url;

    public CordovaHttpBase(String str, String str2, String str3, Object obj, JSONObject jSONObject, int i, boolean z, String str4, TLSConfiguration tLSConfiguration, CallbackContext callbackContext2) {
        this.method = str;
        this.url = str2;
        this.serializer = str3;
        this.data = obj;
        this.headers = jSONObject;
        this.timeout = i;
        this.followRedirects = z;
        this.responseType = str4;
        this.tlsConfiguration = tLSConfiguration;
        this.callbackContext = callbackContext2;
    }

    public CordovaHttpBase(String str, String str2, JSONObject jSONObject, int i, boolean z, String str3, TLSConfiguration tLSConfiguration, CallbackContext callbackContext2) {
        this.method = str;
        this.url = str2;
        this.headers = jSONObject;
        this.timeout = i;
        this.followRedirects = z;
        this.responseType = str3;
        this.tlsConfiguration = tLSConfiguration;
        this.callbackContext = callbackContext2;
    }

    public void run() {
        CordovaHttpResponse cordovaHttpResponse = new CordovaHttpResponse();
        try {
            HttpRequest createRequest = createRequest();
            prepareRequest(createRequest);
            sendBody(createRequest);
            processResponse(createRequest, cordovaHttpResponse);
        } catch (HttpRequest.HttpRequestException e) {
            if (e.getCause() instanceof SSLException) {
                cordovaHttpResponse.setStatus(-2);
                cordovaHttpResponse.setErrorMessage("TLS connection could not be established: " + e.getMessage());
                Log.w(TAG, "TLS connection could not be established", e);
            } else if (e.getCause() instanceof UnknownHostException) {
                cordovaHttpResponse.setStatus(-3);
                cordovaHttpResponse.setErrorMessage("Host could not be resolved: " + e.getMessage());
                Log.w(TAG, "Host could not be resolved", e);
            } else if (e.getCause() instanceof SocketTimeoutException) {
                cordovaHttpResponse.setStatus(-4);
                cordovaHttpResponse.setErrorMessage("Request timed out: " + e.getMessage());
                Log.w(TAG, "Request timed out", e);
            } else {
                cordovaHttpResponse.setStatus(-1);
                cordovaHttpResponse.setErrorMessage("There was an error with the request: " + e.getCause().getMessage());
                Log.w(TAG, "Generic request error", e);
            }
        } catch (Exception e2) {
            cordovaHttpResponse.setStatus(-1);
            cordovaHttpResponse.setErrorMessage(e2.getMessage());
            Log.e(TAG, "An unexpected error occured", e2);
        }
        try {
            if (cordovaHttpResponse.hasFailed()) {
                this.callbackContext.error(cordovaHttpResponse.toJSON());
            } else {
                this.callbackContext.success(cordovaHttpResponse.toJSON());
            }
        } catch (JSONException e3) {
            Log.e(TAG, "An unexpected error occured while creating HTTP response object", e3);
        }
    }

    /* access modifiers changed from: protected */
    public HttpRequest createRequest() throws JSONException {
        return new HttpRequest((CharSequence) this.url, this.method);
    }

    /* access modifiers changed from: protected */
    public void prepareRequest(HttpRequest httpRequest) throws JSONException, IOException {
        httpRequest.followRedirects(this.followRedirects);
        httpRequest.readTimeout(this.timeout);
        httpRequest.acceptCharset(HttpRequest.CHARSET_UTF8);
        httpRequest.uncompress(true);
        HttpRequest.setConnectionFactory(new OkConnectionFactory());
        if (this.tlsConfiguration.getHostnameVerifier() != null) {
            httpRequest.setHostnameVerifier(this.tlsConfiguration.getHostnameVerifier());
        }
        httpRequest.setSSLSocketFactory(this.tlsConfiguration.getTLSSocketFactory());
        setContentType(httpRequest);
        httpRequest.headers((Map<String, String>) JsonUtils.getStringMap(this.headers));
    }

    /* access modifiers changed from: protected */
    public void setContentType(HttpRequest httpRequest) {
        if ("json".equals(this.serializer)) {
            httpRequest.contentType(HttpRequest.CONTENT_TYPE_JSON, HttpRequest.CHARSET_UTF8);
        } else if ("utf8".equals(this.serializer)) {
            httpRequest.contentType("text/plain", HttpRequest.CHARSET_UTF8);
        } else if ("raw".equals(this.serializer)) {
            httpRequest.contentType("application/octet-stream");
        } else if (!"urlencoded".equals(this.serializer) && "multipart".equals(this.serializer)) {
            httpRequest.contentType("multipart/form-data");
        }
    }

    /* access modifiers changed from: protected */
    public void sendBody(HttpRequest httpRequest) throws Exception {
        if (this.data != null) {
            if ("json".equals(this.serializer)) {
                httpRequest.send((CharSequence) this.data.toString());
            } else if ("utf8".equals(this.serializer)) {
                httpRequest.send((CharSequence) ((JSONObject) this.data).getString(PushConstants.STYLE_TEXT));
            } else if ("raw".equals(this.serializer)) {
                httpRequest.send(Base64.decode((String) this.data, 0));
            } else if ("urlencoded".equals(this.serializer)) {
                httpRequest.form((Map<?, ?>) JsonUtils.getObjectMap((JSONObject) this.data));
            } else if ("multipart".equals(this.serializer)) {
                JSONArray jSONArray = ((JSONObject) this.data).getJSONArray("buffers");
                JSONArray jSONArray2 = ((JSONObject) this.data).getJSONArray("names");
                JSONArray jSONArray3 = ((JSONObject) this.data).getJSONArray("fileNames");
                JSONArray jSONArray4 = ((JSONObject) this.data).getJSONArray("types");
                for (int i = 0; i < jSONArray.length(); i++) {
                    byte[] decode = Base64.decode(jSONArray.getString(i), 0);
                    String string = jSONArray2.getString(i);
                    if (jSONArray3.isNull(i)) {
                        httpRequest.part(string, new String(decode, HttpRequest.CHARSET_UTF8));
                    } else {
                        httpRequest.part(string, jSONArray3.getString(i), jSONArray4.getString(i), (InputStream) new ByteArrayInputStream(decode));
                    }
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void processResponse(HttpRequest httpRequest, CordovaHttpResponse cordovaHttpResponse) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        httpRequest.receive((OutputStream) byteArrayOutputStream);
        cordovaHttpResponse.setStatus(httpRequest.code());
        cordovaHttpResponse.setUrl(httpRequest.url().toString());
        cordovaHttpResponse.setHeaders(httpRequest.headers());
        if (httpRequest.code() < 200 || httpRequest.code() >= 300) {
            cordovaHttpResponse.setErrorMessage(HttpBodyDecoder.decodeBody(byteArrayOutputStream.toByteArray(), httpRequest.charset()));
        } else if (PushConstants.STYLE_TEXT.equals(this.responseType) || "json".equals(this.responseType)) {
            cordovaHttpResponse.setBody(HttpBodyDecoder.decodeBody(byteArrayOutputStream.toByteArray(), httpRequest.charset()));
        } else {
            cordovaHttpResponse.setData(byteArrayOutputStream.toByteArray());
        }
    }
}
