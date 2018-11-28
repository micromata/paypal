package de.micromata.paypal.http;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class MySSLSocketTestFactory extends SSLSocketFactory {
    private static int SocketCreated = 0;

    private static boolean initialized;

    SSLSocketFactory factory;

    static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        SSLSocketFactory fac = HttpsURLConnection.getDefaultSSLSocketFactory();
        HttpsURLConnection.setDefaultSSLSocketFactory(new MySSLSocketTestFactory(fac));

    }

    static void resetSocketCreated() {
        SocketCreated = 0;
    }

    static int getSocketCreated() {
        return SocketCreated;
    }

    @Override
    public Socket createSocket() throws IOException {
        ++SocketCreated;
        return factory.createSocket();
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort)
            throws IOException {
        ++SocketCreated;
        return factory.createSocket(address, port, localAddress, localPort);
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        ++SocketCreated;
        return factory.createSocket(host, port);
    }

    @Override
    public Socket createSocket(Socket s, InputStream consumed, boolean autoClose) throws IOException {
        ++SocketCreated;
        return factory.createSocket(s, consumed, autoClose);
    }

    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        ++SocketCreated;
        return factory.createSocket(s, host, port, autoClose);
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort)
            throws IOException, UnknownHostException {
        ++SocketCreated;
        return factory.createSocket(host, port, localHost, localPort);
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        ++SocketCreated;
        return factory.createSocket(host, port);
    }

    @Override
    public boolean equals(Object obj) {
        return factory.equals(obj);
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return factory.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return factory.getSupportedCipherSuites();
    }

    @Override
    public int hashCode() {
        return factory.hashCode();
    }

    @Override
    public String toString() {
        return factory.toString();
    }

    public MySSLSocketTestFactory(SSLSocketFactory fac) {
        this.factory = fac;
    }
}
