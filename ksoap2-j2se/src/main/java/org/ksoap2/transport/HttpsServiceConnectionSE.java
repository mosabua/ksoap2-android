package org.ksoap2.transport;

import javax.net.ssl.HttpsURLConnection;

import org.ksoap2.HeaderProperty;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.List;
import java.util.Set;

/**
 * HttpsServiceConnectionSE is a service connection that uses a https url connection and requires explicit setting of
 * host, port and file.
 *
 * The explicit setting is necessary since pure url passing and letting the Java URL class parse the string does not
 * work properly on Android.
 *
 * Links for reference:
 * @see "http://stackoverflow.com/questions/2820284/ssl-on-android-strange-issue"
 * @see "http://stackoverflow.com/questions/2899079/custom-ssl-handling-stopped-working-on-android-2-2-froyo"
 * @see "http://code.google.com/p/android/issues/detail?id=2690"
 * @see "http://code.google.com/p/android/issues/detail?id=2764"
 *
 * @see "https://gist.github.com/908048" There can be problems with the
 * certificate of theof the server on older android versions. You can disable
 * SSL for the versions only e.g. with an approach like this.
 *
 * @author Manfred Moser <manfred@simpligility.com>
 */
public class HttpsServiceConnectionSE implements ServiceConnection {

    private HttpsURLConnection connection;

    /**
     * Create the transport with the supplied parameters.
     * @param host the name of the host e.g. webservices.somewhere.com
     * @param port the http port to connect on
     * @param file the path to the file on the webserver that represents the
     * webservice e.g. /api/services/myservice.jsp
     * @param timeout the timeout for the connection in milliseconds
     * @throws IOException
     */
    public HttpsServiceConnectionSE(String host, int port, String file,
                                     int timeout) throws IOException {
        connection = (HttpsURLConnection) new URL(HttpsTransportSE.PROTOCOL, host, port, file).openConnection();
        updateConnectionParameters(timeout);
    }

    private void updateConnectionParameters(int timeout) {
        connection.setConnectTimeout(timeout);
        connection.setReadTimeout(timeout); // even if we connect fine we want to time out if we cant read anything..
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setDoInput(true);
   }

    public void connect() throws IOException {
        connection.connect();
    }

    public void disconnect() {
        connection.disconnect();
    }

    public List getResponseProperties() {
        Map properties = connection.getHeaderFields();
        Set keys = properties.keySet();
        List retList = new LinkedList();

        for (Iterator i = keys.iterator(); i.hasNext();) {
            String key = (String) i.next();
            List values = (List) properties.get(key);

            for (int j = 0; j < values.size(); j++) {
                retList.add(new HeaderProperty(key, (String) values.get(j)));
            }
        }

        return retList;
    }

    public void setRequestProperty(String key, String value) {
        connection.setRequestProperty(key, value);
    }

    public void setRequestMethod(String requestMethod) throws IOException {
        connection.setRequestMethod(requestMethod);
    }

    public OutputStream openOutputStream() throws IOException {
        return connection.getOutputStream();
    }

    public InputStream openInputStream() throws IOException {
        return connection.getInputStream();
    }

    public InputStream getErrorStream() {
        return connection.getErrorStream();
    }

    public String getHost() {
        return connection.getURL().getHost();
    }

    public int getPort() {
        return connection.getURL().getPort();
    }

    public String getPath() {
        return connection.getURL().getPath();
    }

    public void setSSLSocketFactory(SSLSocketFactory sf) {
        connection.setSSLSocketFactory(SSLSocketFactory sf);
    }
}
