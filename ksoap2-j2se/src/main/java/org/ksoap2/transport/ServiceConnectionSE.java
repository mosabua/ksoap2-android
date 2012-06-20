/* Copyright (c) 2003,2004, Stefan Haustein, Oberhausen, Rhld., Germany
 * Copyright (c) 2006, James Seigel, Calgary, AB., Canada
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The  above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE. */

package org.ksoap2.transport;

import java.io.*;
import java.net.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ksoap2.HeaderProperty;

/**
 * Connection for J2SE environments.
 */
public class ServiceConnectionSE implements ServiceConnection {

    private HttpURLConnection connection;

    /**
     * Constructor taking the url to the endpoint for this soap communication
     * @param url the url to open the connection to.
     * @throws IOException
     */
    public ServiceConnectionSE(String url) throws IOException {
        this(null, url, ServiceConnection.DEFAULT_TIMEOUT);
    }

    public ServiceConnectionSE(Proxy proxy, String url) throws IOException {
        this(proxy, url, ServiceConnection.DEFAULT_TIMEOUT);
    }

    /**
     * Constructor taking the url to the endpoint for this soap communication
     * @param url the url to open the connection to.
     * @param timeout the connection and read timeout for the http connection in milliseconds
     * @throws IOException                            // 20 seconds
     */
    public ServiceConnectionSE(String url, int timeout) throws IOException {
        this(null, url, timeout);
    }

    public ServiceConnectionSE(Proxy proxy, String url, int timeout) throws IOException {
        connection = (proxy == null)
            ? (HttpURLConnection) new URL(url).openConnection()
            : (HttpURLConnection) new URL(url).openConnection(proxy);
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setConnectTimeout(timeout);
        connection.setReadTimeout(timeout); // even if we connect fine we want to time out if we cant read anything..
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

    public void setRequestProperty(String string, String soapAction) {
        connection.setRequestProperty(string, soapAction);
    }

    public void setRequestMethod(String requestMethod) throws IOException {
        connection.setRequestMethod(requestMethod);
    }

    public void setFixedLengthStreamingMode(int contentLength) {
        connection.setFixedLengthStreamingMode(contentLength);
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
}
