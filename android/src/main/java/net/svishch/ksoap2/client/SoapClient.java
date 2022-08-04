package net.svishch.ksoap2.client;


import net.svishch.ksoap2.CallbackSOAP;
import net.svishch.ksoap2.RecuestSOAP;
import net.svishch.ksoap2.client.okhttp.HttpClient;
import net.svishch.ksoap2.util.NewInstanceObject;
import okhttp3.Credentials;
import okhttp3.Headers;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;


public class SoapClient {
    private String url = "";
    private String username = "";
    private String password = "";
    private boolean debug;
    private UrlSettings urlSettings;
    private SoapSerializationEnvelope envelope   = new SoapSerializationEnvelope(SoapEnvelope.VER12);
    private OkHttp3Transport clientTransport;
    HttpClient httpClient;

    public SoapClient(UrlSettings urlSettings) {
        this.url = urlSettings.getUrl();
        this.username = urlSettings.getUser();
        this.password = urlSettings.getPassword();
        this.debug = urlSettings.isDebug();
        this.urlSettings = urlSettings;
        this.httpClient = new HttpClient(urlSettings);
        init();
    }

    private void init() {
        envelope.dotNet = true;
        envelope.implicitTypes = true;
        envelope.setAddAdornments(false);

        Headers headers = Headers.of("Authorization", getPass());

        //httpClient.getSoap(url,soapAction,callbackString);
        clientTransport = new OkHttp3Transport.Builder(this.url)
                .headers(headers)
                //.client(httpClient.getClient(this.url))
                .debug(this.debug)
                .build();
    }

    public void get(CallbackSOAP callback, RecuestSOAP recuestSOAP ) throws IOException, XmlPullParserException {
        callback.result(get(recuestSOAP));
    }

    public SoapObject get( RecuestSOAP recuestSOAP ) throws IOException, XmlPullParserException {

        envelope.setOutputSoapObject(recuestSOAP.getSoapObject());
        clientTransport.call(recuestSOAP.getSoapAction(), envelope);
        SoapObject result = (SoapObject) envelope.getResponse();

        return result;
    }

    public <T> T get(Object recuest , Class<T> response ) throws IOException, XmlPullParserException {

        /*
        RecuestSOAP recuestSOAP = null;
        envelope.setOutputSoapObject(recuestSOAP.getSoapObject());
        clientTransport.call(recuestSOAP.getSoapAction(), envelope);
        SoapObject result = (SoapObject) envelope.getResponse();
          return new FormSoap().formSoap(result,response);
*/
        T object = new NewInstanceObject().create(response);

        return (T) object;
    }

    private String getPass()  {
        String credentials = Credentials.basic(this.username, password);
        return credentials;
    }

}
