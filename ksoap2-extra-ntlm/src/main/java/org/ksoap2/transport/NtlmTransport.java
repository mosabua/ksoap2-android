package org.ksoap2.transport;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.XmlPullParserException;

/**
 * A transport to be used with NTLM.
 *
 * Inspired by http://hc.apache.org/httpcomponents-client-ga/ntlm.html
 * @author Lian Hwang lian_hwang@hotmail.com
 * @author Manfred Moser <manfred@simpligity.com>
 */
public class NtlmTransport extends Transport {

    static final String ENCODING = "utf-8";

    private final DefaultHttpClient client = new DefaultHttpClient();
    private final HttpContext localContext = new BasicHttpContext();
    private String urlString;
    private String user;
    private String password;
    private String ntDomain;
    private String ntWorkstation;

    public void setCredentials(String url, String user, String password,
                               String domain, String workStation) {
        this.urlString = url;
        this.user = user;
        this.password = password;
        this.ntDomain = domain;
        this.ntWorkstation = workStation;

    }

    public List call(String targetNamespace, SoapEnvelope envelope, List headers)
            throws IOException, XmlPullParserException {
        return call(targetNamespace, envelope, headers, null);
    }

    public List call(String soapAction, SoapEnvelope envelope, List headers, File outputFile)
            throws IOException, XmlPullParserException {
        if (outputFile != null) {
            // implemented in HttpTransportSE if you are willing to port..
            throw new RuntimeException("Writing to file not supported");
        }
        HttpResponse resp = null;
        
        try {

            HttpPost httppost = new HttpPost(urlString);
            httppost.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
            NTCredentials ntc1 = new NTCredentials(user,password,null,"");

            client.getCredentialsProvider().setCredentials(AuthScope.ANY,ntc1);
            
            client.getAuthSchemes().register("ntlm", new NTLMSchemeFactory());
            setHeaders(soapAction, envelope, httppost, headers);
            
            resp = client.execute(httppost, localContext);
            HttpEntity respEntity = resp.getEntity();

            InputStream is = respEntity.getContent();
            Header[] retHeaders = null;
            int contentLength = 8192; // To determine the size of the response and adjust buffer size
            boolean gZippedContent = false;
            boolean xmlContent = false;
            int status = resp.getStatusLine().getStatusCode();

            try {
                retHeaders = resp.getAllHeaders();
                for (int i = 0; i < retHeaders.length; i++) {
                    Header hp = retHeaders[i];
                    // HTTP response code has null key
                    if (null == hp.getName()) {
                        continue;
                    }

                    // If we know the size of the response, we should use the size to initiate vars
                    if (hp.getName().equalsIgnoreCase("content-length") ) {
                        if ( hp.getValue() != null ) {
                            try {
                                contentLength = Integer.parseInt( hp.getValue() );
                            } catch ( NumberFormatException nfe ) {
                                contentLength = 8192;
                            }
                        }
                    }

                    // Check the content-type header to see if we're getting back XML, in case of a
                    // SOAP fault on 500 codes
                    if (hp.getName().equalsIgnoreCase("Content-Type")
                            && hp.getValue().contains("xml")) {
                        xmlContent = true;
                    }

                    // ignoring case since users found that all smaller case is used on some server
                    // and even if it is wrong according to spec, we rather have it work..
                    if (hp.getName().equalsIgnoreCase("Content-Encoding")
                         && hp.getValue().equalsIgnoreCase("gzip")) {
                        gZippedContent = true;
                    }
                }

                //first check the response code....
                if (status != 200) {
                    //throw new IOException("HTTP request failed, HTTP status: " + status);
                    throw new HttpResponseException(status, "HTTP request failed, HTTP status: " + status);
                }
                if (contentLength > 0) {
                    if (gZippedContent) {
                        is = getUnZippedInputStream(
                                new BufferedInputStream(is,contentLength));
                    } else {
                        is = new BufferedInputStream(is,contentLength);
                    }
                }
            } catch (IOException e) {
                if (contentLength > 0) {
                    if(gZippedContent && contentLength > 0) {
                        is = getUnZippedInputStream(
                                new BufferedInputStream(is,contentLength));
                    } else {
                        is = new BufferedInputStream(is,contentLength);
                    }
                }

                if ( e instanceof HttpResponseException) {
                    if (!xmlContent) {
                        if (debug && is != null) {
                            //go ahead and read the error stream into the debug buffers/file if needed.
                            readDebug(is, contentLength, outputFile);
                        }

                        //we never want to drop through to attempting to parse the HTTP error stream as a SOAP response.
                        
                        throw e;
                    }
                }
            }

            if (debug) {
                is = readDebug(is, contentLength, outputFile);
            }

            parseResponse(envelope, is);

        } catch (Exception ex) {
             ex.printStackTrace();
        }

        if (resp != null) {
            return Arrays.asList(resp.getAllHeaders());
        } else {
            return null;
        }
    }
    private InputStream readDebug(InputStream is, int contentLength, File outputFile) throws IOException {
        OutputStream bos;
        if (outputFile != null) {
            bos = new FileOutputStream(outputFile);
        } else {
            // If known use the size if not use default value
            bos = new ByteArrayOutputStream( (contentLength > 0 ) ? contentLength : 256*1024);
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
    private void setHeaders(String soapAction, SoapEnvelope envelope, HttpPost httppost, List headers) {
        byte[] requestData = null;
        try {
            requestData = createRequestData(envelope);
            requestDump = debug ? new String(requestData) : null;
            responseDump = null;

        } catch (IOException iOException) {
        }
        ByteArrayEntity byteArrayEntity = new ByteArrayEntity(requestData);
        httppost.setEntity(byteArrayEntity);
        httppost.addHeader("User-Agent", org.ksoap2.transport.Transport.USER_AGENT);
        // SOAPAction is not a valid header for VER12 so do not add
        // it
        // @see "http://code.google.com/p/ksoap2-android/issues/detail?id=67
        if (envelope.version != SoapSerializationEnvelope.VER12) {
            httppost.addHeader("SOAPAction", soapAction);
        }

        if (envelope.version == SoapSerializationEnvelope.VER12) {
            httppost.addHeader("Content-Type", Transport.CONTENT_TYPE_SOAP_XML_CHARSET_UTF_8);
        } else {
            httppost.addHeader("Content-Type", Transport.CONTENT_TYPE_XML_CHARSET_UTF_8);
        }

        // Pass the headers provided by the user along with the call
        if (headers != null) {
            for (int i = 0; i < headers.size(); i++) {
                HeaderProperty hp = (HeaderProperty) headers.get(i);
                httppost.addHeader(hp.getKey(), hp.getValue());
            }
        }
    }

    // Try to execute a cheap method first. This will trigger NTLM authentication
    public void setupNtlm(String dummyUrl, String userId, String password) {
        try {

           
            
            DefaultHttpClient httpclient = new DefaultHttpClient();
      
            HttpGet httpGet = new HttpGet(dummyUrl);
            httpGet.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
            NTCredentials ntc1 = new NTCredentials(userId,password,null,"");
   
            (httpclient).getCredentialsProvider().setCredentials(AuthScope.ANY,ntc1);
            httpclient.getAuthSchemes().register("ntlm", new NTLMSchemeFactory());
            HttpResponse response;
   
            response = httpclient.execute(httpGet);
            Header[] headers = response.getAllHeaders();
            StatusLine statusLine = response.getStatusLine();
            response.getEntity().consumeContent();
        } catch (Exception ex) {
            // swallow
         ex.printStackTrace();
        }
    }

        //NTLM Scheme factory
    
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
    public ServiceConnection getServiceConnection() throws IOException
    {
        throw new IOException("Not using ServiceConnection in transport");
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