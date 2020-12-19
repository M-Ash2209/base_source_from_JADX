package com.silkimen.cordovahttp;

import android.app.Activity;
import android.content.res.AssetManager;
import android.util.Log;
import com.silkimen.http.TLSConfiguration;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import org.apache.cordova.CallbackContext;

class CordovaServerTrust implements Runnable {
    private static final String TAG = "Cordova-Plugin-HTTP";
    private Activity activity;
    private CallbackContext callbackContext;
    private String mode;
    private final TrustManager[] noOpTrustManagers = {new X509TrustManager() {
        public void checkClientTrusted(X509Certificate[] x509CertificateArr, String str) {
        }

        public void checkServerTrusted(X509Certificate[] x509CertificateArr, String str) {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }};
    private final HostnameVerifier noOpVerifier = new HostnameVerifier() {
        public boolean verify(String str, SSLSession sSLSession) {
            return true;
        }
    };
    private TLSConfiguration tlsConfiguration;

    public CordovaServerTrust(String str, Activity activity2, TLSConfiguration tLSConfiguration, CallbackContext callbackContext2) {
        this.mode = str;
        this.activity = activity2;
        this.tlsConfiguration = tLSConfiguration;
        this.callbackContext = callbackContext2;
    }

    public void run() {
        try {
            if ("legacy".equals(this.mode)) {
                this.tlsConfiguration.setHostnameVerifier((HostnameVerifier) null);
                this.tlsConfiguration.setTrustManagers((TrustManager[]) null);
            } else if ("nocheck".equals(this.mode)) {
                this.tlsConfiguration.setHostnameVerifier(this.noOpVerifier);
                this.tlsConfiguration.setTrustManagers(this.noOpTrustManagers);
            } else if ("pinned".equals(this.mode)) {
                this.tlsConfiguration.setHostnameVerifier((HostnameVerifier) null);
                this.tlsConfiguration.setTrustManagers(getTrustManagers(getCertsFromBundle("www/certificates")));
            } else {
                this.tlsConfiguration.setHostnameVerifier((HostnameVerifier) null);
                this.tlsConfiguration.setTrustManagers(getTrustManagers(getCertsFromKeyStore("AndroidCAStore")));
            }
            this.callbackContext.success();
        } catch (Exception e) {
            Log.e(TAG, "An error occured while configuring SSL cert mode", e);
            this.callbackContext.error("An error occured while configuring SSL cert mode");
        }
    }

    private TrustManager[] getTrustManagers(KeyStore keyStore) throws GeneralSecurityException {
        TrustManagerFactory instance = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        instance.init(keyStore);
        return instance.getTrustManagers();
    }

    private KeyStore getCertsFromBundle(String str) throws GeneralSecurityException, IOException {
        AssetManager assets = this.activity.getAssets();
        String[] list = assets.list(str);
        CertificateFactory instance = CertificateFactory.getInstance("X.509");
        KeyStore instance2 = KeyStore.getInstance(KeyStore.getDefaultType());
        instance2.load((InputStream) null, (char[]) null);
        for (int i = 0; i < list.length; i++) {
            int lastIndexOf = list[i].lastIndexOf(46);
            if (lastIndexOf != -1 && list[i].substring(lastIndexOf).equals(".cer")) {
                instance2.setCertificateEntry("CA" + i, instance.generateCertificate(assets.open(str + "/" + list[i])));
            }
        }
        return instance2;
    }

    private KeyStore getCertsFromKeyStore(String str) throws GeneralSecurityException, IOException {
        KeyStore instance = KeyStore.getInstance(str);
        instance.load((KeyStore.LoadStoreParameter) null);
        return instance;
    }
}
