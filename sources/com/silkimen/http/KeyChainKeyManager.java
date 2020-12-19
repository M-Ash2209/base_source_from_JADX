package com.silkimen.http;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509ExtendedKeyManager;

public class KeyChainKeyManager extends X509ExtendedKeyManager {
    private final String alias;
    private final X509Certificate[] chain;
    private final PrivateKey key;

    public KeyChainKeyManager(String str, PrivateKey privateKey, X509Certificate[] x509CertificateArr) {
        this.alias = str;
        this.key = privateKey;
        this.chain = x509CertificateArr;
    }

    public String chooseClientAlias(String[] strArr, Principal[] principalArr, Socket socket) {
        return this.alias;
    }

    public X509Certificate[] getCertificateChain(String str) {
        return this.chain;
    }

    public PrivateKey getPrivateKey(String str) {
        return this.key;
    }

    public final String chooseServerAlias(String str, Principal[] principalArr, Socket socket) {
        throw new UnsupportedOperationException();
    }

    public final String[] getClientAliases(String str, Principal[] principalArr) {
        throw new UnsupportedOperationException();
    }

    public final String[] getServerAliases(String str, Principal[] principalArr) {
        throw new UnsupportedOperationException();
    }
}
