/** 
 * Copyright (c) 2006, James Seigel, Calgary, AB., Canada
 * Copyright (c) 2003,2004, Stefan Haustein, Oberhausen, Rhld., Germany
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
 * IN THE SOFTWARE. 
 * Contributor(s): Paul Spencer, John D. Beatty, Dave Dash, F. Hunter, 
 *                  Renaud Tognelli, Thomas Strang, Alexander Krebs, Sean McDaniel
 */
package org.ksoap2.transport;

import java.io.*;

import org.ksoap2.serialization.Base64;

/**
 * An Http transport layer class which provides a mechanism to login to
 * webservices using Basic Authentication on midp platforms.
 * 
 * <i>Thanks to Paul Spencer</i>
 */
public class HttpTransportBasicAuth extends HttpTransport {
    private String username;
    private String password;
    /**
     * Constructor with username and password
     * 
     * @param url
     *            The url address of the webservice endpoint
     * @param username
     *            Username for the Basic Authentication challenge RFC 2617 
     * @param password
     *            Password for the Basic Authentication challenge RFC 2617
     */
    public HttpTransportBasicAuth(String url, String username, String password) {
        super(url);
        this.username = username;
        this.password = password;
    }

    public ServiceConnection getServiceConnection() throws IOException {
        ServiceConnectionMidp midpConnection = new ServiceConnectionMidp(url);
        addBasicAuthentication(midpConnection);
        return midpConnection;
    }

    protected void addBasicAuthentication(ServiceConnection midpConnection) throws IOException {
        if (username != null && password != null) {
            StringBuffer buf = new StringBuffer(username);
            buf.append(':').append(password);
            byte[] raw = buf.toString().getBytes();
            buf.setLength(0);
            buf.append("Basic ");
            buf.append(Base64.getEncoder().encodeToString(raw));
            midpConnection.setRequestProperty("Authorization", buf.toString());
        }
    }

}
