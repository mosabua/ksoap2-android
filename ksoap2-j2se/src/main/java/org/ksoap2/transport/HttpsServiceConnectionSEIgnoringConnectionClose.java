package org.ksoap2.transport;

import java.io.IOException;

class HttpsServiceConnectionSEIgnoringConnectionClose extends HttpsServiceConnectionSE {

    public HttpsServiceConnectionSEIgnoringConnectionClose(String host, int port, String file, int timeout)
            throws IOException {
        super(host, port, file, timeout);
    }

    //@Override
    public void setRequestProperty(String key, String value) {
        // We want to ignore any setting of "Connection: close" because
        // it is buggy with Android SSL.
        if ("Connection".equalsIgnoreCase(key) && "close".equalsIgnoreCase(value)) {
            return;
        } else {
            super.setRequestProperty(key, value);
        }
    }
}
