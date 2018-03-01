package org.ksoap2.transport;

import okhttp3.OkHttpClient;
import org.ksoap2.SoapEnvelope;
import org.xmlpull.v1.XmlPullParserException;

import java.io.*;
import java.net.Proxy;
import java.util.List;

/**
 * A transport to be used with NTLM.
 * <p>
 * Inspired by http://hc.apache.org/httpcomponents-client-ga/ntlm.html
 *
 * @author Lian Hwang lian_hwang@hotmail.com
 * @author Manfred Moser <manfred@simpligity.com>
 */
public class NtlmTransport extends HttpTransportSE {
    private String user;
    private String password;
    private String ntDomain;
    private String ntWorkstation;

    /**
     * Creates instance of NtlmTransport with set url
     *
     * @param url the destination to POST SOAP data
     */
    public NtlmTransport(String url) {
        super(Proxy.NO_PROXY, url);
    }

    /**
     * Creates instance of NtlmTransport with set url and defines a
     * proxy server to use to access it
     *
     * @param proxy Proxy information or <code>Proxy.NO_PROXY</code> for direct access
     * @param url   The destination to POST SOAP data
     */
    public NtlmTransport(Proxy proxy, String url) {
        super(proxy, url);
    }

    /**
     * Creates instance of NtlmTransport with set url
     *
     * @param url     the destination to POST SOAP data
     * @param timeout timeout for connection and Read Timeouts (milliseconds)
     */
    public NtlmTransport(String url, int timeout) {
        super(url, timeout);
    }

    /**
     * Creates instance of NtlmTransport with set url
     *
     * @param proxy   Proxy information or <code>Proxy.NO_PROXY</code> for direct access
     * @param url     the destination to POST SOAP data
     * @param timeout timeout for connection and Read Timeouts (milliseconds)
     */
    public NtlmTransport(Proxy proxy, String url, int timeout) {
        super(proxy, url, timeout);
    }

    /**
     * Creates instance of NtlmTransport with set url
     *
     * @param url           the destination to POST SOAP data
     * @param timeout       timeout for connection and Read Timeouts (milliseconds)
     * @param contentLength Content Lenght in bytes if known in advance
     */
    public NtlmTransport(String url, int timeout, int contentLength) {
        super(url, timeout);
    }

    /**
     * Creates instance of NtlmTransport with set url
     *
     * @param proxy         Proxy information or <code>Proxy.NO_PROXY</code> for direct access
     * @param url           the destination to POST SOAP data
     * @param timeout       timeout for connection and Read Timeouts (milliseconds)
     * @param contentLength Content Lenght in bytes if known in advance
     */
    public NtlmTransport(Proxy proxy, String url, int timeout, int contentLength) {
        super(proxy, url, timeout);
    }

    @Override
    public List call(OkHttpClient client, String soapAction, SoapEnvelope envelope, List headers, File outputFile)
            throws HttpResponseException, IOException, XmlPullParserException {
        client = client.newBuilder()
                .authenticator(new NtlmAuthenticator(user, password, ntDomain, ntWorkstation))
                .build();

        return super.call(client, soapAction, envelope, headers, outputFile);
    }

    public void setCredentials(String user, String password, String domain, String workStation) {
        this.user = user;
        this.password = password;
        this.ntDomain = domain;
        this.ntWorkstation = workStation;
    }

}
