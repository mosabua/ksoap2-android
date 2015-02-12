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
 */

package org.ksoap2.transport;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.io.*;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;

import org.ksoap2.*;
import org.kxml2.io.*;
import org.xmlpull.v1.*;

/**
 * Abstract class which holds common methods and members that are used by the
 * transport layers. This class encapsulates the serialization and
 * deserialization of the soap messages, leaving the basic communication
 * routines to the subclasses.
 */
abstract public class Transport {

    /**
     * Added to enable web service interactions on the emulator to be debugged
     * with Fiddler2 (Windows) but provides utility for other proxy
     * requirements.
     */
    protected Proxy proxy;
    protected String url;
    protected int timeout = ServiceConnection.DEFAULT_TIMEOUT;
    /** Set to true if debugging */
    public boolean debug;
    /** String dump of request for debugging. */
    public String requestDump;
    /** String dump of response for debugging */
    public String responseDump;
    private String xmlVersionTag = "";

    protected static final String CONTENT_TYPE_XML_CHARSET_UTF_8 = "text/xml;charset=utf-8";
    protected static final String CONTENT_TYPE_SOAP_XML_CHARSET_UTF_8 = "application/soap+xml;charset=utf-8";
    protected static final String USER_AGENT = "ksoap2-android/2.6.0+";

    private int bufferLength = ServiceConnection.DEFAULT_BUFFER_SIZE;

    private HashMap prefixes = new HashMap();

    public HashMap getPrefixes() {
        return prefixes;
    }

    public Transport() {
    }

    public Transport(String url) {
        this(null, url);
    }

    public Transport(String url, int timeout) {
        this.url = url;
        this.timeout = timeout;
    }

    public Transport(String url, int timeout, int bufferLength) {
        this.url = url;
        this.timeout = timeout;
        this.bufferLength = bufferLength;
    }

    /**
     * Construct the transport object
     * 
     * @param proxy
     *            Specifies the proxy server to use for accessing the web
     *            service or <code>null</code> if a direct connection is
     *            available
     * @param url
     *            Specifies the web service url
     * 
     */
    public Transport(Proxy proxy, String url) {
        this.proxy = proxy;
        this.url = url;
    }

    public Transport(Proxy proxy, String url, int timeout) {
        this.proxy = proxy;
        this.url = url;
        this.timeout = timeout;
    }

    public Transport(Proxy proxy, String url, int timeout, int bufferLength) {
        this.proxy = proxy;
        this.url = url;
        this.timeout = timeout;
        this.bufferLength = bufferLength;
    }

    /**
     * Sets up the parsing to hand over to the envelope to deserialize.
     */
    protected void parseResponse(SoapEnvelope envelope, InputStream is)
            throws XmlPullParserException, IOException {
        XmlPullParser xp = new KXmlParser();
        xp.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
        xp.setInput(is, null);
        envelope.parse(xp);
        /*
         * Fix memory leak when running on android in strict mode. Issue 133
         */
        is.close();
    }

    /**
     * Serializes the request.
     */
    protected byte[] createRequestData(SoapEnvelope envelope, String encoding)
            throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(bufferLength);
        byte result[] = null;
        bos.write(xmlVersionTag.getBytes());
        XmlSerializer xw = new KXmlSerializer();

        final Iterator keysIter = prefixes.keySet().iterator();

        xw.setOutput(bos, encoding);
        while (keysIter.hasNext()) {
            String key = (String) keysIter.next();
            xw.setPrefix(key, (String) prefixes.get(key));
        }
        envelope.write(xw);
        xw.flush();
        bos.write('\r');
        bos.write('\n');
        bos.flush();
        result = bos.toByteArray();
        xw = null;
        bos = null;
        return result;
    }

    /**
     * Serializes the request.
     */
    protected byte[] createRequestData(SoapEnvelope envelope)
            throws IOException {
        return createRequestData(envelope, null);
    }

    /**
     * Set the target url.
     * 
     * @param url
     *            the target url.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl()
    {
        return url;
    }


    /**
     * Sets the version tag for the outgoing soap call. Example <?xml
     * version=\"1.0\" encoding=\"UTF-8\"?>
     * 
     * @param tag
     *            the xml string to set at the top of the soap message.
     */
    public void setXmlVersionTag(String tag) {
        xmlVersionTag = tag;
    }

    /**
     * Attempts to reset the connection.
     */
    public void reset() {
    }

    /**
     * Perform a soap call with a given namespace and the given envelope
     * providing any extra headers that the user requires such as cookies.
     * Headers that are returned by the web service will be returned to the
     * caller in the form of a <code>List</code> of <code>HeaderProperty</code>
     * instances.
     * 
     * @param soapAction
     *            the namespace with which to perform the call in.
     * @param envelope
     *            the envelope the contains the information for the call.
     * @param headers
     *            <code>List</code> of <code>HeaderProperty</code> headers to
     *            send with the SOAP request.
     * 
     * @return Headers returned by the web service as a <code>List</code> of
     *         <code>HeaderProperty</code> instances.
     */
    abstract public List call(String soapAction, SoapEnvelope envelope,
            List headers) throws IOException, XmlPullParserException;

    /**
     * Perform a soap call with a given namespace and the given envelope
     * providing any extra headers that the user requires such as cookies.
     * Headers that are returned by the web service will be returned to the
     * caller in the form of a <code>List</code> of <code>HeaderProperty</code>
     * instances.
     * 
     * @param soapAction
     *            the namespace with which to perform the call in.
     * @param envelope
     *            the envelope the contains the information for the call.
     * @param headers
     *            <code>List</code> of <code>HeaderProperty</code> headers to
     *            send with the SOAP request.
     * @param outputFile
     *            a file to stream the response into rather than parsing it,
     *            streaming happens when file is not null
     * 
     * @return Headers returned by the web service as a <code>List</code> of
     *         <code>HeaderProperty</code> instances.
     */
    abstract public List call(String soapAction, SoapEnvelope envelope,
            List headers, File outputFile) throws IOException,
            XmlPullParserException;

    /**
     * Perform a soap call with a given namespace and the given envelope.
     * 
     * @param soapAction
     *            the namespace with which to perform the call in.
     * @param envelope
     *            the envelope the contains the information for the call.
     */
    public void call(String soapAction, SoapEnvelope envelope)
            throws IOException, XmlPullParserException {
        call(soapAction, envelope, null);
    }

    /**
     * Return the name of the host that is specified as the web service target
     * 
     * @return Host name
     */
    public String getHost() throws MalformedURLException {

        return new URL(url).getHost();
    }

    /**
     * Return the port number of the host that is specified as the web service
     * target
     * 
     * @return Port number
     */
    public int getPort() throws MalformedURLException {

        return new URL(url).getPort();
    }

    /**
     * Return the path to the web service target
     * 
     * @return The URL's path
     */
    public String getPath() throws MalformedURLException {

        return new URL(url).getPath();
    }

    abstract public ServiceConnection getServiceConnection() throws IOException;
}
