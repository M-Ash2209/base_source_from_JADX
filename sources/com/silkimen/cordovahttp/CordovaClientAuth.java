package com.silkimen.cordovahttp;

import android.app.Activity;
import android.content.Context;
import android.security.KeyChain;
import android.security.KeyChainAliasCallback;
import android.util.Log;
import com.silkimen.http.KeyChainKeyManager;
import com.silkimen.http.TLSConfiguration;
import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.Principal;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import org.apache.cordova.CallbackContext;

class CordovaClientAuth implements Runnable, KeyChainAliasCallback {
    private static final String TAG = "Cordova-Plugin-HTTP";
    private Activity activity;
    private String aliasString;
    private CallbackContext callbackContext;
    private Context context;
    private String mode;
    private String pkcsPassword;
    private byte[] rawPkcs;
    private TLSConfiguration tlsConfiguration;

    public CordovaClientAuth(String str, String str2, byte[] bArr, String str3, Activity activity2, Context context2, TLSConfiguration tLSConfiguration, CallbackContext callbackContext2) {
        this.mode = str;
        this.aliasString = str2;
        this.rawPkcs = bArr;
        this.pkcsPassword = str3;
        this.activity = activity2;
        this.tlsConfiguration = tLSConfiguration;
        this.context = context2;
        this.callbackContext = callbackContext2;
    }

    public void run() {
        if ("systemstore".equals(this.mode)) {
            loadFromSystemStore();
        } else if ("buffer".equals(this.mode)) {
            loadFromBuffer();
        } else {
            disableClientAuth();
        }
    }

    private void loadFromSystemStore() {
        String str = this.aliasString;
        if (str == null) {
            KeyChain.choosePrivateKeyAlias(this.activity, this, (String[]) null, (Principal[]) null, (String) null, -1, (String) null);
        } else {
            alias(str);
        }
    }

    private void loadFromBuffer() {
        try {
            KeyStore instance = KeyStore.getInstance("PKCS12");
            KeyManagerFactory instance2 = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            instance.load(new ByteArrayInputStream(this.rawPkcs), this.pkcsPassword.toCharArray());
            instance2.init(instance, this.pkcsPassword.toCharArray());
            this.tlsConfiguration.setKeyManagers(instance2.getKeyManagers());
            this.callbackContext.success();
        } catch (Exception e) {
            Log.e(TAG, "Couldn't load given PKCS12 container for authentication", e);
            this.callbackContext.error("Couldn't load given PKCS12 container for authentication");
        }
    }

    private void disableClientAuth() {
        this.tlsConfiguration.setKeyManagers((KeyManager[]) null);
        this.callbackContext.success();
    }

    public void alias(String str) {
        if (str != null) {
            try {
                KeyChainKeyManager keyChainKeyManager = new KeyChainKeyManager(str, KeyChain.getPrivateKey(this.context, str), KeyChain.getCertificateChain(this.context, str));
                this.tlsConfiguration.setKeyManagers(new KeyManager[]{keyChainKeyManager});
                this.callbackContext.success(str);
            } catch (Exception e) {
                Log.e(TAG, "Couldn't load private key and certificate pair with given alias \"" + str + "\" for authentication", e);
                CallbackContext callbackContext2 = this.callbackContext;
                callbackContext2.error("Couldn't load private key and certificate pair with given alias \"" + str + "\" for authentication");
            }
        } else {
            throw new Exception("Couldn't get a consent for private key access");
        }
    }
}
