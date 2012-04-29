/**
 * Digi-Key
 * http://www.digikey.com
 *
 * Copyright 2010 Logicopolis Technology Inc. All rights reserved.
 * http://www.logicopolis.com
 */

package org.ksoap2.transport;

import java.io.IOException;

/**
 * KeepAliveHttpsTransport deals with the problems with the Android ssl libraries having trouble with certificates and
 * certificate authorities somehow messing up connecting/needing reconnects. Added as generic class for SE since it
 * might be useful in SE environments as well and can be used as an example to create your own transport
 * implementations.
 *
 * @author Manfred Moser <manfred@simpligility.com>
 *
 * @see "http://groups.google.com/group/android-developers/browse_thread/thread/3dcf62e7886a213/21f912bb90a011d6"
 * @see "http://code.google.com/p/android/issues/detail?id=7074"
 * @see "http://crazybob.org/2010_02_01_crazyboblee_archive.html"
 */
public class KeepAliveHttpsTransportSE extends HttpsTransportSE
{
    private HttpsServiceConnectionSE conn = null;
    private final String host;
    private final int port;
    private final String file;
    private final int timeout;

    public KeepAliveHttpsTransportSE (String host, int port, String file, int timeout) {
        super(host, port, file, timeout);
        this.host = host;
        this.port = port;
        this.file = file;
        this.timeout = timeout;
    }

    /**
     * Get a service connection. Returns an implementation of {@link org.ksoap2.transport.ServiceConnectionSE} that
     * ignores "Connection: close" request property setting and has "Connection: keep-alive" always set and is uses
     * a https connection.
     * @see org.ksoap2.transport.HttpTransportSE#getServiceConnection()
     */
    //@Override
    protected ServiceConnection getServiceConnection() throws IOException
    {
        conn = new HttpsServiceConnectionSEIgnoringConnectionClose(host, port, file, timeout);
        conn.setRequestProperty("Connection", "keep-alive");
        return conn;
    }

}
