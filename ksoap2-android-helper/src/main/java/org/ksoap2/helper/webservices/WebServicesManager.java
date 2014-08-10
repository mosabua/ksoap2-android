/* The MIT License (MIT)
 *
 *Copyright (c) 2014 Omar Hussain
 *
 *Permission is hereby granted, free of charge, to any person obtaining a copy
 *of this software and associated documentation files (the "Software"), to deal
 *in the Software without restriction, including without limitation the rights
 *to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *copies of the Software, and to permit persons to whom the Software is
 *furnished to do so, subject to the following conditions:
 *
 *The above copyright notice and this permission notice shall be included in
 *all copies or substantial portions of the Software.
 *
 *THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *THE SOFTWARE.
 * 
 *
 * */

package org.ksoap2.helper.webservices;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kobjects.base64.Base64;
import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapFault;
import org.ksoap2.helper.caching.SoapCacheManager;
import org.ksoap2.helper.callbacks.IGetDataCallback;
import org.ksoap2.helper.enums.SoapRequestCachePolicy;
import org.ksoap2.helper.objects.SoapRequest;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.transport.NtlmTransport;
import org.ksoap2.transport.Transport;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.util.Log;

/**
 * Helper class to perform SOAP requests asynchronously 
 * and return response to calling class via @IGetDataCallback callbacks
 * @author omarhussain
 *
 */

public class WebServicesManager {

    public WebServicesManager() {
    }

    private final NtlmTransport getNtlmTransport(String url, String username, String password){
        NtlmTransport httpTransport = new NtlmTransport();
        httpTransport.setCredentials(url, username , password, "ntDomain", "ws");
        httpTransport.debug = true;
        return httpTransport;
    }
    private final HttpTransportSE getHttpTransportSE(String url, String username, String password) {

        HttpTransportSE ht = new HttpTransportSE(Proxy.NO_PROXY, url, 10000);
        if(username != null && password != null){
        String login = Base64.encode((username + ":" + password).getBytes());
        try {
            ht.getServiceConnection().setRequestProperty("Authorization", "Basic " + login);
        } catch (IOException e) {

            e.printStackTrace();
        }
        }
        ht.debug = true;
        ht.setXmlVersionTag("<?xml version=\"1.0\" encoding= \"UTF-8\" ?>");
        return ht;
    }
    public boolean isConnectedToInternet(Context context){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;

    }
    public void processRequest(SoapRequest request, String uname, String pwd, Context ctx, IGetDataCallback cb) {
        if(isConnectedToInternet(ctx)){
            new ProcessRequestAsync(uname,pwd,ctx,cb).execute(request);
        }else{
            new AlertDialog.Builder(ctx)
            .setTitle("Notice")
            .setMessage("You are currently offline.")
            .setCancelable(false)
            .setPositiveButton("ok", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).create().show();                       
            cb.onGetData(request, null);  
        }
    }

    public class ProcessRequestAsync extends
    android.os.AsyncTask<SoapRequest, Void, Object> {
        Context context;
        IGetDataCallback callback;
        SoapRequest requestObject;
        String username;
        String password;
        public ProcessRequestAsync(String uname, String pwd, Context ctx,IGetDataCallback cb) {
            if(ctx == null){
                throw new NullPointerException("Context cannot be null.");
            }
            this.username = uname;
            this.password = pwd;
            this.context = ctx;
            this.callback = cb;

        }

        @Override
        protected void onPreExecute() {

        }
        @Override
        protected void onProgressUpdate(Void...result){

        }
        @Override
        protected void onPostExecute(Object result) {
            Intent activityIntent = new Intent();
            activityIntent.setAction("org.ksoap2.ServicesActivityIntent");
            activityIntent.putExtra("processing",false);
            context.sendBroadcast(activityIntent);
            this.callback.onGetData(requestObject,result);
        }

        @Override
        protected Object doInBackground(SoapRequest... params) {
            Intent activityIntent = new Intent();
            activityIntent.setAction("org.ksoap2.ServicesActivityIntent");
            activityIntent.putExtra("processing",true);
            context.sendBroadcast(activityIntent);
            SoapRequest request = params[0];
            requestObject = request;
            Object data = null;
            SoapSerializationEnvelope envelope = request.getSoapSerializationEnvelope();
            envelope.dotNet = true;
            envelope.skipNullProperties = true;
            envelope.implicitTypes = true;
            String requestString = SoapCacheManager.serializeSoapRequest(envelope);
            Log.d("SoapKit", "Request:" +requestString);
            String requestStringHash = requestString
                    .hashCode() + "";
            File requestFile = this.context
                    .getFileStreamPath(requestStringHash);
            if (request.policy == SoapRequestCachePolicy.SCASoapRequestCachePolicyCacheOnly 
                    || request.policy == SoapRequestCachePolicy.SCASoapRequestCachePolicyCacheFirst) {
                if (requestFile.exists()) {
                    try {
                        // convert String into InputStream
                        String requestStringCached = SoapCacheManager.readFile(requestFile).toString();
                        InputStream is = new ByteArrayInputStream(
                                requestStringCached.getBytes());
                        Log.d("SoapKit", "Response:" +requestStringCached);
                        return SoapCacheManager.parseSoapResponse(envelope, is);

                    } catch (Exception e) {
                        if(request.policy == SoapRequestCachePolicy.SCASoapRequestCachePolicyCacheOnly){
                            return null;
                        }
                    }

                }
            }
            Transport ht = null;
            if(username != null && password != null){
                getNtlmTransport(request.getServiceUrl(),username,password);
            }else{
                getHttpTransportSE(request.getServiceUrl(),username,password);
            }
            try {
                List<HeaderProperty> headers = new ArrayList<HeaderProperty>();
                /* Add your custom headers */
                headers.add(new HeaderProperty("SOAPAction", request
                        .getSoapAction()));
                Log.d("SoapKit:Timestamp Before", new Date().toLocaleString());
                ht.call(request.getSoapAction(), envelope, headers);
                Log.d("SoapKit:Timestamp After", new Date().toLocaleString());
                String resp = ht.responseDump;
                Log.d("SoapKit", "Response:" +resp);
                SoapCacheManager.writeFile(requestFile, resp);
                try{
                    data = envelope.getResponse();
                }catch(SoapFault fault){
                    data = fault;
                }
                data = envelope.bodyIn;

            } catch (Exception t) {
                t.printStackTrace();
                if (request.policy != SoapRequestCachePolicy.SCASoapRequestCachePolicyNetworkOnly) {
                    if (requestFile.exists()) {
                        try {
                            // convert String into InputStream
                            String requestStringCached = SoapCacheManager.readFile(requestFile).toString();
                            InputStream is = new ByteArrayInputStream(
                                    requestStringCached.getBytes());
                            Log.d("SoapKit", "Response:" +requestStringCached);
                            return SoapCacheManager.parseSoapResponse(envelope, is);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
            return data;

        }


    }

}
