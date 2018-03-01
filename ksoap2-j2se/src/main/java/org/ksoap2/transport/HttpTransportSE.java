/**
 * Copyright (c) 2003,2004, Stefan Haustein, Oberhausen, Rhld., Germany
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The  above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 * <p>
 * Contributor(s): John D. Beatty, Dave Dash, F. Hunter, Alexander Krebs,
 * Lars Mehrmann, Sean McDaniel, Thomas Strang, Renaud Tognelli
 */
package org.ksoap2.transport;

import okhttp3.*;
import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.XmlPullParserException;

import java.io.*;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

/**
 * A J2SE based HttpTransport layer.
 */
public class HttpTransportSE extends Transport {

    /**
     * Creates instance of HttpTransportSE with set url
     *
     * @param url the destination to POST SOAP data
     */
    public HttpTransportSE(String url) {
        super(null, url);
    }

    /**
     * Creates instance of HttpTransportSE with set url and defines a
     * proxy server to use to access it
     *
     * @param proxy Proxy information or <code>null</code> for direct access
     * @param url   The destination to POST SOAP data
     */
    public HttpTransportSE(Proxy proxy, String url) {
        super(proxy, url);
    }

    /**
     * Creates instance of HttpTransportSE with set url
     *
     * @param url     the destination to POST SOAP data
     * @param timeout timeout for connection and Read Timeouts (milliseconds)
     */
    public HttpTransportSE(String url, int timeout) {
        super(url, timeout);
    }

    public HttpTransportSE(Proxy proxy, String url, int timeout) {
        super(proxy, url, timeout);
    }

    /**
     * Creates instance of HttpTransportSE with set url
     *
     * @param url           the destination to POST SOAP data
     * @param timeout       timeout for connection and Read Timeouts (milliseconds)
     * @param contentLength Content Lenght in bytes if known in advance
     */
    public HttpTransportSE(String url, int timeout, int contentLength) {
        super(url, timeout);
    }

    public HttpTransportSE(Proxy proxy, String url, int timeout, int contentLength) {
        super(proxy, url, timeout);
    }

    /**
     * set the desired soapAction header field
     *
     * @param soapAction the desired soapAction
     * @param envelope   the envelope containing the information for the soap call.
     * @throws HttpResponseException
     * @throws IOException
     * @throws XmlPullParserException
     */
    public void call(String soapAction, SoapEnvelope envelope)
            throws HttpResponseException, IOException, XmlPullParserException {

        call(soapAction, envelope, null);
    }

    @Override
    public ServiceConnection getServiceConnection() throws IOException {
        return null; // NOT USING THIS
    }

    public List call(String soapAction, SoapEnvelope envelope, List headers)
            throws HttpResponseException, IOException, XmlPullParserException {
        return call(soapAction, envelope, headers, null);
    }

    /**
     * Perform a soap call with a given namespace and the given envelope providing
     * any extra headers that the user requires such as cookies. Headers that are
     * returned by the web service will be returned to the caller in the form of a
     * <code>List</code> of <code>HeaderProperty</code> instances.
     *
     * @param soapAction the namespace with which to perform the call in.
     * @param envelope   the envelope the contains the information for the call.
     * @param headers    <code>List</code> of <code>HeaderProperty</code> headers to send with the SOAP request.
     * @param outputFile a file to stream the response into rather than parsing it, streaming happens when file is not null
     * @return Headers returned by the web service as a <code>List</code> of
     * <code>HeaderProperty</code> instances.
     * @throws HttpResponseException an IOException when Http response code is different from 200
     */
    public List call(String soapAction, SoapEnvelope envelope, List headers, File outputFile)
            throws HttpResponseException, IOException, XmlPullParserException {

        HttpUrl httpUrl = HttpUrl.parse(url);

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(timeout, TimeUnit.MILLISECONDS)
                .readTimeout(timeout, TimeUnit.MILLISECONDS)
                .proxy(null == proxy ? Proxy.NO_PROXY : proxy)
                .build();

        return call(httpClient, soapAction, envelope, headers, outputFile);
    }

    public List call(OkHttpClient httpClient, String soapAction, SoapEnvelope envelope, List headers, File outputFile)
            throws HttpResponseException, IOException, XmlPullParserException {

        if (soapAction == null) {
            soapAction = "\"\"";
        }

        MediaType contentType;
        if (envelope.version == SoapSerializationEnvelope.VER12) {
            contentType = MediaType.parse(CONTENT_TYPE_SOAP_XML_CHARSET_UTF_8);
        } else {
            contentType = MediaType.parse(CONTENT_TYPE_XML_CHARSET_UTF_8);
        }

        byte[] requestData = createRequestData(envelope, "UTF-8");
        requestDump = debug ? new String(requestData) : null;
        responseDump = null;

        RequestBody body = RequestBody.create(contentType, requestData);

        Request.Builder builder = new Request.Builder()
                .url(url)
                .cacheControl(CacheControl.FORCE_NETWORK)
                .post(body);

        builder.addHeader("User-Agent", USER_AGENT);

        // SOAPAction is not a valid header for VER12 so do not add
        // it
        // @see "http://code.google.com/p/ksoap2-android/issues/detail?id=67
        if (envelope.version != SoapSerializationEnvelope.VER12) {
            builder.addHeader("SOAPAction", soapAction);
        }

        // this seems to cause issues so we are removing it
        //connection.setRequestProperty("Connection", "close");
        builder.addHeader("Accept-Encoding", "gzip");

        // Pass the headers provided by the user along with the call
        if (headers != null) {
            for (int i = 0; i < headers.size(); i++) {
                HeaderProperty hp = (HeaderProperty) headers.get(i);
                builder.addHeader(hp.getKey(), hp.getValue());
            }
        }

        Request request = builder.build();

        Call call = httpClient.newCall(request);
        Response response = call.execute();

        InputStream is = null;
        List<HeaderProperty> retHeaders = new ArrayList<HeaderProperty>();
        int contentLength = 8192; // To determine the size of the response and adjust buffer size
        boolean gZippedContent = false;
        boolean xmlContent = false;

        try {
            for (int i = 0; i < response.headers().size(); i++) {
                HeaderProperty hp = new HeaderProperty(response.headers().name(i), response.header(response.headers().name(i)));
                retHeaders.add(hp);

                // If we know the size of the response, we should use the size to initiate vars
                if (hp.getKey().equalsIgnoreCase("content-length")) {
                    if (hp.getValue() != null) {
                        try {
                            contentLength = Integer.parseInt(hp.getValue());
                        } catch (NumberFormatException nfe) {
                            contentLength = 8192;
                        }
                    }
                }


                // Check the content-type header to see if we're getting back XML, in case of a
                // SOAP fault on 500 codes
                if (hp.getKey().equalsIgnoreCase("Content-Type")
                        && hp.getValue().contains("xml")) {
                    xmlContent = true;
                }


                // ignoring case since users found that all smaller case is used on some server
                // and even if it is wrong according to spec, we rather have it work..
                if (hp.getKey().equalsIgnoreCase("Content-Encoding")
                        && hp.getValue().equalsIgnoreCase("gzip")) {
                    gZippedContent = true;
                }
            }

            //first check the response status....
            if (!response.isSuccessful()) {
                //202 is a correct status returned by WCF OneWay operation
                throw new HttpResponseException("HTTP request failed, HTTP status: " + response.code(), response.code(), retHeaders);
            }

            if (contentLength > 0) {
                if (gZippedContent) {
                    is = getUnZippedInputStream(
                            new BufferedInputStream(response.body().byteStream(), contentLength));
                } else {
                    is = new BufferedInputStream(response.body().byteStream(), contentLength);
                }
            }
        } catch (IOException e) {
            if (contentLength > 0) {
                if (gZippedContent) {
                    is = getUnZippedInputStream(
                            new BufferedInputStream(response.body().byteStream(), contentLength));
                } else {
                    is = new BufferedInputStream(response.body().byteStream(), contentLength);
                }
            }

            if (e instanceof HttpResponseException) {
                if (!xmlContent) {
                    if (debug && is != null) {
                        //go ahead and read the error stream into the debug buffers/file if needed.
                        readDebug(is, contentLength, outputFile);
                    }

                    throw e;
                }
            }
        } finally {
            if (debug) {
                is = readDebug(is, contentLength, outputFile);
            }

            if (is != null) {
                parseResponse(envelope, is, retHeaders);
                is.close();
                is = null;
            }
        }

        return retHeaders;
    }

    protected void parseResponse(SoapEnvelope envelope, InputStream is, List returnedHeaders)
            throws XmlPullParserException, IOException {
        parseResponse(envelope, is);
    }

    private InputStream readDebug(InputStream is, int contentLength, File outputFile) throws IOException {
        OutputStream bos;
        if (outputFile != null) {
            bos = new FileOutputStream(outputFile);
        } else {
            // If known use the size if not use default value
            bos = new ByteArrayOutputStream((contentLength > 0) ? contentLength : 256 * 1024);
        }

        byte[] buf = new byte[256];

        while (true) {
            int rd = is.read(buf, 0, 256);
            if (rd == -1) {
                break;
            }
            bos.write(buf, 0, rd);
        }

        bos.flush();
        if (bos instanceof ByteArrayOutputStream) {
            buf = ((ByteArrayOutputStream) bos).toByteArray();
        }
        bos = null;
        responseDump = new String(buf);
        is.close();

        if (outputFile != null) {
            return new FileInputStream(outputFile);
        } else {
            return new ByteArrayInputStream(buf);
        }
    }

    private InputStream getUnZippedInputStream(InputStream inputStream) throws IOException {
        /* workaround for Android 2.3 
           (see http://stackoverflow.com/questions/5131016/)
        */
        try {
            return (GZIPInputStream) inputStream;
        } catch (ClassCastException e) {
            return new GZIPInputStream(inputStream);
        }
    }
}
