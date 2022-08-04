package net.svishch.ksoap2.client;

import net.svishch.ksoap2.HttpResponseException;
import okhttp3.*;
import org.ksoap2.SoapEnvelope;
import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OkHttp3Transport {
    private static final String DEFAULT_CHARSET = "UTF-8";
    public static final int DEFAULT_TIMEOUT = 20000;
    protected static final String USER_AGENT_PREFIX = "ksoap2-okhttp/3.6.3";
    private final String userAgent;
    private final OkHttpClient client;
    private final HttpUrl url;
    private final Headers headers;
    private Logger logger;
    private boolean debug = false;

    private OkHttp3Transport(Builder builder)
    {
        this.debug = builder.debug;

        if (builder.debug) {
            setDebug();
            this.logger = Logger.getLogger(OkHttp3Transport.class.getName());
            this.logger.setLevel(builder.debug ? Level.FINEST : Level.INFO);
        }

        okhttp3.OkHttpClient.Builder clientBuilder;
        if (null != builder.client) {
            clientBuilder = builder.client.newBuilder();
        } else {
            clientBuilder = new okhttp3.OkHttpClient.Builder();
        }

        clientBuilder.connectTimeout((long)builder.timeout, TimeUnit.MILLISECONDS).readTimeout((long)builder.timeout, TimeUnit.MILLISECONDS);
        if (null != builder.proxy) {
            clientBuilder.proxy(builder.proxy);
            if (null != builder.proxyAuthenticator) {
                clientBuilder.proxyAuthenticator(builder.proxyAuthenticator);
            }
        }

        if (null != builder.sslSocketFactory) {
            if (null == builder.trustManager) {
                throw new NullPointerException("TrustManager = null");
            }

            clientBuilder.sslSocketFactory(builder.sslSocketFactory, builder.trustManager);
        }

        if (null != builder.authenticator) {
            clientBuilder.authenticator(builder.authenticator);
        }

        this.client = clientBuilder.build();
        this.userAgent = this.buildUserAgent(builder);
        this.url = builder.url;
        this.headers = builder.headers;
    }

    private String buildUserAgent(Builder builder) {
        if (null != builder.userAgent) {
            return builder.userAgent;
        } else {
            String agent = System.getProperty("http.agent");
            if (null != agent) {
                Matcher m = Pattern.compile("(\\s\\(.*\\))").matcher(agent);
                if (m.find() && m.groupCount() > 0 && m.group(1).length() > 0) {
                    return "ksoap2-okhttp/3.6.3" + m.group(1);
                }
            }

            return "ksoap2-okhttp/3.6.3";
        }
    }

    public Headers call(String soapAction, SoapEnvelope envelope) throws IOException, XmlPullParserException {
        return this.call(soapAction, envelope, (Headers)null);
    }

    public Headers call(String soapAction, SoapEnvelope envelope, Headers headers) throws IOException, XmlPullParserException {
        if (soapAction == null) {
            soapAction = "\"\"";
        }

        sendLogger("SoapAction: " + soapAction);

        MediaType contentType;
        if (envelope.version == 120) {
            contentType = MediaType.parse("application/soap+xml;charset=utf-8");
        } else {
            contentType = MediaType.parse("text/xml;charset=utf-8");
        }

        sendLogger("ContentType: " + contentType);
        byte[] requestData = envelope.getRequestData();
        sendLoggerFinest("Request Payload: " + new String(requestData, "UTF-8"));


        RequestBody body = RequestBody.create(contentType, requestData);
        okhttp3.Request.Builder builder = (new okhttp3.Request.Builder()).url(this.url).cacheControl(CacheControl.FORCE_NETWORK).post(body);
        builder.addHeader("User-Agent", this.userAgent);
        if (envelope.version != 120) {
            builder.addHeader("SOAPAction", soapAction);
        }

        int i;
        if (null != this.headers) {
            for(i = 0; i < this.headers.size(); ++i) {
                builder.addHeader(this.headers.name(i), this.headers.value(i));
            }
        }

        if (null != headers) {
            for(i = 0; i < headers.size(); ++i) {
                builder.addHeader(headers.name(i), headers.value(i));
            }
        }

        Request request = builder.build();
        sendLogger("Request Headers: " + request.headers().toString());
        Response response = this.client.newCall(request).execute();
        ResponseBody responseBody = null;

        Headers var12;
        try {
            if (response == null) {
                throw new HttpResponseException("Null response.", -1);
            }

            responseBody = response.body();
            if (responseBody == null) {
                throw new HttpResponseException("Null response body.", response.code());
            }

            Headers responseHeaders = response.headers();
            sendLogger("Response Headers: " + responseHeaders.toString());
            sendLoggerFinest("Response Payload (max first 32KB): " + response.peekBody(32768L).string());

            if (!response.isSuccessful()) {
                throw new HttpResponseException("HTTP request failed, HTTP status: " + response.code(), response.code(), responseHeaders);
            }

            envelope.parse(responseBody.byteStream());
            var12 = responseHeaders;
        } catch (HttpResponseException var18) {
            if (null != responseBody) {
                try {
                    envelope.parse(responseBody.byteStream());
                } catch (XmlPullParserException var17) {
                }
            }

            throw var18;
        } finally {
            if (null != responseBody) {
                responseBody.close();
            }

        }

        return var12;
    }


    public static class Builder {
        private final HttpUrl url;
        private Proxy proxy = null;
        private int timeout = 20000;
        private String userAgent = null;
        private Headers headers = null;
        private OkHttpClient client = null;
        private SSLSocketFactory sslSocketFactory = null;
        private X509TrustManager trustManager = null;
        private Authenticator authenticator = null;
        private Authenticator proxyAuthenticator = null;
        private boolean debug = false;

        public Builder(HttpUrl url) {
            this.url = url;
        }

        public Builder(String url) {
            this.url = HttpUrl.parse(url);
        }

        public Builder client(OkHttpClient client) {
            this.client = client;
            return this;
        }

        public Builder proxy(Proxy proxy) {
            this.proxy = proxy;
            return this;
        }

        public Builder timeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public Builder headers(Headers headers) {
            this.headers = headers;
            return this;
        }

        public Builder sslSocketFactory(SSLSocketFactory sslSocketFactory, X509TrustManager trustManager) {
            this.sslSocketFactory = sslSocketFactory;
            this.trustManager = trustManager;
            return this;
        }

        public Builder authenticator(Authenticator authenticator) {
            this.authenticator = authenticator;
            return this;
        }

        public Builder proxyAuthenticator(Authenticator authenticator) {
            this.proxyAuthenticator = authenticator;
            return this;
        }

        public Builder debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public OkHttp3Transport build() {
            return new OkHttp3Transport(this);
        }
    }

    private void sendLogger(String mess) {
        if (this.debug) {
            this.logger.fine(mess);
        }
    }

    private void sendLoggerFinest(String mess) {
        if (this.debug && this.logger.getLevel().intValue() <= Level.FINEST.intValue()) {
            this.logger.finest(mess);
        }
    }


    private void setDebug() {

        InputStream stream = null;

        try {
            stream = OkHttp3Transport.class.getResourceAsStream("logging.properties");
            if (null == stream) {
                stream = OkHttp3Transport.class.getClassLoader().getResourceAsStream("logging.properties");
            }

            if (null != stream) {
                LogManager.getLogManager().readConfiguration(stream);
            } else {
                System.err.println("Couldn't find logger configuration.");
            }
        } catch (IOException var10) {
            System.err.println("Couldn't read logger configuration.");
            var10.printStackTrace();
        } finally {
            if (null != stream) {
                try {
                    stream.close();
                } catch (IOException var9) {
                }
            }

        }

    }
}
