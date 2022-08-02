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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Interface to allow the abstraction of the raw transport information
 */
public interface ServiceConnection {

    public static final int DEFAULT_TIMEOUT = 20000; // 20 seconds
    public static final int DEFAULT_BUFFER_SIZE = 256*1024; // 256 Kb

    /**
     * Make an outgoing connection.
     * 
     * @exception IOException
     */
    public void connect() throws IOException;

    /**
     * Disconnect from the outgoing connection
     * 
     * @exception IOException
     */
    public void disconnect() throws IOException;

    /**
     * Returns to the caller all of the headers that were returned with the
     * response to the SOAP request. Primarily this gives the caller an 
     * opportunity to save the cookies for later use.
     * 
     * @return List of HeaderProperty instances that were returned as part of the http response as http header
     * properties
     * 
     * @exception IOException
     */
    public List getResponseProperties() throws IOException;

    /**
     * Returns the numerical HTTP status to the caller
     * @return an integer status value
     * @throws IOException
     */
    public int getResponseCode() throws IOException;

    /**
     * Set properties on the outgoing connection.
     * 
     * @param propertyName
     *            the name of the property to set. For HTTP connections these
     *            are the request properties in the HTTP Header.
     * @param value
     *            the string to set the property header to.
     * @exception IOException
     */
    public void setRequestProperty(String propertyName, String value) throws IOException;

    /**
     * Sets how to make the requests. For HTTP this is typically POST or GET.
     * 
     * @param requestMethodType
     *            the type of request method to make the soap call with.
     * @exception IOException
     */
    public void setRequestMethod(String requestMethodType) throws IOException;

    /**
     * If the length of a HTTP request body is known ahead, sets fixed length 
     * to enable streaming without buffering. Sets after connection will cause an exception.
     *
     * @param contentLength the fixed length of the HTTP request body
     * @see http://developer.android.com/reference/java/net/HttpURLConnection.html
     **/
    public void setFixedLengthStreamingMode(int contentLength);

    public void setChunkedStreamingMode();

    /**
     * Open and return the outputStream to the endpoint.
     * 
     * @exception IOException
     * @return the output stream to write the soap message to.
     */
    public OutputStream openOutputStream() throws IOException;

    /**
     * Opens and returns the inputstream from which to parse the result of the
     * soap call.
     * 
     * @exception IOException
     * @return the inputstream containing the xml to parse the result from the
     *         call from.
     */
    public InputStream openInputStream() throws IOException;

    /**
     * @return the error stream for the call.
     */
    public InputStream getErrorStream();

    /**
     * Return the name of the host that is specified as the web service target
     *
     * @return Host name
     */
    abstract public String getHost();

    /**
     * Return the port number of the host that is specified as the web service target
     *
     * @return Port number
     */
    abstract public int getPort();

    /**
     * Return the path to the web service target
     *
     * @return The URL's path
     */
    abstract public String getPath();
}
