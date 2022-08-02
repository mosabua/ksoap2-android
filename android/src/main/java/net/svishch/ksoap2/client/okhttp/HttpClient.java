package net.svishch.ksoap2.client.okhttp;


import net.svishch.ksoap2.client.UrlSettings;
import okhttp3.*;
import okhttp3.internal.http2.Http2Reader;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class HttpClient {


    private String username = "";
    private String password = "";
    private boolean debug = false;

    private OkHttpClient client;

    public HttpClient(UrlSettings urlSettings) {
        this.username = urlSettings.getUser();
        this.password = urlSettings.getPassword();
        this.debug = urlSettings.isDebug();

        init();
    }

    private void init() {
        if (debug) {
            client =  new OkHttpClient().newBuilder()
                    .addInterceptor(new LoggingInterceptor())
                    .build();
        } else {
            client = new OkHttpClient();
        }
    }

    public void get(String url, CallbackString callback ) throws IOException {
        String result = get(url);
        callback.result(result);
    }

    public String get(String url) throws IOException {
        Response response =  client
                .newCall(getNewCall(url))
                .execute();
       return response.body().string();
    }

    public OkHttpClient getClient(String url) {
        client.newCall(getNewCall(url));
        return client;
    }

    private Request getNewCall(String url){

        Request request  = new Request.Builder()
                .header("Authorization", getPass())
                .url(url)
                .build();
        return request;
    }

    public void getSoap(String url, String soapAction, CallbackString callback) throws IOException {

        Request request  = new Request.Builder()
                .header("Authorization", getPass())
                .header("SOAPAction", soapAction)
                .url(url)
                .build();


        Response response =  client
                .newCall(request)
                .execute();
        callback.result(response.body().string());

    }

    private class  LoggingInterceptor implements Interceptor {
        @NotNull
        @Override
        public Response intercept(@NotNull Chain chain) throws IOException {
            Request request  = chain.request();
            long t1 = System.nanoTime();

            String log1 =  String.format(
                    "Sending request %s on %s%n%s",
                    request.url(), chain.connection(), request.headers()
            );

            Response response = chain.proceed(request);
            long t2 = System.nanoTime();
            String log2 = String.format(
                    "Received response for %s in %.1fms%n%s",
                    response.request().url(), (t2 - t1) / 1e6, response.headers()
            );

            Http2Reader.Companion.getLogger().info(log1);
            Http2Reader.Companion.getLogger().info(log2);
            System.out.println(log1);
            System.out.println(log2);
            return response;
        }
    }

    private String getPass()  {
        String credentials = Credentials.basic(this.username, password);
        return credentials;
    }

}
