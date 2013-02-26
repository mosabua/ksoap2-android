package org.ksoap2.transport;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;

/**
 * HttpsTransportSE is a simple transport for https protocal based connections. It creates a #HttpsServiceConnectionSE
 * with the provided parameters.
 *
 * @author Manfred Moser <manfred@simpligility.com>
 */
public class HttpsTransportSE extends HttpTransportSE{

    static final String PROTOCOL = "https";
    private static final String PROTOCOL_FULL = PROTOCOL + "://";

    private ServiceConnection serviceConnection = null;
    protected final String host;
    protected final int port;
    protected final String file;

    public HttpsTransportSE (String host, int port, String file, int timeout) {
        super(HttpsTransportSE.PROTOCOL_FULL + host + ":" + port + file);
        this.host = host;
        this.port = port;
        this.file = file;
        this.timeout = timeout;
    }

    /**
     * Creates instance of HttpTransportSE with set url and defines a
     * proxy server to use to access it
     *
     * @param proxy
     * Proxy information or <code>null</code> for direct access
     */
    public HttpsTransportSE(Proxy proxy, String host, int port, String file, int timeout) {
        super(proxy, HttpsTransportSE.PROTOCOL_FULL + host + ":" + port + file);
        this.host = host;
        this.port = port;
        this.file = file;
        this.timeout = timeout;
    }

    /**
     * Returns the HttpsServiceConnectionSE and creates it if necessary
     * @see org.ksoap2.transport.HttpsTransportSE#getServiceConnection()
     */
    public ServiceConnection getServiceConnection() throws IOException
    {
        if (serviceConnection == null) {
            serviceConnection = new HttpsServiceConnectionSE(proxy, host, port, file, timeout);
        }
        return serviceConnection;
    }

    public String getHost() {

        String retVal = null;

        try {
            retVal = new URL(url).getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return retVal;
    }

    public int getPort() {

        int retVal = -1;

        try {
            retVal = new URL(url).getPort();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return retVal;
    }

    public String getPath() {

        String retVal = null;

        try {
            retVal = new URL(url).getPath();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return retVal;
    }
}
