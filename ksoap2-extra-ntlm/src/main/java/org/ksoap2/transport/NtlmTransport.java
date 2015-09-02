package org.ksoap2.transport;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthSchemeFactory;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.auth.NTLMScheme;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.XmlPullParserException;

/**
 * A transport to be used with NTLM.
 *
 * Inspired by http://hc.apache.org/httpcomponents-client-ga/ntlm.html
 * 
 * @author Lian Hwang lian_hwang@hotmail.com
 * @author Manfred Moser <manfred@simpligity.com>
 */
public class NtlmTransport extends Transport {
	private String user;
	private String password;
	private String ntDomain;
	private String ntWorkstation;

	/**
	 * Creates instance of NtlmTransport with set url
	 * 
	 * @param url
	 *            the destination to POST SOAP data
	 */
	public NtlmTransport(String url) {
		super(url);
	}

	/**
	 * Creates instance of NtlmTransport with set url
	 * 
	 * @param url
	 *            the destination to POST SOAP data
	 * @param timeout
	 *            timeout for connection and Read Timeouts (milliseconds)
	 */
	public NtlmTransport(String url, int timeout) {
		super(url, timeout);
	}

	public void setCredentials(String user, String password, String domain, String workStation) {
		this.user = user;
		this.password = password;
		this.ntDomain = domain;
		this.ntWorkstation = workStation;
	}

	public List call(String targetNamespace, SoapEnvelope envelope, List headers) throws IOException, XmlPullParserException {
		return call(targetNamespace, envelope, headers, null);
	}

	public List call(String soapAction, SoapEnvelope envelope, List headers, File outputFile) throws IOException, XmlPullParserException {
		if (outputFile != null) {
			// implemented in HttpTransportSE if you are willing to port..
			throw new RuntimeException("Writing to file not supported");
		}

		HttpResponse response = null;

		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, this.timeout);

		HttpClient client = new DefaultHttpClient(httpParameters);

		((AbstractHttpClient) client).getAuthSchemes().register("ntlm", new NTLMSchemeFactory());
		NTCredentials credentials = new NTCredentials(user, password, ntWorkstation, ntDomain);
		((AbstractHttpClient) client).getCredentialsProvider().setCredentials(AuthScope.ANY, credentials);

		HttpPost httpPost = new HttpPost(url);
		setHeaders(soapAction, envelope, httpPost, headers);

		response = client.execute(httpPost);

		if (response == null) {
			throw new IOException("Null response");
		}

		if (response.getStatusLine().getStatusCode() == 401) {
			throw new RuntimeException("Unauthorized", new NtlmAuthenticationException());
		}

		HttpEntity responseEntity = response.getEntity();

		parseResponse(envelope, responseEntity.getContent());

		return Arrays.asList(response.getAllHeaders());
	}

	private void setHeaders(String soapAction, SoapEnvelope envelope, HttpPost httppost, List headers) {
		byte[] requestData = null;
		try {
			requestData = createRequestData(envelope);
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

	// NTLM Scheme factory
	private class NTLMSchemeFactory implements AuthSchemeFactory {
		public AuthScheme newInstance(final HttpParams params) {
			// see http://www.robertkuzma.com/2011/07/
			// manipulating-sharepoint-list-items-with-android-java-and-ntlm-authentication/
			return new NTLMScheme(new JCIFSEngine());
		}
	}

	public ServiceConnection getServiceConnection() throws IOException {
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
